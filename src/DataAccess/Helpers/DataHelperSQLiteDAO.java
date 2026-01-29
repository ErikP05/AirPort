//  © 2K26 ❱──💀──❰ pat_mic ? code is life : life is code
package DataAccess.Helpers;

import java.sql.*;
import java.util.List;

import DataAccess.Interfaces.IDAO;

import java.util.ArrayList;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import Infrastructure.AppConfig;
import Infrastructure.AppException;

public class DataHelperSQLiteDAO<T> implements IDAO<T> {
    protected final Class<T> DTOClass;
    protected final String tableName;
    protected final String tablePK;

    private static final String DBPath = AppConfig.getDATABASE();
    private static Connection conn = null;

    protected static synchronized Connection openConnection() throws SQLException {
        if (conn == null || conn.isClosed()) {
            conn = DriverManager.getConnection(DBPath);
        }
        return conn;
    }
    
    /**
     * Método público para obtener conexión desde servicios externos
     * @return conexión a la base de datos
     * @throws SQLException si hay error al conectar
     */
    public static synchronized Connection getConnection() throws SQLException {
        return openConnection();
    }

    protected static void closeConnection() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }

    protected String getDataTimeNow() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * Construye la relacion entre la clase DTO y la tabla de la base de datos
     * 
     * @param dtoClass  : Nombre de la clase DTO
     * @param tableName : Nombre de la tabla
     * @param tablePK   : Nombre del PK de la tabla
     * @throws AppException: Error al asociar la clase con la tabla
     */
    public DataHelperSQLiteDAO(Class<T> dtoClass, String tableName, String tablePK) throws AppException {
        try {
            openConnection();
        } catch (SQLException e) {
            throw new AppException(null, e, getClass(), "DataHelperSQLiteDAO");
        }
        this.DTOClass = dtoClass;
        this.tableName = tableName;
        this.tablePK = tablePK;
    }

    @Override
    public boolean create(T entity) throws AppException {
        Field[] fields = DTOClass.getDeclaredFields();
        StringBuilder columns = new StringBuilder();
        StringBuilder placeholders = new StringBuilder();

        for (Field field : fields) {
            field.setAccessible(true);
            String name = field.getName();
            // Excluir PK y campos por defecto y auditoria
            if (!name.equalsIgnoreCase(tablePK)
                    && !name.equalsIgnoreCase("Estado")
                    && !name.equalsIgnoreCase("FechaCreacion")
                    && !name.equalsIgnoreCase("FechaModifica")) {
                columns.append(name).append(",");
                placeholders.append("?,");
            }
        }

        // Eliminar la última coma
        String cols = columns.substring(0, columns.length() - 1);
        String vals = placeholders.substring(0, placeholders.length() - 1);

        String sql = String.format("INSERT INTO %s (%s) VALUES (%s)", tableName, cols, vals);

        try (PreparedStatement stmt = openConnection().prepareStatement(sql)) {
            int index = 1;
            for (Field field : fields) {
                String name = field.getName();
                if (!name.equalsIgnoreCase(tablePK)
                        && !name.equalsIgnoreCase("Estado")
                        && !name.equalsIgnoreCase("FechaCreacion")
                        && !name.equalsIgnoreCase("FechaModifica"))
                    stmt.setObject(index++, field.get(entity));
            }
            return (stmt.executeUpdate() > 0);
        } catch (SQLException | IllegalAccessException e) {
            String errorMsg = e.getMessage();
            String userMsg = null;
            
            // Detectar errores de constraint UNIQUE
            if (errorMsg.contains("UNIQUE constraint failed")) {
                if (errorMsg.contains("UidRfid")) {
                    userMsg = "El UID RFID ya está registrado. Por favor, use uno diferente.";
                } else if (errorMsg.contains("Cedula")) {
                    userMsg = "La cédula ya está registrada. Por favor, verifique los datos.";
                } else if (errorMsg.contains("Email")) {
                    userMsg = "El email ya está registrado. Por favor, use uno diferente.";
                } else {
                    userMsg = "Ya existe un registro con estos datos únicos.";
                }
            } else if (errorMsg.contains("NOT NULL constraint failed")) {
                userMsg = "Todos los campos obligatorios deben ser completados.";
            }
            
            throw new AppException(userMsg, e, getClass(), "create");
        }
    }

    @Override
    public boolean update(T entity) throws AppException {
        try {
            Field[] fields = DTOClass.getDeclaredFields();
            StringBuilder updates = new StringBuilder();
            Object pkValue = null;

            for (Field field : fields) {
                String name = field.getName();

                if (!name.equalsIgnoreCase(tablePK)) {
                    updates.append(name).append(" = ?, ");
                } else {
                    if (!field.canAccess(entity)) {
                        field.setAccessible(true);
                    }
                    pkValue = field.get(entity);
                }
            }

            updates.append("FechaModifica = ?"); // campo técnico de auditoría

            String sql = String.format("UPDATE %s SET %s WHERE %s = ?", tableName, updates, tablePK);

            try (PreparedStatement stmt = openConnection().prepareStatement(sql)) {
                int index = 1;
                for (Field field : fields) {
                    String name = field.getName();
                    if (!name.equalsIgnoreCase(tablePK)) {
                        if (!field.canAccess(entity)) {
                            field.setAccessible(true);
                        }
                        stmt.setObject(index++, field.get(entity));
                    }
                }

                stmt.setString(index++, getDataTimeNow()); // FechaModifica
                stmt.setObject(index, pkValue); // WHERE PK = ?

                return stmt.executeUpdate() > 0;
            }

        } catch (SQLException | IllegalAccessException e) {
            String errorMsg = e.getMessage();
            String userMsg = null;
            
            // Detectar errores de constraint UNIQUE
            if (errorMsg.contains("UNIQUE constraint failed")) {
                if (errorMsg.contains("UidRfid")) {
                    userMsg = "El UID RFID ya está registrado. Por favor, use uno diferente.";
                } else if (errorMsg.contains("Cedula")) {
                    userMsg = "La cédula ya está registrada. Por favor, verifique los datos.";
                } else {
                    userMsg = "Ya existe un registro con estos datos únicos.";
                }
            }
            
            throw new AppException(userMsg, e, getClass(), "update");
        }
    }

    @Override
    public boolean delete(Integer id) throws AppException {
        String sql = String.format("UPDATE %s SET Estado = ?, FechaModifica = ? WHERE %s = ?", tableName, tablePK);
        try (PreparedStatement stmt = openConnection().prepareStatement(sql)) {
            stmt.setString(1, "X");
            stmt.setString(2, getDataTimeNow());
            stmt.setInt(3, id);
            boolean resultado = stmt.executeUpdate() > 0;
            
            // Ejecutar limpieza automática después de marcar como eliminado
            if (resultado) {
                ejecutarLimpiezaAutomatica();
            }
            
            return resultado;
        } catch (SQLException e) {
            throw new AppException(null, e, getClass(), "delete");
        }
    }
    
    /**
     * Ejecuta limpieza automática de registros marcados como eliminados
     * Solo se ejecuta si hay registros pendientes para evitar VAC UUMs innecesarios
     */
    private void ejecutarLimpiezaAutomatica() {
        try {
            BusinessLogic.Entities.CleanupService cleanupService = 
                new BusinessLogic.Entities.CleanupService();
            
            // Verificar si hay registros pendientes antes de limpiar
            int pendientes = cleanupService.contarRegistrosPendientes();
            
            if (pendientes > 0) {
                int eliminados = cleanupService.ejecutarLimpieza();
                System.out.println("🧹 Limpieza automática: " + eliminados + " registros eliminados físicamente");
            }
        } catch (Exception e) {
            // No lanzar excepción para no interrumpir el flujo principal
            System.err.println("⚠ Advertencia: Error en limpieza automática - " + e.getMessage());
        }
    }

    @Override
    public T readBy(Integer id) throws AppException {
        String sql = String.format("SELECT * FROM %s WHERE %s = ? AND Estado = 'A'", tableName, tablePK);
        try (PreparedStatement stmt = openConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapResultSetToEntity(rs) : null;
            }
        } catch (SQLException e) {
            throw new AppException(null, e, getClass(), "readBy");
        }
    }

    @Override
    public List<T> readAll() throws AppException {
        List<T> list = new ArrayList<>();
        String sql = String.format("SELECT * FROM %s WHERE Estado = 'A'", tableName);
        try (PreparedStatement stmt = openConnection().prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToEntity(rs));
            }
        } catch (SQLException e) {
            throw new AppException(null, e, getClass(), "readAll");
        }
        return list;
    }

    @Override
    public Integer getMaxReg(String tableCelName) throws AppException {
        String sql = String.format("SELECT MAX(%s) FROM %s WHERE Estado = 'A'", tableCelName, tableName);
        try (PreparedStatement stmt = openConnection().prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            throw new AppException(null, e, getClass(), "getMaxReg(...)");
        }
    }

    @Override
    public Integer getMinReg(String tableCelName) throws AppException {
        String sql = String.format("SELECT MIN(%s) FROM %s WHERE Estado = 'A'", tableCelName, tableName);
        try (PreparedStatement stmt = openConnection().prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            throw new AppException(null, e, getClass(), "getMinReg(...)");
        }
    }

    @Override
    public Integer getCountReg() throws AppException {
        String sql = String.format("SELECT COUNT(*) FROM %s WHERE Estado = 'A'", tableName);
        try (PreparedStatement stmt = openConnection().prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            throw new AppException(null, e, getClass(), "getCountReg(...)");
        }
    }

    protected T mapResultSetToEntity(ResultSet rs) throws AppException {
        try {
            T instance = DTOClass.getDeclaredConstructor().newInstance();
            ResultSetMetaData meta = rs.getMetaData();

            for (int i = 1; i <= meta.getColumnCount(); i++) {
                String col = meta.getColumnLabel(i); // usa alias si existen
                Object val = rs.getObject(i);

                Field field = DTOClass.getDeclaredField(col);
                if (!field.canAccess(instance)) {
                    field.setAccessible(true);
                }
                
                // Convertir tipos si es necesario
                if (val != null) {
                    Class<?> fieldType = field.getType();
                    
                    // Convertir String a LocalDate
                    if (fieldType.equals(LocalDate.class) && val instanceof String) {
                        val = LocalDate.parse((String) val);
                    }
                    // Convertir String a LocalTime
                    else if (fieldType.equals(LocalTime.class) && val instanceof String) {
                        val = LocalTime.parse((String) val);
                    }
                    // Convertir String a LocalDateTime
                    else if (fieldType.equals(LocalDateTime.class) && val instanceof String) {
                        val = LocalDateTime.parse((String) val, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    }
                }
                
                field.set(instance, val);
            }
            return instance;
        } catch (SQLException | NoSuchMethodException | InvocationTargetException | InstantiationException
                | IllegalAccessException | NoSuchFieldException e) {
            throw new AppException(null, e, getClass(), "mapResultSetToEntity");
        }
    }
}
