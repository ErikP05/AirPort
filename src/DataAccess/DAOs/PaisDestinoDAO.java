package DataAccess.DAOs;

import DataAccess.DTOs.PaisDestinoDTO;
import DataAccess.Helpers.DataHelperSQLiteDAO;
import Infrastructure.AppException;

public class PaisDestinoDAO extends DataHelperSQLiteDAO<PaisDestinoDTO> {
    public PaisDestinoDAO() throws AppException {
        super(PaisDestinoDTO.class, "PaisDestino", "IdPaisDestino");
    }
}