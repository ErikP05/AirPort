package Infrastructure.Ports;
import com.fazecast.jSerialComm.SerialPort;
import java.util.Scanner;


public class SerialRFIDPorts implements RFIDPorts {


private SerialPort port;
private Scanner scanner;
private boolean connected = false;



private final String PORT_NAME = "COM7";
private final int BAUD_RATE = 9600;


public static void listAvailablePorts() {
    SerialPort[] ports = SerialPort.getCommPorts();
    System.out.println("Puertos seriales disponibles:");
    for (SerialPort p : ports) {
        System.out.println(" - " + p.getSystemPortName() + ": " + p.getDescriptivePortName());
    }
}


@Override
public void connect() {
    port = SerialPort.getCommPort(PORT_NAME);
    if (port == null) {
        System.out.println("❌ Puerto " + PORT_NAME + " no encontrado en el sistema.");
        return;
    }
    port.setBaudRate(BAUD_RATE);
    port.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);

    if (port.openPort()) {
        scanner = new Scanner(port.getInputStream());
        connected = true;
        System.out.println("RFID conectado por puerto serial: " + PORT_NAME);
    } else {
        System.out.println("No se pudo abrir el puerto serial: " + PORT_NAME + ". Verifica que no esté ocupado o que el dispositivo esté conectado.");
    }
}


@Override
public String readUID() {
if (!connected || scanner == null) return null;


if (scanner.hasNextLine()) {
String data = scanner.nextLine().trim();


// Filtra solo las líneas que contienen UID
if (data.startsWith("UID")) {
return data;
}
}
return null;
}


@Override
public void disconnect() {
connected = false;


if (scanner != null) {
scanner.close();
}


if (port != null && port.isOpen()) {
port.closePort();
}


System.out.println("RFID desconectado");
}
}
