package App.DesktopApp.Forms;

import java.awt.BorderLayout;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

import Infrastructure.AppConfig;

public class AppSplashScreen extends JFrame {
    private static final double SCALE_PERCENT = 0.5; // % del tamaño original de la imagen

    public AppSplashScreen() {
        initComponents();
    }

    private void initComponents() {
        JProgressBar prbLoading = new JProgressBar(0, 100);
        ImageIcon icoImagen = new ImageIcon(AppConfig.getImgSplash());

        // Escalar la imagen según el porcentaje
        int newWidth = (int) (icoImagen.getIconWidth() * SCALE_PERCENT);
        int newHeight = (int) (icoImagen.getIconHeight() * SCALE_PERCENT);

        Image scaledImage = icoImagen.getImage().getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        JLabel lblSplash = new JLabel(scaledIcon);

        prbLoading.setStringPainted(true);
        setUndecorated(true);
        getContentPane().add(lblSplash, BorderLayout.CENTER);
        add(prbLoading, BorderLayout.SOUTH);

        // Ajustar tamaño del frame al nuevo tamaño de la imagen
        setSize(newWidth, newHeight + prbLoading.getPreferredSize().height);
        setLocationRelativeTo(null); // Centrar en la pantalla
        setVisible(true);

        // Simulación de carga
        for (int i = 0; i <= 100; i++) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            prbLoading.setValue(i);
        }

        setVisible(false);
        dispose();
    }
}
