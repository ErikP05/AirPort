package App.DesktopApp.Forms;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import Infrastructure.AppMSG;

public class AppStart extends JFrame {
    AppMenu pnlMenu = new AppMenu();
    APPCliente pnlClient = new APPCliente();
    APPAdmin pnlAdmin = new APPAdmin();
    JPanel pnlMain = new PHome();
    JPanel currentMenu = pnlMenu;

    public AppStart(String tilteApp) {
        initComponents(tilteApp);

        pnlMenu.addActionHome(e -> {
            setWestPanel(pnlMenu);
            setPanel(new PHome());
        });

        pnlMenu.addActionClient(e -> {
            setWestPanel(pnlClient);
            setPanel(new PHome());
        });

        pnlMenu.addActionAdmin(e -> loginAdmin());

        pnlClient.addActionVolver(e -> {
            setWestPanel(pnlMenu);
            setPanel(new PHome());
        });
        pnlClient.addActionMisVuelos(e -> setPanel(new PReserva()));
        pnlClient.addActionRealizarCheckin(e -> setPanel(new PCheckIn()));
        pnlAdmin.addActionVolver(e -> {
            setWestPanel(pnlMenu);
            setPanel(new PHome());
        });
        pnlAdmin.addActionTablaAviones(e -> setPanel(new PAvion()));
        pnlAdmin.addActionTablaPasajero(e -> setPanel(new PPasajero()));
        pnlAdmin.addActionTablaPaisDestino(e -> setPanel(new PPaisDestino()));
        pnlAdmin.addActionTablaPaisOrigen(e -> setPanel(new PPaisOrigen()));
        pnlAdmin.addActionTablaVuelos(e -> setPanel(new PVuelos()));
        pnlAdmin.addActionTablaReservas(e -> setPanel(new PReserva()));
        pnlAdmin.addActionNFCManager(e -> setPanel(new PNFCManager()));
        pnlAdmin.addActionRegistrarVuelos(e -> setPanel(new PVuelos()));
        pnlClient.addActionCheckInRFID(e -> setPanel(new PCheckIn()));
    }

    private void setPanel(JPanel formularioPanel) {
        Container container = getContentPane();
        container.remove(pnlMain);
        pnlMain = formularioPanel;
        container.add(pnlMain, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void setWestPanel(JPanel newMenu) {
        Container container = getContentPane();
        container.remove(currentMenu);
        currentMenu = newMenu;
        container.add(currentMenu, BorderLayout.WEST);
        revalidate();
        repaint();
    }

    private void initComponents(String tilteApp) {
        setTitle(tilteApp);
        setSize(1100, 760);
        setResizable(false);
        setLocationRelativeTo(null); // Centrar en la pantalla
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Crear un contenedor para los dos paneles usando BorderLayout
        Container container = getContentPane();
        container.setLayout(new BorderLayout());

        // Agregar los paneles al contenedor
        container.add(pnlMenu, BorderLayout.WEST);
        container.add(pnlMain, BorderLayout.CENTER);
        setVisible(true);
    }

    private void loginAdmin() {
        JTextField txtUsuario = new JTextField();
        JPasswordField txtContrasenia = new JPasswordField();

        Object[] message = {
                "Usuario:", txtUsuario,
                "Contraseña:", txtContrasenia
        };

        int option = JOptionPane.showConfirmDialog(
                this,
                message,
                "Login de Administrador",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            String usuario = txtUsuario.getText();
            String contrasenia = new String(txtContrasenia.getPassword());

            if (usuario.equals("admin") && contrasenia.equals("1234")) {
                setWestPanel(pnlAdmin);
                setPanel(new PHome());
            } else {
                AppMSG.showError("Credenciales incorrectas. Acceso denegado.");
            }
        }
    }
}