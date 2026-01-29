package BusinessLogic.Entities;

import BusinessLogic.Interfaces.IPasajeroService;
import DataAccess.DAOs.PasajeroDAO;
import DataAccess.DTOs.PasajeroDTO;
import Infrastructure.AppException;

public class Pasajeroimp implements IPasajeroService {
	private final PasajeroDAO pasajeroDAO;

	public Pasajeroimp() throws AppException {
		this.pasajeroDAO = new PasajeroDAO();
	}

	@Override
	public boolean registrarPasajero(String nombre, String apellido, String cedula, String email, String uidRfid,
			String fechaNacimiento, int idSexo) throws Exception {
		// Verificar si el UidRfid ya existe
		PasajeroDTO existenteUID = pasajeroDAO.readByUidRfid(uidRfid);
		if (existenteUID != null) {
			throw new AppException("El UID RFID '" + uidRfid + "' ya está registrado para el pasajero: " 
				+ existenteUID.getNombre() + " " + existenteUID.getApellido() 
				+ " (Cédula: " + existenteUID.getCedula() + ")");
		}
		// Verificar si la Cedula ya existe
		PasajeroDTO existenteCedula = pasajeroDAO.readByCedula(cedula);
		if (existenteCedula != null) {
			throw new AppException("La cédula '" + cedula + "' ya está registrada para el pasajero: " 
				+ existenteCedula.getNombre() + " " + existenteCedula.getApellido());
		}
		PasajeroDTO pasajero = new PasajeroDTO(idSexo, nombre, apellido, cedula, uidRfid, email, fechaNacimiento);
		return pasajeroDAO.create(pasajero);
	}

	@Override
	public boolean eliminarPasajero(int idPasajero) throws Exception {
		// Usar eliminación en cascada para eliminar también registros relacionados
		return pasajeroDAO.deleteCascade(idPasajero);
	}

	@Override
	public boolean actualizarPasajero(int idPasajero, String nombre, String apellido, String email) throws Exception {
		PasajeroDTO pasajero = pasajeroDAO.readBy(idPasajero);
		if (pasajero == null)
			return false;
		pasajero.setNombre(nombre);
		pasajero.setApellido(apellido);
		pasajero.setEmail(email);
		return pasajeroDAO.update(pasajero);
	}

	@Override
	public boolean actualizarUidRfid(int idPasajero, String nuevoUidRfid) throws Exception {
		// Verificar que el nuevo UID no esté en uso por otro pasajero
		PasajeroDTO existenteUID = pasajeroDAO.readByUidRfid(nuevoUidRfid);
		if (existenteUID != null && existenteUID.getIdPasajero() != idPasajero) {
			throw new AppException("El UID RFID '" + nuevoUidRfid + "' ya está registrado para otro pasajero: " 
				+ existenteUID.getNombre() + " " + existenteUID.getApellido() 
				+ " (Cédula: " + existenteUID.getCedula() + ")");
		}
		
		PasajeroDTO pasajero = pasajeroDAO.readBy(idPasajero);
		if (pasajero == null)
			return false;
		
		pasajero.setUidRfid(nuevoUidRfid);
		return pasajeroDAO.update(pasajero);
	}
}
