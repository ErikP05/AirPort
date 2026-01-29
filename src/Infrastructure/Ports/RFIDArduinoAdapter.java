package Infrastructure.Ports;

import com.fazecast.jSerialComm.SerialPort;
import Infrastructure.AppException;
import Infrastructure.Tools.CMD;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Adaptador para comunicación con Arduino RFID via puerto serial
 * Implementa operaciones: READ, WRITE, UPDATE, DELETE
 */
public class RFIDArduinoAdapter implements RFIDPorts {

    private SerialPort serialPort;
    private InputStream inputStream;
    private OutputStream outputStream;
    private boolean connected = false;

    private String portName;
    private int baudRate;
    private int timeout;

    public RFIDArduinoAdapter(String portName, int baudRate, int timeout) {
        this.portName = portName;
        this.baudRate = baudRate;
        this.timeout = timeout;
    }

    @Override
    public void connect() {
        try {
            // Buscar el puerto especificado
            SerialPort[] ports = SerialPort.getCommPorts();
            for (SerialPort port : ports) {
                if (port.getSystemPortName().equals(portName)) {
                    serialPort = port;
                    break;
                }
            }

            if (serialPort == null) {
                throw new AppException("Puerto " + portName + " no encontrado");
            }

            // Configurar puerto
            serialPort.setComPortParameters(baudRate, 8, 1, 0);
            serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, timeout, 0);

            // Abrir puerto
            if (serialPort.openPort()) {
                inputStream = serialPort.getInputStream();
                outputStream = serialPort.getOutputStream();
                connected = true;

                // Esperar que Arduino se inicialice
                Thread.sleep(2000);

                // Leer mensaje READY del Arduino
                String response = readResponse();
                CMD.println("Arduino conectado: " + response);
            } else {
                throw new AppException("No se pudo abrir el puerto " + portName);
            }

        } catch (Exception e) {
            connected = false;
            throw new RuntimeException("Error al conectar con Arduino: " + e.getMessage(), e);
        }
    }

    @Override
    public String readUID() {
        if (!connected) {
            throw new RuntimeException("Arduino no conectado");
        }

        try {
            // Enviar comando READ
            sendCommand("READ");

            // Leer respuesta
            String response = readResponse();

            if (response.startsWith("UID:")) {
                return response.substring(4).trim();
            } else if (response.startsWith("ERROR:")) {
                throw new AppException(response.substring(6));
            }

            throw new AppException("Respuesta inesperada: " + response);

        } catch (Exception e) {
            throw new RuntimeException("Error al leer UID: " + e.getMessage(), e);
        }
    }

    /**
     * Escribe datos de pasajero en la tarjeta NFC
     * 
     * @param idPasajero ID del pasajero
     * @param nombre     Nombre del pasajero
     * @param cedula     Cédula del pasajero
     * @return true si se escribió correctamente
     */
    public boolean writeCard(int idPasajero, String nombre, String cedula) {
        if (!connected) {
            throw new RuntimeException("Arduino no conectado");
        }

        try {
            // Formato: WRITE:IdPasajero|Nombre|Cedula
            String command = String.format("WRITE:%d|%s|%s", idPasajero, nombre, cedula);
            sendCommand(command);

            // Leer respuesta
            String response = readResponse();

            if (response.equals("OK")) {
                return true;
            } else if (response.startsWith("ERROR:")) {
                throw new AppException(response.substring(6));
            }

            return false;

        } catch (Exception e) {
            throw new RuntimeException("Error al escribir tarjeta: " + e.getMessage(), e);
        }
    }

    /**
     * Actualiza datos de pasajero en la tarjeta NFC
     * 
     * @param idPasajero ID del pasajero
     * @param nombre     Nombre del pasajero
     * @param cedula     Cédula del pasajero
     * @return true si se actualizó correctamente
     */
    public boolean updateCard(int idPasajero, String nombre, String cedula) {
        if (!connected) {
            throw new RuntimeException("Arduino no conectado");
        }

        try {
            // Formato: UPDATE:IdPasajero|Nombre|Cedula
            String command = String.format("UPDATE:%d|%s|%s", idPasajero, nombre, cedula);
            sendCommand(command);

            // Leer respuesta
            String response = readResponse();

            if (response.equals("OK")) {
                return true;
            } else if (response.startsWith("ERROR:")) {
                throw new AppException(response.substring(6));
            }

            return false;

        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar tarjeta: " + e.getMessage(), e);
        }
    }

    /**
     * Borra todos los datos de la tarjeta NFC
     * 
     * @return true si se borró correctamente
     */
    public boolean deleteCard() {
        if (!connected) {
            throw new RuntimeException("Arduino no conectado");
        }

        try {
            sendCommand("DELETE");

            // Leer respuesta
            String response = readResponse();

            if (response.equals("OK")) {
                return true;
            } else if (response.startsWith("ERROR:")) {
                throw new AppException(response.substring(6));
            }

            return false;

        } catch (Exception e) {
            throw new RuntimeException("Error al borrar tarjeta: " + e.getMessage(), e);
        }
    }

    /**
     * Envía mensaje al LCD del Arduino
     * 
     * @param linea1 Primera línea (máx 16 caracteres)
     * @param linea2 Segunda línea (máx 16 caracteres)
     */
    public void sendLCDMessage(String linea1, String linea2) {
        if (!connected) {
            return;
        }

        try {
            String command = String.format("LCD:%s|%s", linea1, linea2);
            sendCommand(command);
        } catch (Exception e) {
            CMD.printlnError("Error al enviar mensaje LCD: " + e.getMessage());
        }
    }

    @Override
    public void disconnect() {
        if (connected && serialPort != null) {
            try {
                if (inputStream != null)
                    inputStream.close();
                if (outputStream != null)
                    outputStream.close();
                serialPort.closePort();
                connected = false;
                CMD.println("Arduino desconectado");
            } catch (Exception e) {
                CMD.printlnError("Error al desconectar: " + e.getMessage());
            }
        }
    }

    /**
     * Verifica si está conectado
     */
    public boolean isConnected() {
        return connected && serialPort != null && serialPort.isOpen();
    }

    // ===== MÉTODOS PRIVADOS =====

    private void sendCommand(String command) throws Exception {
        if (outputStream == null) {
            throw new AppException("Stream de salida no disponible");
        }

        String cmd = command + "\n";
        outputStream.write(cmd.getBytes());
        outputStream.flush();
        CMD.println("Comando enviado: " + command);
    }

    private String readResponse() throws Exception {
        if (inputStream == null) {
            throw new AppException("Stream de entrada no disponible");
        }

        StringBuilder response = new StringBuilder();
        long startTime = System.currentTimeMillis();

        while (System.currentTimeMillis() - startTime < timeout) {
            if (inputStream.available() > 0) {
                int data = inputStream.read();
                if (data == '\n') {
                    break;
                }
                response.append((char) data);
            }
            Thread.sleep(10);
        }

        String result = response.toString().trim();
        CMD.println("Respuesta recibida: " + result);
        return result;
    }

    /**
     * Lista todos los puertos COM disponibles
     */
    public static String[] getAvailablePorts() {
        SerialPort[] ports = SerialPort.getCommPorts();
        String[] portNames = new String[ports.length];
        for (int i = 0; i < ports.length; i++) {
            portNames[i] = ports[i].getSystemPortName();
        }
        return portNames;
    }
}
