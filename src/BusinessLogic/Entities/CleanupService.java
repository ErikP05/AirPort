package BusinessLogic.Entities;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import Infrastructure.AppException;
import DataAccess.Helpers.DataHelperSQLiteDAO;

/**
 * Servicio de limpieza automática de registros marcados como eliminados (Estado='X')
 * Se ejecuta después de cada operación de eliminación lógica
 */
public class CleanupService {
    
    private static final String[] TABLES_ORDER = {
        "CheckInLog", "RFIDTag", "Reserva", "Vuelo", 
        "Pasajero", "Avion", "PaisDestino", "PaisOrigen", "Sexo"
    };
    
    /**
     * Limpia automáticamente todos los registros con Estado='X'
     * Ejecuta eliminación física de registros marcados como eliminados
     * 
     * @param ejecutarVacuum si debe ejecutar VACUUM al final (false si se llama desde transacción)
     * @return cantidad total de registros eliminados
     * @throws AppException si ocurre un error durante la limpieza
     */
    public int ejecutarLimpieza(boolean ejecutarVacuum) throws AppException {
        int totalEliminados = 0;
        
        try (Connection conn = DataHelperSQLiteDAO.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Eliminar registros en orden (respetando dependencias)
            for (String table : TABLES_ORDER) {
                try {
                    int deleted = stmt.executeUpdate(
                        "DELETE FROM " + table + " WHERE Estado = 'X'"
                    );
                    totalEliminados += deleted;
                } catch (Exception e) {
                    // Tabla puede no existir, continuar con la siguiente
                }
            }
            
            // Optimizar base de datos si se eliminaron registros y se permite VACUUM
            if (totalEliminados > 0 && ejecutarVacuum) {
                try {
                    stmt.execute("VACUUM");
                } catch (Exception e) {
                    // Si falla VACUUM (ej: dentro de transacción), solo registrar advertencia
                    System.err.println("⚠ No se pudo ejecutar VACUUM: " + e.getMessage());
                }
            }
            
            return totalEliminados;
            
        } catch (Exception e) {
            throw new AppException(
                "Error durante la limpieza automática de registros", 
                e, 
                getClass(), 
                "ejecutarLimpieza"
            );
        }
    }
    
    /**
     * Limpia automáticamente todos los registros con Estado='X'
     * Por defecto ejecuta VACUUM al finalizar
     * 
     * @return cantidad total de registros eliminados
     * @throws AppException si ocurre un error durante la limpieza
     */
    public int ejecutarLimpieza() throws AppException {
        return ejecutarLimpieza(true);
    }
    
    /**
     * Cuenta cuántos registros están pendientes de limpieza
     * 
     * @return cantidad de registros con Estado='X'
     * @throws AppException si ocurre un error
     */
    public int contarRegistrosPendientes() throws AppException {
        int total = 0;
        
        try (Connection conn = DataHelperSQLiteDAO.getConnection();
             Statement stmt = conn.createStatement()) {
            
            for (String table : TABLES_ORDER) {
                try {
                    ResultSet rs = stmt.executeQuery(
                        "SELECT COUNT(*) as cnt FROM " + table + " WHERE Estado = 'X'"
                    );
                    if (rs.next()) {
                        total += rs.getInt("cnt");
                    }
                    rs.close();
                } catch (Exception e) {
                    // Tabla puede no existir
                }
            }
            
            return total;
            
        } catch (Exception e) {
            throw new AppException(
                "Error al contar registros pendientes", 
                e, 
                getClass(), 
                "contarRegistrosPendientes"
            );
        }
    }
    
    /**
     * Verifica si hay registros pendientes de limpieza en una tabla específica
     * 
     * @param tableName nombre de la tabla
     * @return true si hay registros con Estado='X'
     */
    public boolean hayRegistrosPendientesEn(String tableName) {
        try (Connection conn = DataHelperSQLiteDAO.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT COUNT(*) as cnt FROM " + tableName + " WHERE Estado = 'X'"
             )) {
            
            return rs.next() && rs.getInt("cnt") > 0;
            
        } catch (Exception e) {
            return false; // Tabla puede no existir
        }
    }
}
