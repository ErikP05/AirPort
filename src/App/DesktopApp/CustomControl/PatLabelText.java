package App.DesktopApp.CustomControl;

import javax.swing.*;

import Infrastructure.AppStyle;

import java.awt.*;

public class PatLabelText extends JPanel{
    private PatLabel    lblEtiqueta = new PatLabel();
    private PatTextBox  txtContenido= new PatTextBox();

    public PatLabelText(String etiqueta) {
        setLayout(new BorderLayout());

        lblEtiqueta.setCustomizeComponent(  etiqueta, 
                                            AppStyle.FONT_SMALL, 
                                            AppStyle.COLOR_FONT_LIGHT, 
                                            AppStyle.ALIGNMENT_LEFT); 
        add(lblEtiqueta, BorderLayout.NORTH);
        add(txtContenido, BorderLayout.CENTER);
    }
}
