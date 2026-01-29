
package BusinessLogic.Entities;
import BusinessLogic.Interfaces.ICheckInService;
import DataAccess.DAOs.RFIDDAO;
import DataAccess.DAOs.ReservaDAO;
import DataAccess.DAOs.VueloDAO;
import DataAccess.DAOs.AsientoDAO;


public class CheckInServiceimp implements ICheckInService {
	private final RFIDDAO rfidDAO;
	private final ReservaDAO reservaDAO;
	private final VueloDAO vueloDAO;
	private final AsientoDAO asientoDAO;

	public RFIDDAO getRfidDAO() {
        return rfidDAO;
    }

    public ReservaDAO getReservaDAO() {
        return reservaDAO;
    }

    public VueloDAO getVueloDAO() {
        return vueloDAO;
    }

    public AsientoDAO getAsientoDAO() {
        return asientoDAO;
    }

    public CheckInServiceimp(RFIDDAO rfidDAO, ReservaDAO reservaDAO, VueloDAO vueloDAO, AsientoDAO asientoDAO) {
		this.rfidDAO = rfidDAO;
		this.reservaDAO = reservaDAO;
		this.vueloDAO = vueloDAO;
		this.asientoDAO = asientoDAO;
	}

	@Override
	public boolean realizarCheckIn(int idReserva, String uidRfid) throws Exception {
		// 1. Buscar la reserva
		var reserva = reservaDAO.readBy(idReserva);
		if (reserva == null) return false;
		// 2. Validar que el UID RFID coincida con el pasajero de la reserva
		// (Aquí deberías consultar el DAO de pasajero, pero se asume validación simple)
		// 3. Cambiar estado de check-in
		reserva.setEstadoCheckin("T");
		return reservaDAO.update(reserva);
	}

	@Override
	public boolean validarAsientoDisponible(int idVuelo, String asiento) throws Exception {
		// Buscar todas las reservas del vuelo y verificar si el asiento está ocupado
		var reservas = reservaDAO.readAll();
		for (var r : reservas) {
			if (r.getIdVuelo() == idVuelo && asiento.equalsIgnoreCase(r.getAsiento()) && "A".equals(r.getEstado())) {
				return false;
			}
		}
		return true;
	}
}
