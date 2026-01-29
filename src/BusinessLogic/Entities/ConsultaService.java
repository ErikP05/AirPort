package BusinessLogic.Entities;

import DataAccess.DAOs.VueloDAO;
import DataAccess.DAOs.ReservaDAO;
import DataAccess.DAOs.PasajeroDAO;
import DataAccess.DTOs.VueloDTO;
import DataAccess.DTOs.ReservaDTO;
import DataAccess.DTOs.PasajeroDTO;
import Infrastructure.AppException;
import java.util.*;

public class ConsultaService {
    private final VueloDAO vueloDAO;
    private final ReservaDAO reservaDAO;
    private final PasajeroDAO pasajeroDAO;

    public ConsultaService() throws AppException {
        this.vueloDAO = new VueloDAO();
        this.reservaDAO = new ReservaDAO();
        this.pasajeroDAO = new PasajeroDAO();
    }

    public List<VueloDTO> listarVuelos() throws AppException {
        return vueloDAO.readAll();
    }

    // busca todos los pasajeros de un vuelo
    public List<PasajeroDTO> listarPasajerosPorVuelo(int idVuelo) throws AppException {
        List<ReservaDTO> reservas = reservaDAO.readAll();
        List<PasajeroDTO> pasajeros = new ArrayList<>();
        for (ReservaDTO r : reservas) {
            if (r.getIdVuelo() != null && r.getIdVuelo() == idVuelo) {
                PasajeroDTO p = pasajeroDAO.readBy(r.getIdPasajero());
                if (p != null)
                    pasajeros.add(p);
            }
        }
        return pasajeros;
    }

    // dependiendo del vuelo devuelve los asiento ocupados
    public int contarAsientosOcupados(int idVuelo) throws AppException {
        List<ReservaDTO> reservas = reservaDAO.readAll();
        int ocupados = 0;
        for (ReservaDTO r : reservas) {
            if (r.getIdVuelo() != null && r.getIdVuelo() == idVuelo && r.getAsiento() != null
                    && "A".equals(r.getEstado())) {
                ocupados++;
            }
        }
        return ocupados;
    }

    // mira cuantos asientos van quedando para determinar si ya no hay asientos
    // disponibles
    public Map<String, String> asientosPorVuelo(int idVuelo) throws AppException {
        List<ReservaDTO> reservas = reservaDAO.readAll();
        Map<String, String> asientos = new HashMap<>();
        for (ReservaDTO r : reservas) {
            if (r.getIdVuelo() != null && r.getIdVuelo() == idVuelo && r.getAsiento() != null) {
                asientos.put(r.getAsiento(), r.getEstado());
            }
        }
        return asientos;
    }
}
