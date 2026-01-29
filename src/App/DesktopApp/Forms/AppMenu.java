package App.DesktopApp.Forms;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import App.DesktopApp.CustomControl.PatButton;
import Infrastructure.AppConfig;
import Infrastructure.AppMSG;

public class AppMenu extends JPanel {
    private final List<PatButton> menuItems = new ArrayList<>();
    private final JPanel buttonsPanel = new JPanel();

    private PatButton btnHome;
    private PatButton btnClient;
    private PatButton btnAdmin;
    private PatButton btnTest;

    public AppMenu() {
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

        // glue para empujar el copyright
        add(Box.createVerticalGlue());
        add(new JLabel(" ──❰ 🛬 ❱── © Grupo 4"));
    }

    private void initButtons() {
        btnHome = new PatButton(" Home");
        btnClient = new PatButton(" Cliente");
        btnAdmin = new PatButton(" Administrador");
        btnTest = new PatButton(" validar");

        // La lógica interna (que no cambia de panel) se queda aquí
        btnTest.addActionListener(e -> AppMSG.showError("mensaje de error"));

        addMenuItem(btnHome);
        addMenuItem(btnClient);
        addMenuItem(btnAdmin);
        addMenuItem(btnTest);
    }

    public void addMenuItem(PatButton button) {
        menuItems.add(button);
        buttonsPanel.add(button);
        buttonsPanel.revalidate();
        buttonsPanel.repaint();
    }

    public void addActionHome(ActionListener ejecutar) {
        btnHome.addActionListener(ejecutar);
    }

    public void addActionClient(ActionListener ejecutar) {
        btnClient.addActionListener(ejecutar);
    }

    public void addActionAdmin(ActionListener ejecutar) {
        btnAdmin.addActionListener(ejecutar);
    }

    public List<PatButton> getMenuItems() {
        return menuItems;
    }
}
