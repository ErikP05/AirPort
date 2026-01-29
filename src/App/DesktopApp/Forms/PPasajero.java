package App.DesktopApp.Forms;

import javax.swing.JComboBox;
import DataAccess.DAOs.SexoDAO;
import DataAccess.DTOs.SexoDTO;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import BusinessLogic.Interfaces.IPasajeroService;
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
import DataAccess.DAOs.PasajeroDAO;
import DataAccess.DTOs.PasajeroDTO;
import Infrastructure.AppMSG;
import Infrastructure.AppStyle;
import Infrastructure.Tools.CMD;

public class PPasajero extends JPanel implements ActionListener {
    private transient FactoryBL<PasajeroDTO> blFactory = new FactoryBL<>(PasajeroDAO.class);
    private transient PasajeroDTO dtoPasajero = new PasajeroDTO();
    private transient FactoryBL<SexoDTO> blSexo = new FactoryBL<>(SexoDAO.class);
    private transient IPasajeroService pasajeroService;

    private List<SexoDTO> listaSexos;
    private JComboBox<String> cmbSexo = new JComboBox<>();

    private Integer regAct = 0;
    private Integer regMax = 0;

    public PPasajero() {
        try {
            listaSexos = blSexo.getAll();
            pasajeroService = blFactory.createPasajeroService();
            initComponents();

            for (SexoDTO s : listaSexos) {
                cmbSexo.addItem(s.getNombre());
            }

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
            e.printStackTrace();
            AppMSG.show("Error al cargar los datos tipo de persona: " + e.getMessage());
        }
    }

    private void loadRowData() throws Exception {
        regAct = blFactory.getMinReg("idPasajero");
        regMax = blFactory.getMaxReg("idPasajero");
        dtoPasajero = blFactory.getBy(regAct);
    }

    private void showRowData() {
        boolean isDTONull = (dtoPasajero == null || dtoPasajero.getIdPasajero() == null);
        txtIdPasajero.setText((isDTONull) ? " " : dtoPasajero.getIdPasajero().toString());
        txtNombre.setText((isDTONull) ? " " : dtoPasajero.getNombre());
        txtApellido.setText((isDTONull) ? " " : dtoPasajero.getApellido());
        txtCedula.setText((isDTONull) ? " " : dtoPasajero.getCedula());
        txtUidRfid.setText((isDTONull) ? " " : dtoPasajero.getUidRfid());
        txtEmail.setText((isDTONull) ? " " : dtoPasajero.getEmail());
        txtFechaNacimiento.setText((isDTONull) ? " " : dtoPasajero.getFechaNacimiento());

        if (!isDTONull && dtoPasajero.getIdSexo() != null) {
            for (int i = 0; i < listaSexos.size(); i++) {
                if (listaSexos.get(i).getIdSexo().equals(dtoPasajero.getIdSexo())) {
                    cmbSexo.setSelectedIndex(i);
                    break;
                }
            }
        }
        lblTotalReg.setText(regAct.toString() + " de " + regMax);
    }

    private void showDataTable() throws Exception {
        String[] header = { "IdPT", "Nombre", "Apellido", "Cedula", "Sexo", "Uid-Rfid", "Email", "FechaNcimiento" };
        Object[][] data = new Object[regMax][9];
        Map<Integer, String> mapaSexos = new HashMap<>();
        for (SexoDTO s : listaSexos) {
            mapaSexos.put(s.getIdSexo(), s.getNombre());
        }

        int index = 0;
        for (PasajeroDTO d : blFactory.getAll()) {
            data[index][0] = d.getIdPasajero();
            data[index][1] = d.getNombre();
            data[index][2] = d.getApellido();
            data[index][3] = d.getCedula();
            String nombreSexo = mapaSexos.getOrDefault(d.getIdSexo(), "Desc.");
            data[index][4] = nombreSexo;
            data[index][5] = d.getUidRfid();
            data[index][6] = d.getEmail();
            data[index][7] = d.getFechaNacimiento();

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
                        dtoPasajero = blFactory.getBy(regAct);
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
        dtoPasajero = null;
        showRowData();
    }

    private void btnGuardarClick() {
        boolean isNuevo = (dtoPasajero == null); // Determinar si es nuevo
        try {
            if (AppMSG.showConfirmYesNo("¿Seguro que desea " + ((isNuevo) ? "AGREGAR ?" : "ACTUALIZAR ?"))) {

                int indexSeleccionado = cmbSexo.getSelectedIndex();
                int idSexoSeleccionado = listaSexos.get(indexSeleccionado).getIdSexo();

                if (isNuevo) {

                    pasajeroService.registrarPasajero(
                            txtNombre.getText().trim(),
                            txtApellido.getText().trim(),
                            txtCedula.getText().trim(),
                            txtEmail.getText().trim(),
                            txtUidRfid.getText().trim(),
                            txtFechaNacimiento.getText().trim(),
                            idSexoSeleccionado);
                    
                    AppMSG.show("Pasajero registrado exitosamente");
                } else {
                    pasajeroService.actualizarPasajero(
                            dtoPasajero.getIdPasajero(),
                            txtNombre.getText().trim(),
                            txtApellido.getText().trim(),
                            txtEmail.getText().trim());
                    
                    AppMSG.show("Pasajero actualizado exitosamente");
                }
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
            if (AppMSG.showConfirmYesNo("¿Seguro que desea ELIMINAR este pasajero?\n\n" +
                    "ADVERTENCIA: Se eliminarán también:\n" +
                    "- Todas las reservas asociadas\n" +
                    "- Tags RFID vinculados\n" +
                    "- Registros de check-in\n\n" +
                    "Esta acción NO se puede deshacer.")) {
                
                // Usar el servicio con eliminación en cascada
                if (!pasajeroService.eliminarPasajero(dtoPasajero.getIdPasajero()))
                    throw new Exception("Error al eliminar el pasajero.");

                AppMSG.show("Pasajero y registros relacionados eliminados exitosamente");
                loadRowData();
                showRowData();
                showDataTable();
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppMSG.showError("ERROR AL ELIMINAR:\n\n" + e.getMessage());
        }
    }

    private void btnCancelarClick() {
        try {
            if (dtoPasajero == null)
                loadRowData();
            showRowData();
        } catch (Exception ex) {
            ex.printStackTrace();
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
            dtoPasajero = blFactory.getBy(regAct);
            showRowData();
        } catch (Exception ex) {
            CMD.printlnError(ex.toString());
        }
    }

    /************************
     * FormDesing : pat_mic
     ************************/
    int tbAncho = 550, tbAlto = 170; // tabla de datos
    private PatLabel lblTitulo = new PatLabel("Pasajero"),
            lblIdPasajero = new PatLabel(" Código Pasajero :"),
            lblNombre = new PatLabel("*Nombre:"),
            lblApellido = new PatLabel("*Apellido:"),
            lblCedula = new PatLabel("*Cedula:"),
            lblSexo = new PatLabel("*Sexo:"),
            lblUidRfid = new PatLabel("*UidRfid"),
            lblEmail = new PatLabel("*Email"),
            lblFechaNacimiento = new PatLabel("*FechaNacimiento"),
            lblTotalReg = new PatLabel(" 0 de 0 ");
    private PatTextBox txtIdPasajero = new PatTextBox(),
            txtNombre = new PatTextBox(),
            txtApellido = new PatTextBox(),
            txtCedula = new PatTextBox(),
            txtUidRfid = new PatTextBox(),
            txtEmail = new PatTextBox(),
            txtFechaNacimiento = new PatTextBox();
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

        txtIdPasajero.setEnabled(false);

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
        add(lblIdPasajero, gbc);
        gbc.gridy = 5;
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER; // Indica que este componente ocupa toda la fila
        add(txtIdPasajero, gbc);

        gbc.gridy = 6;
        gbc.gridx = 0;
        add(lblNombre, gbc);
        gbc.gridy = 6;
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER; // Indica que este componente ocupa toda la fila
        add(txtNombre, gbc);

        gbc.gridy = 7;
        gbc.gridx = 0;
        add(lblApellido, gbc);
        gbc.gridy = 7;
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER; // Indica que este componente ocupa toda la fila
        add(txtApellido, gbc);

        gbc.gridy = 8;
        gbc.gridx = 0;
        add(lblCedula, gbc);
        gbc.gridy = 8;
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER; // Indica que este componente ocupa toda la fila
        add(txtCedula, gbc);

        gbc.gridy = 9;
        gbc.gridx = 0;
        add(lblSexo, gbc);

        gbc.gridy = 9;
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        add(cmbSexo, gbc);

        gbc.gridy = 10;
        gbc.gridx = 0;
        add(lblUidRfid, gbc);
        gbc.gridy = 10;
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER; // Indica que este componente ocupa toda la fila
        add(txtUidRfid, gbc);

        gbc.gridy = 11;
        gbc.gridx = 0;
        add(lblEmail, gbc);
        gbc.gridy = 11;
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER; // Indica que este componente ocupa toda la fila
        add(txtEmail, gbc);

        gbc.gridy = 12;
        gbc.gridx = 0;
        add(lblFechaNacimiento, gbc);
        gbc.gridy = 12;
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER; // Indica que este componente ocupa toda la fila
        add(txtFechaNacimiento, gbc);

        gbc.gridy = 13;
        gbc.gridx = 0;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(30, 0, 0, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(pnlBtnCRUD, gbc);
    }
}
