package BusinessLogic.Entities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import BusinessLogic.FactoryBL;
import DataAccess.DAOs.AvionDAO;
import DataAccess.DAOs.PaisDestinoDAO;
import DataAccess.DAOs.PaisOrigenDAO;
import DataAccess.DAOs.VueloDAO;
import DataAccess.DTOs.AvionDTO;
import DataAccess.DTOs.PaisDestinoDTO;
import DataAccess.DTOs.PaisOrigenDTO;
import DataAccess.DTOs.VueloDTO;
import Infrastructure.AppException;

public class VueloBL {
    private FactoryBL<VueloDTO> vueloFactory = new FactoryBL<>(VueloDAO.class);
    private FactoryBL<PaisOrigenDTO> paisOrigenFactory = new FactoryBL<>(PaisOrigenDAO.class);
    private FactoryBL<PaisDestinoDTO> paisDestinoFactory = new FactoryBL<>(PaisDestinoDAO.class);
    private FactoryBL<AvionDTO> avionFactory = new FactoryBL<>(AvionDAO.class);

    private Map<Integer, String> mapPaisOrigen = new HashMap<>();
    private Map<Integer, String> mapPaisDestino = new HashMap<>();
    private Map<Integer, String> mapAvion = new HashMap<>();

    public VueloBL() {
        refreshCache();
    }

    public void refreshCache() {
        try {
            for (PaisOrigenDTO p : paisOrigenFactory.getAll()) {
                mapPaisOrigen.put(p.getIdPaisOrigen(), p.getNombre());
            }
            for (PaisDestinoDTO p : paisDestinoFactory.getAll()) {
                mapPaisDestino.put(p.getIdPaisDestino(), p.getNombre());
            }
            for (AvionDTO a : avionFactory.getAll()) {
                mapAvion.put(a.getIdAvion(), a.getSerie());
            }
        } catch (AppException e) {
            e.printStackTrace();
        }
    }

    public boolean add(VueloDTO dto) throws AppException {
        return vueloFactory.add(dto);
    }

    public boolean update(VueloDTO dto) throws AppException {
        return vueloFactory.upd(dto);
    }

    public boolean delete(Integer id) throws AppException {
        return vueloFactory.del(id);
    }

    public VueloDTO getById(Integer id) throws AppException {
        return vueloFactory.getBy(id);
    }

    public Integer getNextId() throws AppException {
        return vueloFactory.getMaxReg("IdVuelo") + 1; // Simulación para mostrar en UI
    }

    public Object[][] getVuelosGridData() throws AppException {
        List<VueloDTO> lista = vueloFactory.getAll();
        Object[][] data = new Object[lista.size()][7]; // 7 columnas

        int i = 0;
        for (VueloDTO v : lista) {
            data[i][0] = v.getIdVuelo();
            // Traducir IDs usando los Mapas. Si no encuentra, muestra el ID o "Desconocido"
            data[i][1] = mapPaisOrigen.getOrDefault(v.getIdPaisOrigen(), "Desconocido (" + v.getIdPaisOrigen() + ")");
            data[i][2] = mapPaisDestino.getOrDefault(v.getIdPaisDestino(),
                    "Desconocido (" + v.getIdPaisDestino() + ")");
            data[i][3] = mapAvion.getOrDefault(v.getIdAvion(), "Avión (" + v.getIdAvion() + ")");
            data[i][4] = v.getEstado();
            data[i][5] = v.getFechaCreacion();
            data[i][6] = v.getFechaModifica();
            i++;
        }
        return data;
    }

    public List<PaisOrigenDTO> getListaPaisOrigen() throws AppException {
        return paisOrigenFactory.getAll();
    }

    public List<PaisDestinoDTO> getListaPaisDestino() throws AppException {
        return paisDestinoFactory.getAll();
    }

    public List<AvionDTO> getListaAviones() throws AppException {
        return avionFactory.getAll();
    }
}