package App.DesktopApp.Forms;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import App.DesktopApp.CustomControl.PatButton;
import App.DesktopApp.CustomControl.PatLabel;
import BusinessLogic.FactoryBL;
import DataAccess.DAOs.PasajeroDAO;
import DataAccess.DTOs.PasajeroDTO;
import Infrastructure.AppMSG;
import Infrastructure.AppStyle;
import Infrastructure.Ports.RFIDArduinoAdapter;

/**
 * Pantalla para gestionar tarjetas NFC de pasajeros
 * Permite: Leer UID, Escribir datos, Actualizar datos, Borrar tarjeta
 */
public class PNFCManager extends JPanel {
    
    private transient FactoryBL<PasajeroDTO> blFactory = new FactoryBL<>(PasajeroDAO.class);
    private RFIDArduinoAdapter rfidAdapter;
    private boolean arduinoConnected = false;
    
    // Componentes UI
    private PatLabel lblTitulo = new PatLabel("Gestión de Tarjetas NFC");
    private PatLabel lblEstado = new PatLabel("Estado: Desconectado");
    private PatLabel lblPuerto = new PatLabel("Puerto COM:");
    private JComboBox<String> cmbPuerto = new JComboBox<>();
    
    private PatButton btnConectar = new PatButton("Conectar Arduino");
    private PatButton btnDesconectar = new PatButton("Desconectar");
    private PatButton btnLeerUID = new PatButton("Leer UID de Tarjeta");
    
    // Panel de operaciones
    private PatLabel lblOperacion = new PatLabel("=== Operaciones con Tarjeta ===");
    private PatLabel lblPasajero = new PatLabel("Seleccionar Pasajero:");
    private JComboBox<String> cmbPasajero = new JComboBox<>();
    
    private PatButton btnEscribir = new PatButton("✍ Escribir en Tarjeta");
    private PatButton btnActualizar = new PatButton("🔄 Actualizar Tarjeta");
    private PatButton btnBorrar = new PatButton("🗑 Borrar Tarjeta");
    
    // Panel de información
    private JTextArea txtInfo = new JTextArea(10, 40);
    private JScrollPane scrollInfo;
    
    // Panel LCD simulado
    private JPanel pnlLCD = new JPanel();
    private JLabel lblLCD1 = new JLabel("                ");
    private JLabel lblLCD2 = new JLabel("                ");
    
    // Datos
    private java.util.List<PasajeroDTO> listaPasajeros;
    
    public PNFCManager() {
        try {
            initComponents();
            cargarPuertos();
            cargarPasajeros();
            configurarEventos();
        } catch (Exception e) {
            AppMSG.showError("Error al inicializar: " + e.getMessage());
        }
    }
    
    private void initComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Título
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        add(lblTitulo, gbc);
        
        // Estado de conexión
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        add(lblEstado, gbc);
        lblEstado.setForeground(Color.RED);
        
        // Selector de puerto
        gbc.gridy = 2;
        gbc.gridx = 0;
        add(lblPuerto, gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        add(cmbPuerto, gbc);
        
        // Botones de conexión
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        add(btnConectar, gbc);
        
        gbc.gridx = 1;
        add(btnDesconectar, gbc);
        btnDesconectar.setEnabled(false);
        
        gbc.gridx = 2;
        add(btnLeerUID, gbc);
        btnLeerUID.setEnabled(false);
        
        // Separador
        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 3;
        add(new JSeparator(), gbc);
        
        // Operaciones
        gbc.gridy = 5;
        add(lblOperacion, gbc);
        
        gbc.gridy = 6;
        gbc.gridwidth = 1;
        add(lblPasajero, gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        add(cmbPasajero, gbc);
        
        // Botones de operación
        gbc.gridy = 7;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        add(btnEscribir, gbc);
        btnEscribir.setEnabled(false);
        
        gbc.gridx = 1;
        add(btnActualizar, gbc);
        btnActualizar.setEnabled(false);
        
        gbc.gridx = 2;
        add(btnBorrar, gbc);
        btnBorrar.setEnabled(false);
        
        // Panel LCD simulado
        gbc.gridy = 8;
        gbc.gridx = 0;
        gbc.gridwidth = 3;
        configurarPanelLCD();
        add(pnlLCD, gbc);
        
        // Área de información
        gbc.gridy = 9;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        
        txtInfo.setEditable(false);
        txtInfo.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtInfo.setBorder(AppStyle.createBorderRect());
        scrollInfo = new JScrollPane(txtInfo);
        add(scrollInfo, gbc);
        
        agregarLog("Sistema iniciado. Conecte el Arduino para comenzar.");
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
        
        actualizarLCD("Airport NFC", "System Ready");
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
    
    private void cargarPasajeros() {
        try {
            listaPasajeros = blFactory.getAll();
            cmbPasajero.removeAllItems();
            
            for (PasajeroDTO p : listaPasajeros) {
                String item = String.format("[%d] %s %s - %s", 
                    p.getIdPasajero(), 
                    p.getNombre(), 
                    p.getApellido(),
                    p.getCedula());
                cmbPasajero.addItem(item);
            }
            
            if (listaPasajeros.isEmpty()) {
                agregarLog("⚠ No hay pasajeros registrados. Registre pasajeros primero.");
            }
            
        } catch (Exception e) {
            agregarLog("❌ Error al cargar pasajeros: " + e.getMessage());
        }
    }
    
    private void configurarEventos() {
        btnConectar.addActionListener(e -> conectarArduino());
        btnDesconectar.addActionListener(e -> desconectarArduino());
        btnLeerUID.addActionListener(e -> leerUID());
        btnEscribir.addActionListener(e -> escribirTarjeta());
        btnActualizar.addActionListener(e -> actualizarTarjeta());
        btnBorrar.addActionListener(e -> borrarTarjeta());
    }
    
    private void conectarArduino() {
        try {
            String puerto = (String) cmbPuerto.getSelectedItem();
            if (puerto == null || puerto.equals("No hay puertos disponibles")) {
                AppMSG.showError("Seleccione un puerto COM válido");
                return;
            }
            
            agregarLog("🔌 Conectando a " + puerto + "...");
            
            // Crear adaptador con configuración desde properties
            rfidAdapter = new RFIDArduinoAdapter(puerto, 9600, 5000);
            rfidAdapter.connect();
            
            arduinoConnected = true;
            lblEstado.setText("Estado: Conectado ✓");
            lblEstado.setForeground(new Color(0, 150, 0));
            
            btnConectar.setEnabled(false);
            btnDesconectar.setEnabled(true);
            btnLeerUID.setEnabled(true);
            btnEscribir.setEnabled(true);
            btnActualizar.setEnabled(true);
            btnBorrar.setEnabled(true);
            
            agregarLog("✅ Arduino conectado exitosamente");
            actualizarLCD("Arduino", "Conectado!");
            
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
            btnLeerUID.setEnabled(false);
            btnEscribir.setEnabled(false);
            btnActualizar.setEnabled(false);
            btnBorrar.setEnabled(false);
            
            agregarLog("🔌 Arduino desconectado");
            actualizarLCD("Desconectado", "");
        }
    }
    
    private void leerUID() {
        if (!arduinoConnected) {
            AppMSG.showError("Arduino no conectado");
            return;
        }
        
        try {
            agregarLog("📖 Leyendo UID de tarjeta...");
            actualizarLCD("Acerque su", "tarjeta NFC...");
            
            String uid = rfidAdapter.readUID();
            
            agregarLog("✅ UID leído: " + uid);
            actualizarLCD("UID Leido:", uid);
            
            AppMSG.show("UID de la tarjeta:\n" + uid);
            
        } catch (Exception e) {
            agregarLog("❌ Error al leer UID: " + e.getMessage());
            AppMSG.showError("Error: " + e.getMessage());
            actualizarLCD("Error:", "Sin tarjeta");
        }
    }
    
    private void escribirTarjeta() {
        if (!arduinoConnected) {
            AppMSG.showError("Arduino no conectado");
            return;
        }
        
        int index = cmbPasajero.getSelectedIndex();
        if (index < 0) {
            AppMSG.showError("Seleccione un pasajero");
            return;
        }
        
        PasajeroDTO pasajero = listaPasajeros.get(index);
        
        if (!AppMSG.showConfirmYesNo("¿Escribir datos del pasajero en la tarjeta NFC?\n\n" +
                "ID: " + pasajero.getIdPasajero() + "\n" +
                "Nombre: " + pasajero.getNombre() + " " + pasajero.getApellido() + "\n" +
                "Cédula: " + pasajero.getCedula())) {
            return;
        }
        
        try {
            agregarLog("✍ Escribiendo datos en tarjeta...");
            agregarLog("   Pasajero: " + pasajero.getNombre() + " " + pasajero.getApellido());
            actualizarLCD("Acerque tarjeta", "para escribir...");
            
            boolean success = rfidAdapter.writeCard(
                pasajero.getIdPasajero(),
                pasajero.getNombre() + " " + pasajero.getApellido(),
                pasajero.getCedula()
            );
            
            if (success) {
                agregarLog("✅ Datos escritos exitosamente");
                actualizarLCD("Datos escritos", "exitosamente!");
                AppMSG.show("Tarjeta NFC programada correctamente");
            } else {
                agregarLog("❌ Fallo al escribir datos");
                AppMSG.showError("No se pudieron escribir los datos");
            }
            
        } catch (Exception e) {
            agregarLog("❌ Error: " + e.getMessage());
            AppMSG.showError("Error al escribir: " + e.getMessage());
        }
    }
    
    private void actualizarTarjeta() {
        if (!arduinoConnected) {
            AppMSG.showError("Arduino no conectado");
            return;
        }
        
        int index = cmbPasajero.getSelectedIndex();
        if (index < 0) {
            AppMSG.showError("Seleccione un pasajero");
            return;
        }
        
        PasajeroDTO pasajero = listaPasajeros.get(index);
        
        if (!AppMSG.showConfirmYesNo("¿Actualizar tarjeta NFC con nuevos datos?\n\n" +
                "ID: " + pasajero.getIdPasajero() + "\n" +
                "Nombre: " + pasajero.getNombre() + " " + pasajero.getApellido() + "\n" +
                "Cédula: " + pasajero.getCedula())) {
            return;
        }
        
        try {
            agregarLog("🔄 Actualizando datos en tarjeta...");
            actualizarLCD("Acerque tarjeta", "para actualizar");
            
            boolean success = rfidAdapter.updateCard(
                pasajero.getIdPasajero(),
                pasajero.getNombre() + " " + pasajero.getApellido(),
                pasajero.getCedula()
            );
            
            if (success) {
                agregarLog("✅ Tarjeta actualizada exitosamente");
                actualizarLCD("Tarjeta", "actualizada!");
                AppMSG.show("Tarjeta NFC actualizada correctamente");
            } else {
                agregarLog("❌ Fallo al actualizar");
                AppMSG.showError("No se pudo actualizar la tarjeta");
            }
            
        } catch (Exception e) {
            agregarLog("❌ Error: " + e.getMessage());
            AppMSG.showError("Error al actualizar: " + e.getMessage());
        }
    }
    
    private void borrarTarjeta() {
        if (!arduinoConnected) {
            AppMSG.showError("Arduino no conectado");
            return;
        }
        
        if (!AppMSG.showConfirmYesNo("⚠ ADVERTENCIA ⚠\n\n" +
                "¿Está seguro de borrar TODOS los datos de la tarjeta?\n" +
                "Esta acción NO se puede deshacer.")) {
            return;
        }
        
        try {
            agregarLog("🗑 Borrando datos de tarjeta...");
            actualizarLCD("Acerque tarjeta", "para borrar...");
            
            boolean success = rfidAdapter.deleteCard();
            
            if (success) {
                agregarLog("✅ Tarjeta borrada exitosamente");
                actualizarLCD("Tarjeta borrada", "exitosamente!");
                AppMSG.show("Tarjeta NFC borrada correctamente");
            } else {
                agregarLog("❌ Fallo al borrar");
                AppMSG.showError("No se pudo borrar la tarjeta");
            }
            
        } catch (Exception e) {
            agregarLog("❌ Error: " + e.getMessage());
            AppMSG.showError("Error al borrar: " + e.getMessage());
        }
    }
    
    private void agregarLog(String mensaje) {
        String timestamp = new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date());
        txtInfo.append("[" + timestamp + "] " + mensaje + "\n");
        txtInfo.setCaretPosition(txtInfo.getDocument().getLength());
    }
    
    private void actualizarLCD(String linea1, String linea2) {
        lblLCD1.setText(String.format("%-16s", linea1).substring(0, Math.min(16, linea1.length())));
        lblLCD2.setText(String.format("%-16s", linea2).substring(0, Math.min(16, linea2.length())));
        
        if (rfidAdapter != null && arduinoConnected) {
            rfidAdapter.sendLCDMessage(linea1, linea2);
        }
    }
}
