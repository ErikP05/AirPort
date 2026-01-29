package App.DesktopApp.Forms;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import App.DesktopApp.CustomControl.PatButton;
import Infrastructure.AppConfig;

public class APPAdmin extends JPanel {
    private final List<PatButton> menuItems = new ArrayList<>();
    private final JPanel buttonsPanel = new JPanel();
    private PatButton btnTabalaVuelos;
    private PatButton btnTablaPaisDestino;
    private PatButton btnTablaPaisOrigen;
    private PatButton btnTablaPasajero;
    private PatButton btnTablaAviones;
    private PatButton btnTablaReservas;
    private PatButton btnNFCManager;
    private PatButton btnVolver;

    public APPAdmin() {
        initComponents();
        initButtons();
    }

    private void initComponents() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(250, getHeight()));

        // add-logo
        try {
            Image logo = ImageIO.read(AppConfig.getImgLogo());
            logo = logo.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            add(new JLabel(new ImageIcon(logo)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // panel para los items del menu
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        add(buttonsPanel);
    }

    private void initButtons() {
        PatButton btnRegistroVuelos = new PatButton("Registrar Vuelos");
        btnTabalaVuelos = new PatButton("Mostrar Vuelos");
        btnTablaPasajero = new PatButton("Pasajeros Registrados");
        btnTablaReservas = new PatButton("📋 Reservas");
        btnTablaAviones = new PatButton("Aviones Registrados");
        btnTablaPaisDestino = new PatButton("Paises de Destino Registrados");
        btnTablaPaisOrigen = new PatButton("Paises de Origen Registrados");
        btnNFCManager = new PatButton("💳 Gestión Tarjetas NFC");
        btnVolver = new PatButton("Volver");

        // Acción real para abrir el formulario de registro de vuelos se agregará desde AppStart

        addMenuItem(btnRegistroVuelos);
        addMenuItem(btnTabalaVuelos);
        addMenuItem(btnTablaPasajero);
        addMenuItem(btnTablaReservas);
        addMenuItem(btnTablaAviones);
        addMenuItem(btnTablaPaisDestino);
        addMenuItem(btnTablaPaisOrigen);
        addMenuItem(btnNFCManager);
        addMenuItem(btnVolver);

    }

    public void addMenuItem(PatButton button) {
        menuItems.add(button);
        buttonsPanel.add(button);
        buttonsPanel.revalidate();
        buttonsPanel.repaint();
    }

    public void addActionVolver(ActionListener action) {
        btnVolver.addActionListener(action);
    }

    public void addActionTablaAviones(ActionListener action) {
        btnTablaAviones.addActionListener(action);
    }

    public void addActionTablaPasajero(ActionListener action) {
        btnTablaPasajero.addActionListener(action);
    }

    public void addActionTablaPaisDestino(ActionListener action) {
        btnTablaPaisDestino.addActionListener(action);
    }

    public void addActionTablaPaisOrigen(ActionListener action) {
        btnTablaPaisOrigen.addActionListener(action);
    }

    public void addActionTablaVuelos(ActionListener action) {
        btnTabalaVuelos.addActionListener(action);
    }

    public void addActionTablaReservas(ActionListener action) {
        btnTablaReservas.addActionListener(action);
    }

    public void addActionNFCManager(ActionListener action) {
        btnNFCManager.addActionListener(action);
    }

    public void addActionRegistrarVuelos(ActionListener action) {
        // Busca el botón por el texto
        for (PatButton btn : menuItems) {
            if (btn.getText().contains("Registrar Vuelos")) {
                btn.addActionListener(action);
                break;
            }
        }
    }

    public List<PatButton> getMenuItems() {
        return menuItems;
    }
}
