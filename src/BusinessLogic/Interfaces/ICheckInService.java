package BusinessLogic.Interfaces;

public interface ICheckInService {
	boolean realizarCheckIn(int idReserva, String uidRfid) throws Exception;
	boolean validarAsientoDisponible(int idVuelo, String asiento) throws Exception;
}
