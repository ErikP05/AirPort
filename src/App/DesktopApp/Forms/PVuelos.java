package App.DesktopApp.Forms;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import javax.swing.*;

import App.DesktopApp.CustomControl.PatButton;
import App.DesktopApp.CustomControl.PatLabel;
import App.DesktopApp.CustomControl.PatTextBox;
import BusinessLogic.Entities.VueloBL;
import DataAccess.DTOs.AvionDTO;
import DataAccess.DTOs.PaisDestinoDTO;
import DataAccess.DTOs.PaisOrigenDTO;
import DataAccess.DTOs.VueloDTO;
import Infrastructure.AppMSG;

public class PVuelos extends JPanel {
    private VueloBL vueloBL = new VueloBL();
    private VueloDTO vueloActual = null;

    private PatTextBox txtIdVuelo = new PatTextBox();
    private JComboBox<ComboItem> cbPaisOrigen = new JComboBox<>();
    private JComboBox<ComboItem> cbPaisDestino = new JComboBox<>();
    private JComboBox<ComboItem> cbAvion = new JComboBox<>();
    private PatTextBox txtFechaVuelo = new PatTextBox();
    private PatTextBox txtHoraVuelo = new PatTextBox();
    private PatTextBox txtEstado = new PatTextBox();
    private JTable tablaDatos = new JTable();
    private JScrollPane scrollPane = new JScrollPane(tablaDatos);

    private PatButton btnNuevo = new PatButton("Nuevo");
    private PatButton btnGuardar = new PatButton("Guardar");
    private PatButton btnEliminar = new PatButton("Eliminar");

    public PVuelos() {
        initComponents();
        cargarCombos();
        cargarTabla();

        // Eventos
        btnNuevo.addActionListener(e -> limpiarFormulario());
        btnGuardar.addActionListener(e -> btnGuardarClick());
        btnEliminar.addActionListener(e -> btnEliminarClick());

        tablaDatos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                filaSeleccionada();
            }
        });
    }

    private void cargarCombos() {
        try {
            cbPaisOrigen.removeAllItems();
            cbPaisDestino.removeAllItems();
            cbAvion.removeAllItems();

            for (PaisOrigenDTO p : vueloBL.getListaPaisOrigen()) {
                cbPaisOrigen.addItem(new ComboItem(p.getIdPaisOrigen(), p.getNombre()));
            }
            for (PaisDestinoDTO p : vueloBL.getListaPaisDestino()) {
                cbPaisDestino.addItem(new ComboItem(p.getIdPaisDestino(), p.getNombre()));
            }
            for (AvionDTO a : vueloBL.getListaAviones()) {
                cbAvion.addItem(new ComboItem(a.getIdAvion(), a.getSerie()));
            }
        } catch (Exception e) {
            AppMSG.showError("Error al cargar listas: " + e.getMessage());
        }
    }

    private void cargarTabla() {
        try {
            String[] columnas = { "ID", "Origen", "Destino", "Avión" };
            Object[][] datos = vueloBL.getVuelosGridData();

            tablaDatos.setModel(new javax.swing.table.DefaultTableModel(datos, columnas) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            });

        } catch (Exception e) {
            AppMSG.showError("Error al cargar tabla: " + e.getMessage());
        }
    }

    private void btnGuardarClick() {
        try {
            ComboItem itemOrigen = (ComboItem) cbPaisOrigen.getSelectedItem();
            ComboItem itemDestino = (ComboItem) cbPaisDestino.getSelectedItem();
            ComboItem itemAvion = (ComboItem) cbAvion.getSelectedItem();
            String fechaVuelo = txtFechaVuelo.getText().trim();
            String horaVuelo = txtHoraVuelo.getText().trim();

            if (itemOrigen == null || itemDestino == null || itemAvion == null || fechaVuelo.isEmpty() || horaVuelo.isEmpty()) {
                AppMSG.showError("Seleccione origen, destino, avión y escriba la fecha (YYYY-MM-DD) y hora (HH:MM) del vuelo");
                return;
            }

            if (vueloActual == null)
                vueloActual = new VueloDTO();

            vueloActual.setIdPaisOrigen(itemOrigen.getId());
            vueloActual.setIdPaisDestino(itemDestino.getId());
            vueloActual.setIdAvion(itemAvion.getId());
            
            try {
                vueloActual.setFechaVuelo(LocalDate.parse(fechaVuelo));
            } catch (DateTimeParseException ex) {
                AppMSG.showError("Formato de fecha inválido. Use YYYY-MM-DD");
                return;
            }
            
            try {
                vueloActual.setHoraVuelo(LocalTime.parse(horaVuelo));
            } catch (DateTimeParseException ex) {
                AppMSG.showError("Formato de hora inválido. Use HH:MM (ejemplo: 14:30)");
                return;
            }

            boolean exito;
            if (vueloActual.getIdVuelo() == null || vueloActual.getIdVuelo() == 0) {
                exito = vueloBL.add(vueloActual);
            } else {
                exito = vueloBL.update(vueloActual);
            }

            if (exito) {
                AppMSG.show("Vuelo guardado correctamente");
                limpiarFormulario();
                vueloBL.refreshCache();
                cargarTabla();
            } else {
                AppMSG.showError("No se pudo guardar");
            }

        } catch (Exception e) {
            AppMSG.showError("Error al guardar: " + e.getMessage());
        }
    }

    private void btnEliminarClick() {
        try {
            if (vueloActual != null && vueloActual.getIdVuelo() != null && 
                AppMSG.showConfirmYesNo("¿Eliminar vuelo " + vueloActual.getIdVuelo() + "?")) {
                vueloBL.delete(vueloActual.getIdVuelo());
                limpiarFormulario();
                cargarTabla();
            }
        } catch (Exception e) {
            AppMSG.showError(e.getMessage());
        }
    }

    private void filaSeleccionada() {
        int row = tablaDatos.getSelectedRow();
        if (row >= 0) {
            try {
                // Obtenemos el ID de la primera columna oculta/visible
                Integer id = (Integer) tablaDatos.getValueAt(row, 0);
                vueloActual = vueloBL.getById(id);

                // Setear campos
                txtIdVuelo.setText(vueloActual.getIdVuelo().toString());
                txtEstado.setText(vueloActual.getEstado());
                txtFechaVuelo.setText(vueloActual.getFechaVuelo() != null ? vueloActual.getFechaVuelo().toString() : "");
                txtHoraVuelo.setText(vueloActual.getHoraVuelo() != null ? vueloActual.getHoraVuelo().toString() : "");

                // Seleccionar items correctos en los combos
                seleccionarComboPorId(cbPaisOrigen, vueloActual.getIdPaisOrigen());
                seleccionarComboPorId(cbPaisDestino, vueloActual.getIdPaisDestino());
                seleccionarComboPorId(cbAvion, vueloActual.getIdAvion());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Helper para buscar ID en el combo y seleccionarlo
    private void seleccionarComboPorId(JComboBox<ComboItem> combo, Integer id) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (combo.getItemAt(i).getId().equals(id)) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    private void limpiarFormulario() {
        vueloActual = null;
        txtIdVuelo.setText("");
        txtEstado.setText("");
        txtFechaVuelo.setText("");
        txtHoraVuelo.setText("");
        if (cbPaisOrigen.getItemCount() > 0)
            cbPaisOrigen.setSelectedIndex(0);
        if (cbPaisDestino.getItemCount() > 0)
            cbPaisDestino.setSelectedIndex(0);
        if (cbAvion.getItemCount() > 0)
            cbAvion.setSelectedIndex(0);
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        JPanel pnlForm = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        pnlForm.add(new PatLabel("ID Vuelo:"), gbc);
        gbc.gridx = 1;
        txtIdVuelo.setEnabled(false);
        pnlForm.add(txtIdVuelo, gbc);
        gbc.gridx = 2;
        pnlForm.add(new PatLabel("Estado:"), gbc);
        gbc.gridx = 3;
        pnlForm.add(txtEstado, gbc);

        // Fila 1: Origen
        gbc.gridx = 0;
        gbc.gridy = 1;
        pnlForm.add(new PatLabel("Origen:"), gbc);
        gbc.gridx = 1;
        pnlForm.add(cbPaisOrigen, gbc);

        // Fila 2: Destino
        gbc.gridx = 0;
        gbc.gridy = 2;
        pnlForm.add(new PatLabel("Destino:"), gbc);
        gbc.gridx = 1;
        pnlForm.add(cbPaisDestino, gbc);

        // Fila 3: Avion
        gbc.gridx = 0;
        gbc.gridy = 3;
        pnlForm.add(new PatLabel("Avión:"), gbc);
        gbc.gridx = 1;
        pnlForm.add(cbAvion, gbc);

        // Fila 4: FechaVuelo
        gbc.gridx = 0;
        gbc.gridy = 4;
        pnlForm.add(new PatLabel("Fecha Vuelo (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        pnlForm.add(txtFechaVuelo, gbc);

        // Fila 5: HoraVuelo
        gbc.gridx = 0;
        gbc.gridy = 5;
        pnlForm.add(new PatLabel("Hora Vuelo (HH:MM):"), gbc);
        gbc.gridx = 1;
        pnlForm.add(txtHoraVuelo, gbc);

        // Fila 6: Botones
        JPanel pnlBotones = new JPanel();
        pnlBotones.add(btnNuevo);
        pnlBotones.add(btnGuardar);
        pnlBotones.add(btnEliminar);
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 4;
        pnlForm.add(pnlBotones, gbc);

        add(pnlForm, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    // --- CLASE AUXILIAR PARA LOS COMBOBOX ---
    // Permite guardar ID y mostrar Texto
    class ComboItem {
        private Integer id;
        private String texto;

        public ComboItem(Integer id, String texto) {
            this.id = id;
            this.texto = texto;
        }

        public Integer getId() {
            return id;
        }

        @Override
        public String toString() {
            return texto;
        } // Esto es lo que muestra el JComboBox
    }
}