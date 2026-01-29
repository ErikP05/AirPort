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
import DataAccess.DAOs.PaisDestinoDAO;
import DataAccess.DTOs.PaisDestinoDTO;
import Infrastructure.AppMSG;
import Infrastructure.AppStyle;
import Infrastructure.Tools.CMD;

public class PPaisDestino extends JPanel implements ActionListener {
    private transient FactoryBL<PaisDestinoDTO> blFactory = new FactoryBL<>(PaisDestinoDAO.class);
    private transient PaisDestinoDTO dtoPais = new PaisDestinoDTO();

    private Integer regAct = 0;
    private Integer regMax = 0;

    public PPaisDestino() {
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
            AppMSG.show("Error al cargar los datos de vuelos: " + e.getMessage());
        }
    }

    private void loadRowData() throws Exception {
        regAct = blFactory.getMinReg("idPaisDestino");
        regMax = blFactory.getMaxReg("idPaisDestino");
        dtoPais = blFactory.getBy(regAct);
    }

    private void showRowData() {
        boolean isDTONull = (dtoPais == null || dtoPais.getIdPaisDestino() == null);
        txtIdPais.setText((isDTONull) ? " " : dtoPais.getIdPaisDestino().toString());
        txtNombre.setText((isDTONull) ? " " : dtoPais.getNombre());
        lblTotalReg.setText(regAct.toString() + " de " + regMax);
    }

    private void showDataTable() throws Exception {
        String[] header = { "IdPais", "Nombre País", "Estado" };
        Object[][] data = new Object[regMax][3];
        int index = 0;
        for (PaisDestinoDTO d : blFactory.getAll()) {
            data[index][0] = d.getIdPaisDestino();
            data[index][1] = d.getNombre();
            data[index][2] = d.getEstado();
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
                        dtoPais = blFactory.getBy(regAct);
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
        dtoPais = null;
        showRowData();
    }

    private void btnGuardarClick() {
        boolean isDTONull = (dtoPais == null);
        try {
            if (AppMSG.showConfirmYesNo("¿Seguro que desea " + ((isDTONull) ? "AGREGAR ?" : "ACTUALIZAR ?"))) {
                if (isDTONull)
                    dtoPais = new PaisDestinoDTO(txtNombre.getText().trim());
                else
                    dtoPais.setNombre(txtNombre.getText());

                if (!((isDTONull) ? blFactory.add(dtoPais)
                        : blFactory.upd(dtoPais)))
                    throw new Exception("Error al almacenar el registo...");

                loadRowData();
                showRowData();
                showDataTable();
            }
        } catch (Exception e) {
            AppMSG.showError(e.getMessage());
        }
    }

    private void btnEliminarClick() {
        try {
            if (AppMSG.showConfirmYesNo("Seguro que desea Eliminar?")) {

                if (!blFactory.del(dtoPais.getIdPaisDestino()))
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
            if (dtoPais == null)
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
            dtoPais = blFactory.getBy(regAct);
            showRowData();
        } catch (Exception ex) {
            CMD.printlnError(ex.toString());
        }
    }

    /************************
     * FormDesing : pat_mic
     ************************/
    int tbAncho = 550, tbAlto = 150; // tabla de datos
    private PatLabel lblTitulo = new PatLabel("Pais"),
            lblIdPais = new PatLabel(" Código País :"),
            lblNombre = new PatLabel("*Nombre País:"),
            lblTotalReg = new PatLabel(" 0 de 0 ");
    private PatTextBox txtIdPais = new PatTextBox(),
            txtNombre = new PatTextBox();
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

        txtIdPais.setEnabled(false);

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
        add(lblIdPais, gbc);
        gbc.gridy = 5;
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER; // Indica que este componente ocupa toda la fila
        add(txtIdPais, gbc);

        gbc.gridy = 6;
        gbc.gridx = 0;
        add(lblNombre, gbc);
        gbc.gridy = 6;
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER; // Indica que este componente ocupa toda la fila
        add(txtNombre, gbc);

        // gbc.gridy = 7;
        // gbc.gridx = 1;
        // gbc.gridwidth = 2;
        // gbc.fill = GridBagConstraints.HORIZONTAL;
        // add(pnlBtnRow, gbc);

        gbc.gridy = 7;
        gbc.gridx = 0;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(30, 0, 0, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(pnlBtnCRUD, gbc);
    }
}
