package App.DesktopApp.CustomControl;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JButton;

import Infrastructure.AppStyle;

import javax.swing.ImageIcon;

public class PatButton extends JButton implements MouseListener {
    public PatButton(String text){
        customizeComponent(text);
    }
    public PatButton(String text, String iconPath){
        customizeComponent(text, iconPath);
    }

    public void customizeComponent(String text, String iconPath){ 
        setSize(20, 70);
        addMouseListener(this);
        customizeComponent(text);
        setBounds(50, 30, 90, 20); 
        setIcon(new ImageIcon(iconPath));
        setFont(AppStyle.FONT);
    }
    public void customizeComponent(String text) {
        setText(text);
        setOpaque(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setForeground(AppStyle.COLOR_FONT);
        setHorizontalAlignment(AppStyle.ALIGNMENT_LEFT);
        setFont(AppStyle.FONT);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // Este evento no se utiliza en este componente.
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // Este evento no se utiliza en este componente.
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // Este evento no se utiliza en este componente.
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        setForeground(Color.BLACK);
        setCursor(AppStyle.CURSOR_HAND);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        setForeground(Color.GRAY);
        setCursor(AppStyle.CURSOR_DEFAULT);
    }
}
