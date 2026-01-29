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

public class APPCliente extends JPanel {
    private final List<PatButton> menuItems = new ArrayList<>();
    private final JPanel buttonsPanel = new JPanel();

    private PatButton btnCheckInRFID;
    private PatButton btnVolver;
    private PatButton btnUsuarioVuelos;
    private PatButton btnUsuarioChekin;

    public APPCliente() {
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
        btnUsuarioVuelos = new PatButton(" Mis Vuelos");
        btnUsuarioChekin = new PatButton(" Realizar Chekin");
        btnCheckInRFID = new PatButton("✈️ Check-in RFID");
        btnVolver = new PatButton("Volver");

        addMenuItem(btnUsuarioVuelos);
        addMenuItem(btnUsuarioChekin);
        addMenuItem(btnCheckInRFID);
        addMenuItem(btnVolver);
    }

    public void addMenuItem(PatButton button) {
        menuItems.add(button);
        buttonsPanel.add(button);
        buttonsPanel.revalidate();
        buttonsPanel.repaint();
    }

    public void addActionVolver(ActionListener ejecutar) {
        btnVolver.addActionListener(ejecutar);
    }

    public void addActionMisVuelos(ActionListener ejecutar) {
        btnUsuarioVuelos.addActionListener(ejecutar);
    }

    public void addActionRealizarCheckin(ActionListener ejecutar) {
        btnUsuarioChekin.addActionListener(ejecutar);
    }

    public void addActionCheckInRFID(ActionListener ejecutar) {
        btnCheckInRFID.addActionListener(ejecutar);
    }

    public List<PatButton> getMenuItems() {
        return menuItems;
    }
}
