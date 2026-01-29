package App.DesktopApp.Forms;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import App.DesktopApp.CustomControl.PatButton;
import App.DesktopApp.CustomControl.PatLabel;
import App.DesktopApp.CustomControl.PatTextBox;
import BusinessLogic.FactoryBL;
import DataAccess.DTOs.AvionDTO;
import DataAccess.DAOs.AvionDAO;
import Infrastructure.AppMSG;
import Infrastructure.AppStyle;
import Infrastructure.Tools.CMD;

public class PAvion extends JPanel implements ActionListener {
    private transient FactoryBL<AvionDTO> blFactory = new FactoryBL<>(AvionDAO.class);
    private transient AvionDTO dtoAvion = new AvionDTO();
    private Integer regAct = 0;
    private Integer regMax = 0;

    public PAvion() {
        try {
            initComponents();

            btnRowIni.addActionListener(this);
            btnRowAnt.addActionListener(this);
            btnRowSig.addActionListener(this);
            btnRowFin.addActionListener(this);

            btnNuevo.addActionListener(e -> btnNuevoClick());
            btnGuardar.addActionListener(e -> btnGuardarClick());
            btnEliminar.addActionListener(e -> btnEliminarClick());
            btnCancelar.addActionListener(e -> btnCancelarClick());

            loadRowData();
            showRowData();
            showDataTable();
        } catch (Exception e) {
            AppMSG.show("Error al cargar los datos tipo de persona: " + e.getMessage());
        }
    }

    private void loadRowData() throws Exception {
        regAct = blFactory.getMinReg("idAvion");
        regMax = blFactory.getMaxReg("idAvion");
        dtoAvion = blFactory.getBy(regAct);
    }

    private void showRowData() {
        boolean isDTONull = (dtoAvion == null || dtoAvion.getIdAvion() == null);
        txtIdAvion.setText((isDTONull) ? " " : dtoAvion.getIdAvion().toString());
        txtSerie.setText((isDTONull) ? " " : dtoAvion.getSerie());
        txtCantidadAsientos.setText((isDTONull) ? " " : dtoAvion.getCantidadAsientos().toString());
        lblTotalReg.setText(regAct.toString() + " de " + regMax);
    }

    private void showDataTable() throws Exception {
        String[] header = { "IdPT", "Serie", "N Asientos", "Estado" };
        Object[][] data = new Object[regMax][4];
        int index = 0;
        for (AvionDTO d : blFactory.getAll()) {
            data[index][0] = d.getIdAvion();
            data[index][1] = d.getSerie();
            data[index][2] = d.getCantidadAsientos();
            data[index][3] = d.getEstado();
            index++;
        }

        JTable table = new JTable(data, header);
        table.setShowHorizontalLines(true);
        table.setRowSelectionAllowed(true);
        table.setGridColor(Color.lightGray);
        table.setColumnSelectionAllowed(false);
        table.setFillsViewportHeight(true);

        pnlTabla.removeAll();
        pnlTabla.setLayout(new BorderLayout());
        pnlTabla.add(new JScrollPane(table), BorderLayout.CENTER);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (row >= 0 && col >= 0) {
                    String strID = table.getModel().getValueAt(row, 0).toString();
                    regAct = Integer.parseInt(strID);
                    try {
                        dtoAvion = blFactory.getBy(regAct);
                        showRowData();
                    } catch (Exception f) {
                        System.out.println("");
                    }
                    System.out.println("Tabla.Selected: " + strID);
                }
            }
        });
    }

    private void btnNuevoClick() {
        dtoAvion = null;
        showRowData();
    }

    private void btnGuardarClick() {
        boolean isDTONull = (dtoAvion == null);
        try {
            if (AppMSG.showConfirmYesNo("¿Seguro que desea " + ((isDTONull) ? "AGREGAR ?" : "ACTUALIZAR ?"))) {
                
                // Validar campos
                String serie = txtSerie.getText().trim();
                String cantidadStr = txtCantidadAsientos.getText().trim();
                
                if (serie.isEmpty()) {
                    AppMSG.showError("La serie del avión es obligatoria");
                    return;
                }
                
                if (cantidadStr.isEmpty()) {
                    AppMSG.showError("La cantidad de asientos es obligatoria");
                    return;
                }
                
                int cantidadAsientos;
                try {
                    cantidadAsientos = Integer.parseInt(cantidadStr);
                    if (cantidadAsientos <= 0) {
                        AppMSG.showError("La cantidad de asientos debe ser mayor a 0");
                        return;
                    }
                } catch (NumberFormatException e) {
                    AppMSG.showError("La cantidad de asientos debe ser un número válido");
                    return;
                }
                
                if (isDTONull) {
                    dtoAvion = new AvionDTO(serie, cantidadAsientos, "");
                } else {
                    dtoAvion.setSerie(serie);
                    dtoAvion.setCantidadAsientos(cantidadAsientos);
                }

                if (!((isDTONull) ? blFactory.add(dtoAvion)
                        : blFactory.upd(dtoAvion)))
                    throw new Exception("Error al almacenar el registro");

                AppMSG.show("Avión " + (isDTONull ? "registrado" : "actualizado") + " exitosamente");
                loadRowData();
                showRowData();
                showDataTable();
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppMSG.showError("ERROR AL GUARDAR:\n\n" + e.getMessage());
        }
    }

    private void btnEliminarClick() {
        try {
            if (AppMSG.showConfirmYesNo("Seguro que desea Eliminar?")) {

                if (!blFactory.del(dtoAvion.getIdAvion()))
                    throw new Exception("Error al eliminar...!");

                loadRowData();
                showRowData();
                showDataTable();
            }
        } catch (Exception e) {
            AppMSG.showError(e.getMessage());
        }
    }

    private void btnCancelarClick() {
        try {
            if (dtoAvion == null)
                loadRowData();
            showRowData();
        } catch (Exception ex) {
            CMD.printlnError(ex.toString());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnRowIni)
            regAct = 1;
        if (e.getSource() == btnRowAnt && (regAct > 1))
            regAct--;
        if (e.getSource() == btnRowSig && (regAct < regMax))
            regAct++;
        if (e.getSource() == btnRowFin)
            regAct = regMax;
        try {
            dtoAvion = blFactory.getBy(regAct);
            showRowData();
        } catch (Exception ex) {
            CMD.printlnError(ex.toString());
        }
    }

    /************************
     * FormDesing : pat_mic
     ************************/
    int tbAncho = 550, tbAlto = 150; // tabla de datos
    private PatLabel lblTitulo = new PatLabel("TIPO DE Avion"),
            lblIdAvion = new PatLabel(" Código Avion :"),
            lblSerie = new PatLabel("*Serie de Avion:"),
            lblCantAsientos = new PatLabel("Cantidad de Asientos"),
            lblTotalReg = new PatLabel(" 0 de 0 ");
    private PatTextBox txtIdAvion = new PatTextBox(),
            txtSerie = new PatTextBox(),
            txtCantidadAsientos = new PatTextBox();
    private PatButton btnPageIni = new PatButton(" |< "),
            btnPageAnt = new PatButton(" << "),
            btnPageSig = new PatButton(" >> "),
            btnPageFin = new PatButton(" >| "),

            btnRowIni = new PatButton(" |< "),
            btnRowAnt = new PatButton(" << "),
            btnRowSig = new PatButton(" >> "),
            btnRowFin = new PatButton(" >| "),

            btnNuevo = new PatButton("Nuevo"),
            btnGuardar = new PatButton("Guardar"),
            btnCancelar = new PatButton("Cancelar"),
            btnEliminar = new PatButton("Eliminar");
    private JPanel pnlTabla = new JPanel(),
            pnlBtnRow = new JPanel(new FlowLayout()),
            pnlBtnPage = new JPanel(new FlowLayout()),
            pnlBtnCRUD = new JPanel(new FlowLayout());

    private void initComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        txtIdAvion.setEnabled(false);

        pnlBtnPage.add(btnPageIni);
        pnlBtnPage.add(btnPageAnt);
        pnlBtnPage.add(new PatLabel(" Page:( "));
        // pnlBtnPage.add(lblTotalReg); //cambiar
        pnlBtnPage.add(new PatLabel(" ) "));
        pnlBtnPage.add(btnPageSig);
        pnlBtnPage.add(btnPageFin);

        pnlBtnRow.add(btnRowIni);
        pnlBtnRow.add(btnRowAnt);
        pnlBtnRow.add(lblTotalReg);
        pnlBtnRow.add(btnRowSig);
        pnlBtnRow.add(btnRowFin);

        pnlBtnCRUD.add(btnNuevo);
        pnlBtnCRUD.add(btnGuardar);
        pnlBtnCRUD.add(btnCancelar);
        pnlBtnCRUD.add(btnEliminar);
        pnlBtnCRUD.setBorder(AppStyle.createBorderRect());

        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        add(lblTitulo, gbc);

        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        add(new JLabel("■ Sección de datos: "), gbc);
        gbc.gridy = 1;
        gbc.gridx = 1;
        add(pnlBtnPage, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.gridwidth = 3;
        gbc.ipady = tbAlto;
        gbc.ipadx = tbAncho;
        pnlTabla.add(new JLabel("Loading data..."));
        add(pnlTabla, gbc);

        gbc.ipady = 1;
        gbc.ipadx = 1;

        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(50, 0, 0, 0); // Ajusta el valor superior a 50
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(Box.createRigidArea(new Dimension(0, 0)), gbc);

        gbc.insets = new Insets(10, 0, 0, 0);

        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        add(new JLabel("■ Sección de registro: "), gbc);
        gbc.gridy = 4;
        gbc.gridx = 1;
        add(pnlBtnRow, gbc);

        gbc.gridy = 5;
        gbc.gridx = 0;
        add(lblIdAvion, gbc);
        gbc.gridy = 5;
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER; // Indica que este componente ocupa toda la fila
        add(txtIdAvion, gbc);

        gbc.gridy = 6;
        gbc.gridx = 0;
        add(lblSerie, gbc);
        gbc.gridy = 6;
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER; // Indica que este componente ocupa toda la fila
        add(txtSerie, gbc);

        gbc.gridy = 7;
        gbc.gridx = 0;
        add(lblCantAsientos, gbc);
        gbc.gridy = 7;
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER; // Indica que este componente ocupa toda la fila
        add(txtCantidadAsientos, gbc);

        // gbc.gridy = 7;
        // gbc.gridx = 1;
        // gbc.gridwidth = 2;
        // gbc.fill = GridBagConstraints.HORIZONTAL;
        // add(pnlBtnRow, gbc);

        gbc.gridy = 8;
        gbc.gridx = 0;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(30, 0, 0, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(pnlBtnCRUD, gbc);
    }
}
