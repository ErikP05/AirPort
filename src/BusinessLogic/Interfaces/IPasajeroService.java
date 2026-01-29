package BusinessLogic.Interfaces;

public interface IPasajeroService {
	boolean registrarPasajero(String nombre, String apellido, String cedula, String email, String uidRfid, String fechaNacimiento, int idSexo) throws Exception;
	boolean eliminarPasajero(int idPasajero) throws Exception;
	boolean actualizarPasajero(int idPasajero, String nombre, String apellido, String email) throws Exception;
	boolean actualizarUidRfid(int idPasajero, String nuevoUidRfid) throws Exception;
}
