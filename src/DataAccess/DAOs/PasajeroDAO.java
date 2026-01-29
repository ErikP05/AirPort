package DataAccess.DAOs;

import DataAccess.DTOs.PasajeroDTO;
import DataAccess.Helpers.DataHelperSQLiteDAO;
import Infrastructure.AppException;

public class PasajeroDAO extends DataHelperSQLiteDAO<PasajeroDTO> {
    public PasajeroDAO() throws AppException {
        super(PasajeroDTO.class, "Pasajero", "IdPasajero");
    }

    public PasajeroDTO readByUidRfid(String uidRfid) throws AppException {
        String sql = "SELECT * FROM Pasajero WHERE UidRfid = ? AND Estado = 'A'";
        try (java.sql.PreparedStatement stmt = openConnection().prepareStatement(sql)) {
            stmt.setString(1, uidRfid);
            try (java.sql.ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapResultSetToEntity(rs) : null;
            }
        } catch (java.sql.SQLException e) {
            throw new AppException(null, e, getClass(), "readByUidRfid");
        }
    }

    public PasajeroDTO readByCedula(String cedula) throws AppException {
        String sql = "SELECT * FROM Pasajero WHERE Cedula = ? AND Estado = 'A'";
        try (java.sql.PreparedStatement stmt = openConnection().prepareStatement(sql)) {
            stmt.setString(1, cedula);
            try (java.sql.ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapResultSetToEntity(rs) : null;
            }
        } catch (java.sql.SQLException e) {
            throw new AppException(null, e, getClass(), "readByCedula");
        }
    }

    /**
     * Busca un pasajero por UID RFID sin importar su estado (incluyendo eliminados)
     * Útil para diagnosticar problemas con UIDs que aparentemente no se pueden usar
     */
    public PasajeroDTO readByUidRfidAnyState(String uidRfid) throws AppException {
        String sql = "SELECT * FROM Pasajero WHERE UidRfid = ?";
        try (java.sql.PreparedStatement stmt = openConnection().prepareStatement(sql)) {
            stmt.setString(1, uidRfid);
            try (java.sql.ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapResultSetToEntity(rs) : null;
            }
        } catch (java.sql.SQLException e) {
            throw new AppException(null, e, getClass(), "readByUidRfidAnyState");
        }
    }

    /**
     * Elimina un pasajero y todos sus registros relacionados en cascada.
     * Elimina en orden:
     * 1. RFIDTag asociados al pasajero
     * 2. CheckInLog asociados al pasajero
     * 3. Reservas asociadas al pasajero
     * 4. El pasajero mismo
     * 
     * Nota: Esta es una eliminación lógica (marca Estado = 'X')
     */
    public boolean deleteCascade(Integer idPasajero) throws AppException {
        try {
            java.sql.Connection conn = openConnection();
            conn.setAutoCommit(false); // Iniciar transacción
            
            try {
                String timestamp = getDataTimeNow();
                
                // 1. Eliminar RFIDTags
                String sqlRFID = "UPDATE RFIDTag SET Estado = 'X', FechaModifica = ? WHERE IdPasajero = ?";
                try (java.sql.PreparedStatement stmt = conn.prepareStatement(sqlRFID)) {
                    stmt.setString(1, timestamp);
                    stmt.setInt(2, idPasajero);
                    stmt.executeUpdate();
                }
                
                // 2. Eliminar CheckInLog (si tiene referencia a IdPasajero)
                String sqlCheckIn = "UPDATE CheckInLog SET Estado = 'X', FechaModifica = ? WHERE IdPasajero = ?";
                try (java.sql.PreparedStatement stmt = conn.prepareStatement(sqlCheckIn)) {
                    stmt.setString(1, timestamp);
                    stmt.setInt(2, idPasajero);
                    stmt.executeUpdate();
                }
                
                // 3. Eliminar Reservas
                String sqlReserva = "UPDATE Reserva SET Estado = 'X', FechaModifica = ? WHERE IdPasajero = ?";
                try (java.sql.PreparedStatement stmt = conn.prepareStatement(sqlReserva)) {
                    stmt.setString(1, timestamp);
                    stmt.setInt(2, idPasajero);
                    stmt.executeUpdate();
                }
                
                // 4. Eliminar Pasajero
                String sqlPasajero = "UPDATE Pasajero SET Estado = 'X', FechaModifica = ? WHERE IdPasajero = ?";
                try (java.sql.PreparedStatement stmt = conn.prepareStatement(sqlPasajero)) {
                    stmt.setString(1, timestamp);
                    stmt.setInt(2, idPasajero);
                    int result = stmt.executeUpdate();
                    
                    if (result > 0) {
                        conn.commit(); // Confirmar transacción
                        return true;
                    } else {
                        conn.rollback(); // Revertir si no se eliminó el pasajero
                        return false;
                    }
                }
                
            } catch (java.sql.SQLException e) {
                conn.rollback(); // Revertir en caso de error
                throw e;
            } finally {
                conn.setAutoCommit(true); // Restaurar auto-commit
            }
            
        } catch (java.sql.SQLException e) {
            throw new AppException("Error al eliminar el pasajero y sus registros relacionados", e, getClass(), "deleteCascade");
        } finally {
            // Ejecutar limpieza DESPUÉS de que la transacción termine
            ejecutarLimpiezaAutomaticaAsync();
        }
    }
    
    /**
     * Ejecuta limpieza automática de forma asíncrona (sin VACUUM)
     * Para evitar problemas con transacciones activas
     */
    private void ejecutarLimpiezaAutomaticaAsync() {
        new Thread(() -> {
            try {
                // Pequeña espera para asegurar que la transacción terminó
                Thread.sleep(100);
                
                BusinessLogic.Entities.CleanupService cleanupService = 
                    new BusinessLogic.Entities.CleanupService();
                
                // Ejecutar sin VACUUM para evitar conflictos con transacciones
                int eliminados = cleanupService.ejecutarLimpieza(false);
                if (eliminados > 0) {
                    System.out.println("🧹 Limpieza automática: " + eliminados + " registros eliminados físicamente");
                }
            } catch (Exception e) {
                System.err.println("⚠ Advertencia: Error en limpieza automática - " + e.getMessage());
            }
        }).start();
    }
}