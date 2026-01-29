package DataAccess.DAOs;

import DataAccess.DTOs.ReservaDTO;
import DataAccess.Helpers.DataHelperSQLiteDAO;
import Infrastructure.AppException;

public class ReservaDAO extends DataHelperSQLiteDAO<ReservaDTO> {
    public ReservaDAO() throws AppException {
        super(ReservaDTO.class, "Reserva", "IdReserva");
    }
}
