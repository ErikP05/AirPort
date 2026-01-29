package DataAccess.DAOs;

import DataAccess.DTOs.VueloDTO;
import DataAccess.Helpers.DataHelperSQLiteDAO;
import Infrastructure.AppException;

public class VueloDAO extends DataHelperSQLiteDAO<VueloDTO> {
    public VueloDAO() throws AppException {
        super(VueloDTO.class, "Vuelo", "IdVuelo");
    }
}
