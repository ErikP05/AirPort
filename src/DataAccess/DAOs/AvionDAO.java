package DataAccess.DAOs;

import DataAccess.DTOs.AvionDTO;
import DataAccess.Helpers.DataHelperSQLiteDAO;
import Infrastructure.AppException;

public class AvionDAO extends DataHelperSQLiteDAO<AvionDTO> {
    public AvionDAO() throws AppException {
        super(AvionDTO.class, "Avion", "IdAvion");
    }
}