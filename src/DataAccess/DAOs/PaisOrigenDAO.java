package DataAccess.DAOs;

import DataAccess.DTOs.PaisOrigenDTO;
import DataAccess.Helpers.DataHelperSQLiteDAO;
import Infrastructure.AppException;

public class PaisOrigenDAO extends DataHelperSQLiteDAO<PaisOrigenDTO> {
    public PaisOrigenDAO() throws AppException {
        super(PaisOrigenDTO.class, "PaisOrigen", "IdPaisOrigen");
    }
}