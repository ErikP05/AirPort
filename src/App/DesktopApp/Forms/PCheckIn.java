package App.DesktopApp.Forms;

import javax.swing.*;
import java.awt.*;
import App.DesktopApp.CustomControl.PatButton;
import App.DesktopApp.CustomControl.PatLabel;
import BusinessLogic.FactoryBL;
import DataAccess.DAOs.*;
import DataAccess.DTOs.*;
import Infrastructure.AppMSG;
import Infrastructure.Ports.RFIDArduinoAdapter;

/**
 * Pantalla de Check-in con RFID
 * Permite realizar check-in automático escaneando tarjeta NFC del pasajero
 */
public class PCheckIn extends JPanel {

    private transient FactoryBL<PasajeroDTO> blPasajero = new FactoryBL<>(PasajeroDAO.class);
    private transient FactoryBL<ReservaDTO> blReserva = new FactoryBL<>(ReservaDAO.class);
    private transient FactoryBL<VueloDTO> blVuelo = new FactoryBL<>(VueloDAO.class);
    private RFIDArduinoAdapter rfidAdapter;
    private boolean arduinoConnected = false;

    // Componentes UI
    private PatLabel lblTitulo = new PatLabel("✈️ Check-in RFID Automático");
    private PatLabel lblEstado = new PatLabel("Estado: Desconectado");
    private PatLabel lblInstrucciones = new PatLabel("1. Conecte el Arduino  2. Acerque su tarjeta NFC");

    private JComboBox<String> cmbPuerto = new JComboBox<>();
    private PatButton btnConectar = new PatButton("🔌 Conectar Arduino");
    private PatButton btnDesconectar = new PatButton("Desconectar");
    private PatButton btnScanCard = new PatButton("📖 Escanear Tarjeta");

    // Panel de información del pasajero
    private JPanel pnlInfoPasajero = new JPanel();
    private JLabel lblInfoTitulo = new JLabel("Información del Pasajero");
    private JTextArea txtInfoPasajero = new JTextArea(8, 40);

    // Panel LCD simulado
    private JPanel pnlLCD = new JPanel();
    private JLabel lblLCD1 = new JLabel("                ");
    private JLabel lblLCD2 = new JLabel("                ");

    // Log de eventos
    private JTextArea txtLog = new JTextArea(10, 40);

    public PCheckIn() {
        initComponents();
        cargarPuertos();
        configurarEventos();
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Título
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        add(lblTitulo, gbc);

        // Instrucciones
        gbc.gridy = 1;
        lblInstrucciones.setFont(new Font("Arial", Font.ITALIC, 12));
        add(lblInstrucciones, gbc);

        // Estado
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        add(lblEstado, gbc);
        lblEstado.setForeground(Color.RED);

        // Selector de puerto
        gbc.gridy = 3;
        gbc.gridx = 0;
        add(new JLabel("Puerto COM:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        add(cmbPuerto, gbc);

        // Botones de conexión
        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        add(btnConectar, gbc);

        gbc.gridx = 1;
        add(btnDesconectar, gbc);
        btnDesconectar.setEnabled(false);

        gbc.gridx = 2;
        add(btnScanCard, gbc);
        btnScanCard.setEnabled(false);

        // Panel LCD
        gbc.gridy = 5;
        gbc.gridx = 0;
        gbc.gridwidth = 3;
        configurarPanelLCD();
        add(pnlLCD, gbc);

        // Panel de información del pasajero
        gbc.gridy = 6;
        configurarPanelInfo();
        add(pnlInfoPasajero, gbc);

        // Log de eventos
        gbc.gridy = 7;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        txtLog.setEditable(false);
        txtLog.setFont(new Font("Monospaced", Font.PLAIN, 11));
        JScrollPane scrollLog = new JScrollPane(txtLog);
        scrollLog.setBorder(BorderFactory.createTitledBorder("Log de Eventos"));
        add(scrollLog, gbc);

        agregarLog("Sistema de Check-in iniciado");
        agregarLog("Conecte el Arduino para comenzar");
    }

    private void configurarPanelLCD() {
        pnlLCD.setLayout(new GridLayout(2, 1));
        pnlLCD.setBackground(new Color(100, 150, 255));
        pnlLCD.setBorder(BorderFactory.createTitledBorder("LCD Arduino (16x2)"));
        pnlLCD.setPreferredSize(new Dimension(400, 80));

        lblLCD1.setFont(new Font("Monospaced", Font.BOLD, 16));
        lblLCD1.setForeground(Color.WHITE);
        lblLCD1.setHorizontalAlignment(SwingConstants.CENTER);

        lblLCD2.setFont(new Font("Monospaced", Font.BOLD, 16));
        lblLCD2.setForeground(Color.WHITE);
        lblLCD2.setHorizontalAlignment(SwingConstants.CENTER);

        pnlLCD.add(lblLCD1);
        pnlLCD.add(lblLCD2);

        actualizarLCD("Airport Check-in", "Acerque tarjeta");
    }

    private void configurarPanelInfo() {
        pnlInfoPasajero.setLayout(new BorderLayout());
        pnlInfoPasajero.setBorder(BorderFactory.createTitledBorder("Información del Pasajero"));
        pnlInfoPasajero.setPreferredSize(new Dimension(400, 150));

        lblInfoTitulo.setFont(new Font("Arial", Font.BOLD, 14));
        lblInfoTitulo.setHorizontalAlignment(SwingConstants.CENTER);

        txtInfoPasajero.setEditable(false);
        txtInfoPasajero.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtInfoPasajero.setText("Esperando escaneo de tarjeta...");

        pnlInfoPasajero.add(lblInfoTitulo, BorderLayout.NORTH);
        pnlInfoPasajero.add(new JScrollPane(txtInfoPasajero), BorderLayout.CENTER);
    }

    private void cargarPuertos() {
        String[] puertos = RFIDArduinoAdapter.getAvailablePorts();
        for (String puerto : puertos) {
            cmbPuerto.addItem(puerto);
        }

        if (puertos.length == 0) {
            cmbPuerto.addItem("No hay puertos disponibles");
            btnConectar.setEnabled(false);
        }
    }

    private void configurarEventos() {
        btnConectar.addActionListener(e -> conectarArduino());
        btnDesconectar.addActionListener(e -> desconectarArduino());
        btnScanCard.addActionListener(e -> escanearTarjeta());
    }

    private void conectarArduino() {
        try {
            String puerto = (String) cmbPuerto.getSelectedItem();
            if (puerto == null || puerto.equals("No hay puertos disponibles")) {
                AppMSG.showError("Seleccione un puerto COM válido");
                return;
            }

            agregarLog("🔌 Conectando a " + puerto + "...");

            rfidAdapter = new RFIDArduinoAdapter(puerto, 9600, 5000);
            rfidAdapter.connect();

            arduinoConnected = true;
            lblEstado.setText("Estado: Conectado ✓");
            lblEstado.setForeground(new Color(0, 150, 0));

            btnConectar.setEnabled(false);
            btnDesconectar.setEnabled(true);
            btnScanCard.setEnabled(true);

            agregarLog("✅ Arduino conectado exitosamente");
            actualizarLCD("Sistema listo", "Acerque tarjeta");

        } catch (Exception e) {
            arduinoConnected = false;
            agregarLog("❌ Error de conexión: " + e.getMessage());
            AppMSG.showError("No se pudo conectar: " + e.getMessage());
        }
    }

    private void desconectarArduino() {
        if (rfidAdapter != null) {
            rfidAdapter.disconnect();
            arduinoConnected = false;

            lblEstado.setText("Estado: Desconectado");
            lblEstado.setForeground(Color.RED);

            btnConectar.setEnabled(true);
            btnDesconectar.setEnabled(false);
            btnScanCard.setEnabled(false);

            agregarLog("🔌 Arduino desconectado");
            actualizarLCD("Desconectado", "");
        }
    }

    private void escanearTarjeta() {
        if (!arduinoConnected) {
            AppMSG.showError("Arduino no conectado");
            return;
        }

        try {
            agregarLog("📖 Esperando tarjeta NFC...");
            actualizarLCD("Acerque su", "tarjeta NFC...");
            txtInfoPasajero.setText("Leyendo tarjeta...");

            // Leer UID de la tarjeta
            String uid = rfidAdapter.readUID();
            agregarLog("✅ UID leído: " + uid);

            // Buscar pasajero por UID
            PasajeroDTO pasajero = buscarPasajeroPorUID(uid);

            if (pasajero == null) {
                agregarLog("❌ Tarjeta no registrada");
                actualizarLCD("Error:", "Tarjeta invalida");
                txtInfoPasajero.setText("❌ ERROR: Tarjeta no registrada en el sistema");
                AppMSG.showError("Tarjeta no registrada.\nPor favor, registre al pasajero primero.");
                return;
            }

            agregarLog("👤 Pasajero encontrado: " + pasajero.getNombre() + " " + pasajero.getApellido());

            // Buscar reserva activa del pasajero
            ReservaDTO reserva = buscarReservaActiva(pasajero.getIdPasajero());

            if (reserva == null) {
                agregarLog("❌ No hay reserva activa");
                actualizarLCD("Error:", "Sin reserva");
                txtInfoPasajero.setText("❌ ERROR: No tiene reserva activa");
                AppMSG.showError("No se encontró reserva activa para este pasajero");
                return;
            }

            // Verificar si ya hizo check-in
            if ("T".equals(reserva.getEstadoCheckin())) {
                agregarLog("⚠ Check-in ya realizado");
                actualizarLCD("Ya registrado", "");
                txtInfoPasajero.setText("⚠ ADVERTENCIA: Ya realizó check-in anteriormente");
                AppMSG.show("Este pasajero ya realizó check-in");
                return;
            }

            // Obtener información del vuelo
            VueloDTO vuelo = blVuelo.getBy(reserva.getIdVuelo());

            // Realizar check-in
            reserva.setEstadoCheckin("T");
            blReserva.upd(reserva);

            agregarLog("✅ CHECK-IN EXITOSO");

            // Mostrar información completa
            String info = String.format(
                    "✅ CHECK-IN EXITOSO\n\n" +
                            "Pasajero: %s %s\n" +
                            "Cédula: %s\n" +
                            "Email: %s\n\n" +
                            "Vuelo: XA-%03d\n" +
                            "Asiento: %s\n" +
                            "Estado: Check-in Realizado\n\n" +
                            "¡Buen viaje!",
                    pasajero.getNombre(),
                    pasajero.getApellido(),
                    pasajero.getCedula(),
                    pasajero.getEmail(),
                    vuelo.getIdVuelo(),
                    reserva.getAsiento());

            txtInfoPasajero.setText(info);
            txtInfoPasajero.setForeground(new Color(0, 120, 0));

            actualizarLCD("Check-in OK!", "Asiento: " + reserva.getAsiento());

            AppMSG.show("✅ Check-in realizado exitosamente\n\n" +
                    "Pasajero: " + pasajero.getNombre() + " " + pasajero.getApellido() + "\n" +
                    "Vuelo: XA-" + String.format("%03d", vuelo.getIdVuelo()) + "\n" +
                    "Asiento: " + reserva.getAsiento());

        } catch (Exception e) {
            agregarLog("❌ Error: " + e.getMessage());
            actualizarLCD("Error:", "Intente de nuevo");
            txtInfoPasajero.setText("❌ ERROR: " + e.getMessage());
            txtInfoPasajero.setForeground(Color.RED);
            AppMSG.showError("Error al procesar check-in: " + e.getMessage());
        }
    }

    private PasajeroDTO buscarPasajeroPorUID(String uid) throws Exception {
        for (PasajeroDTO p : blPasajero.getAll()) {
            if (uid.equalsIgnoreCase(p.getUidRfid())) {
                return p;
            }
        }
        return null;
    }

    private ReservaDTO buscarReservaActiva(int idPasajero) throws Exception {
        for (ReservaDTO r : blReserva.getAll()) {
            if (r.getIdPasajero() == idPasajero && "A".equals(r.getEstado())) {
                return r;
            }
        }
        return null;
    }

    private void agregarLog(String mensaje) {
        String timestamp = new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date());
        txtLog.append("[" + timestamp + "] " + mensaje + "\n");
        txtLog.setCaretPosition(txtLog.getDocument().getLength());
    }

    private void actualizarLCD(String linea1, String linea2) {
        lblLCD1.setText(String.format("%-16s", linea1).substring(0, Math.min(16, linea1.length())));
        lblLCD2.setText(String.format("%-16s", linea2).substring(0, Math.min(16, linea2.length())));

        if (rfidAdapter != null && arduinoConnected) {
            rfidAdapter.sendLCDMessage(linea1, linea2);
        }
    }
}
