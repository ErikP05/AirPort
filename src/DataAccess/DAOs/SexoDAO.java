package DataAccess.DAOs;

import DataAccess.DTOs.SexoDTO;
import DataAccess.Helpers.DataHelperSQLiteDAO;
import Infrastructure.AppException;

public class SexoDAO extends DataHelperSQLiteDAO<SexoDTO> {
    public SexoDAO() throws AppException {
        super(SexoDTO.class, "Sexo", "IdSexo");
    }
}
