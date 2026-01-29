package App.DesktopApp.Forms;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import App.DesktopApp.CustomControl.PatButton;
import App.DesktopApp.CustomControl.PatLabel;
import App.DesktopApp.CustomControl.PatTextBox;
import BusinessLogic.FactoryBL;
import DataAccess.DAOs.*;
import DataAccess.DTOs.*;
import Infrastructure.AppMSG;
import Infrastructure.AppStyle;
import Infrastructure.Tools.CMD;

public class PReserva extends JPanel implements ActionListener {
    private transient FactoryBL<ReservaDTO> blFactory = new FactoryBL<>(ReservaDAO.class);
    private transient ReservaDTO dtoReserva = new ReservaDTO();

    private transient FactoryBL<VueloDTO> blVuelo = new FactoryBL<>(VueloDAO.class);
    private transient FactoryBL<PasajeroDTO> blPasajero = new FactoryBL<>(PasajeroDAO.class);

    private List<VueloDTO> listaVuelos;
    private List<PasajeroDTO> listaPasajeros;
    private JComboBox<String> cmbVuelo = new JComboBox<>();
    private JComboBox<String> cmbPasajero = new JComboBox<>();
    private JComboBox<String> cmbEstadoCheckin = new JComboBox<>();

    private Integer regAct = 0;
    private Integer regMax = 0;

    public PReserva() {
        try {
            listaVuelos = blVuelo.getAll();
            listaPasajeros = blPasajero.getAll();
            initComponents();

            // Cargar vuelos en combo
            for (VueloDTO v : listaVuelos) {
                cmbVuelo.addItem("[" + v.getIdVuelo() + "] Vuelo");
            }

            // Cargar pasajeros en combo
            for (PasajeroDTO p : listaPasajeros) {
                cmbPasajero.addItem("[" + p.getIdPasajero() + "] " + p.getNombre() + " " + p.getApellido());
            }

            // Estados de check-in
            cmbEstadoCheckin.addItem("F - Pendiente");
            cmbEstadoCheckin.addItem("T - Realizado");

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
            AppMSG.show("Error al cargar datos: " + e.getMessage());
        }
    }

    private void loadRowData() throws Exception {
        regAct = blFactory.getMinReg("idReserva");
        regMax = blFactory.getMaxReg("idReserva");
        dtoReserva = blFactory.getBy(regAct);
    }

    private void showRowData() {
        boolean isDTONull = (dtoReserva == null || dtoReserva.getIdReserva() == null);
        txtIdReserva.setText((isDTONull) ? " " : dtoReserva.getIdReserva().toString());
        txtAsiento.setText((isDTONull) ? " " : dtoReserva.getAsiento());

        if (!isDTONull && dtoReserva.getIdVuelo() != null) {
            for (int i = 0; i < listaVuelos.size(); i++) {
                if (listaVuelos.get(i).getIdVuelo().equals(dtoReserva.getIdVuelo())) {
                    cmbVuelo.setSelectedIndex(i);
                    break;
                }
            }
        }

        if (!isDTONull && dtoReserva.getIdPasajero() != null) {
            for (int i = 0; i < listaPasajeros.size(); i++) {
                if (listaPasajeros.get(i).getIdPasajero().equals(dtoReserva.getIdPasajero())) {
                    cmbPasajero.setSelectedIndex(i);
                    break;
                }
            }
        }

        if (!isDTONull && dtoReserva.getEstadoCheckin() != null) {
            cmbEstadoCheckin.setSelectedIndex(dtoReserva.getEstadoCheckin().equals("T") ? 1 : 0);
        }

        lblTotalReg.setText(regAct.toString() + " de " + regMax);
    }

    private void showDataTable() throws Exception {
        String[] header = { "ID", "Vuelo", "Pasajero", "Asiento", "Check-in", "Estado" };
        Object[][] data = new Object[regMax][6];

        Map<Integer, String> mapaVuelos = new HashMap<>();
        for (VueloDTO v : listaVuelos) {
            mapaVuelos.put(v.getIdVuelo(), "Vuelo " + v.getIdVuelo());
        }

        Map<Integer, String> mapaPasajeros = new HashMap<>();
        for (PasajeroDTO p : listaPasajeros) {
            mapaPasajeros.put(p.getIdPasajero(), p.getNombre() + " " + p.getApellido());
        }

        int index = 0;
        for (ReservaDTO r : blFactory.getAll()) {
            data[index][0] = r.getIdReserva();
            data[index][1] = mapaVuelos.getOrDefault(r.getIdVuelo(), "N/A");
            data[index][2] = mapaPasajeros.getOrDefault(r.getIdPasajero(), "N/A");
            data[index][3] = r.getAsiento();
            data[index][4] = r.getEstadoCheckin().equals("T") ? "✓ Realizado" : "⏳ Pendiente";
            data[index][5] = r.getEstado();
            index++;
        }

        JTable table = new JTable(data, header);
        table.setShowHorizontalLines(true);
        table.setRowSelectionAllowed(true);
        table.setGridColor(Color.lightGray);
        table.setColumnSelectionAllowed(false);
        table.setFillsViewportHeight(true);

        // Color coding para check-in
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (column == 4 && value != null) {
                    if (value.toString().contains("Realizado")) {
                        c.setForeground(new Color(0, 150, 0));
                    } else {
                        c.setForeground(new Color(200, 100, 0));
                    }
                } else {
                    c.setForeground(Color.BLACK);
                }
                return c;
            }
        });

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
                        dtoReserva = blFactory.getBy(regAct);
                        showRowData();
                    } catch (Exception f) {
                        CMD.printlnError(f.toString());
                    }
                }
            }
        });
    }

    private void btnNuevoClick() {
        dtoReserva = null;
        showRowData();
    }

    private void btnGuardarClick() {
        boolean isNuevo = (dtoReserva == null);
        try {
            if (AppMSG.showConfirmYesNo("¿Seguro que desea " + ((isNuevo) ? "AGREGAR ?" : "ACTUALIZAR ?"))) {

                int indexVuelo = cmbVuelo.getSelectedIndex();
                int indexPasajero = cmbPasajero.getSelectedIndex();
                int idVuelo = listaVuelos.get(indexVuelo).getIdVuelo();
                int idPasajero = listaPasajeros.get(indexPasajero).getIdPasajero();
                String asiento = txtAsiento.getText().trim();
                String estadoCheckin = cmbEstadoCheckin.getSelectedIndex() == 1 ? "T" : "F";

                // Validar asiento duplicado
                if (isNuevo && !validarAsientoDisponible(idVuelo, asiento)) {
                    AppMSG.showError("El asiento " + asiento + " ya está ocupado en este vuelo");
                    return;
                }

                if (isNuevo) {
                    ReservaDTO nuevaReserva = new ReservaDTO(0, idVuelo, idPasajero, estadoCheckin, asiento);
                    blFactory.create(nuevaReserva);
                } else {
                    dtoReserva.setIdVuelo(idVuelo);
                    dtoReserva.setIdPasajero(idPasajero);
                    dtoReserva.setAsiento(asiento);
                    dtoReserva.setEstadoCheckin(estadoCheckin);
                    blFactory.upd(dtoReserva);
                }

                loadRowData();
                showRowData();
                showDataTable();
            }
        } catch (Exception e) {
            AppMSG.showError(e.getMessage());
        }
    }

    private boolean validarAsientoDisponible(int idVuelo, String asiento) throws Exception {
        for (ReservaDTO r : blFactory.getAll()) {
            if (r.getIdVuelo() == idVuelo && asiento.equalsIgnoreCase(r.getAsiento()) && "A".equals(r.getEstado())) {
                return false;
            }
        }
        return true;
    }

    private void btnEliminarClick() {
        try {
            if (AppMSG.showConfirmYesNo("¿Seguro que desea Eliminar?")) {
                if (!blFactory.del(dtoReserva.getIdReserva()))
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
            if (dtoReserva == null)
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
            dtoReserva = blFactory.getBy(regAct);
            showRowData();
        } catch (Exception ex) {
            CMD.printlnError(ex.toString());
        }
    }

    /************************
     * FormDesign
     ************************/
    int tbAncho = 550, tbAlto = 170;
    private PatLabel lblTitulo = new PatLabel("📋 Gestión de Reservas"),
            lblIdReserva = new PatLabel(" Código Reserva:"),
            lblVuelo = new PatLabel("*Vuelo:"),
            lblPasajero = new PatLabel("*Pasajero:"),
            lblAsiento = new PatLabel("*Asiento:"),
            lblEstadoCheckin = new PatLabel("*Estado Check-in:"),
            lblTotalReg = new PatLabel(" 0 de 0 ");
    private PatTextBox txtIdReserva = new PatTextBox(),
            txtAsiento = new PatTextBox();
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

        txtIdReserva.setEnabled(false);

        pnlBtnPage.add(btnPageIni);
        pnlBtnPage.add(btnPageAnt);
        pnlBtnPage.add(new PatLabel(" Page:( "));
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
        gbc.insets = new Insets(50, 0, 0, 0);
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
        add(lblIdReserva, gbc);
        gbc.gridy = 5;
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        add(txtIdReserva, gbc);

        gbc.gridy = 6;
        gbc.gridx = 0;
        add(lblVuelo, gbc);
        gbc.gridy = 6;
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        add(cmbVuelo, gbc);

        gbc.gridy = 7;
        gbc.gridx = 0;
        add(lblPasajero, gbc);
        gbc.gridy = 7;
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        add(cmbPasajero, gbc);

        gbc.gridy = 8;
        gbc.gridx = 0;
        add(lblAsiento, gbc);
        gbc.gridy = 8;
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        add(txtAsiento, gbc);

        gbc.gridy = 9;
        gbc.gridx = 0;
        add(lblEstadoCheckin, gbc);
        gbc.gridy = 9;
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        add(cmbEstadoCheckin, gbc);

        gbc.gridy = 10;
        gbc.gridx = 0;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(30, 0, 0, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(pnlBtnCRUD, gbc);
    }
}
