-- ============================================================================
-- Script para eliminar PERMANENTEMENTE registros marcados como eliminados
-- ============================================================================
-- Este script elimina físicamente (DELETE) todos los registros con Estado = 'X'
-- de todas las tablas del sistema
-- 
-- ADVERTENCIA: Esta acción es IRREVERSIBLE
-- Se recomienda hacer un backup de la base de datos antes de ejecutar
-- ============================================================================

-- database: ..\DataBase\AirPort.Sqlite

-- Mostrar conteo de registros a eliminar ANTES de la eliminación
SELECT 'REGISTROS A ELIMINAR:' AS Info;

SELECT 'CheckInLog' AS Tabla, COUNT(*) AS Cantidad 
FROM CheckInLog WHERE Estado = 'X'
UNION ALL
SELECT 'RFIDTag' AS Tabla, COUNT(*) AS Cantidad 
FROM RFIDTag WHERE Estado = 'X'
UNION ALL
SELECT 'Reserva' AS Tabla, COUNT(*) AS Cantidad 
FROM Reserva WHERE Estado = 'X'
UNION ALL
SELECT 'Vuelo' AS Tabla, COUNT(*) AS Cantidad 
FROM Vuelo WHERE Estado = 'X'
UNION ALL
SELECT 'Pasajero' AS Tabla, COUNT(*) AS Cantidad 
FROM Pasajero WHERE Estado = 'X'
UNION ALL
SELECT 'Avion' AS Tabla, COUNT(*) AS Cantidad 
FROM Avion WHERE Estado = 'X'
UNION ALL
SELECT 'PaisDestino' AS Tabla, COUNT(*) AS Cantidad 
FROM PaisDestino WHERE Estado = 'X'
UNION ALL
SELECT 'PaisOrigen' AS Tabla, COUNT(*) AS Cantidad 
FROM PaisOrigen WHERE Estado = 'X'
UNION ALL
SELECT 'Sexo' AS Tabla, COUNT(*) AS Cantidad 
FROM Sexo WHERE Estado = 'X';

-- ============================================================================
-- ELIMINACIÓN FÍSICA DE REGISTROS
-- ============================================================================
-- IMPORTANTE: El orden de eliminación respeta las dependencias de foreign keys
-- Se eliminan primero las tablas hijas y luego las tablas padre
-- ============================================================================

-- 1. Eliminar CheckInLog (tabla sin dependientes)
DELETE FROM CheckInLog WHERE Estado = 'X';
SELECT 'CheckInLog: ' || changes() || ' registros eliminados' AS Resultado;

-- 2. Eliminar RFIDTag (depende de Pasajero)
DELETE FROM RFIDTag WHERE Estado = 'X';
SELECT 'RFIDTag: ' || changes() || ' registros eliminados' AS Resultado;

-- 3. Eliminar Reserva (depende de Vuelo y Pasajero)
DELETE FROM Reserva WHERE Estado = 'X';
SELECT 'Reserva: ' || changes() || ' registros eliminados' AS Resultado;

-- 4. Eliminar Vuelo (depende de Avion, PaisOrigen, PaisDestino)
DELETE FROM Vuelo WHERE Estado = 'X';
SELECT 'Vuelo: ' || changes() || ' registros eliminados' AS Resultado;

-- 5. Eliminar Pasajero (depende de Sexo)
DELETE FROM Pasajero WHERE Estado = 'X';
SELECT 'Pasajero: ' || changes() || ' registros eliminados' AS Resultado;

-- 6. Eliminar Avion (tabla sin dependientes)
DELETE FROM Avion WHERE Estado = 'X';
SELECT 'Avion: ' || changes() || ' registros eliminados' AS Resultado;

-- 7. Eliminar PaisDestino (tabla sin dependientes)
DELETE FROM PaisDestino WHERE Estado = 'X';
SELECT 'PaisDestino: ' || changes() || ' registros eliminados' AS Resultado;

-- 8. Eliminar PaisOrigen (tabla sin dependientes)
DELETE FROM PaisOrigen WHERE Estado = 'X';
SELECT 'PaisOrigen: ' || changes() || ' registros eliminados' AS Resultado;

-- 9. Eliminar Sexo (tabla sin dependientes)
DELETE FROM Sexo WHERE Estado = 'X';
SELECT 'Sexo: ' || changes() || ' registros eliminados' AS Resultado;

-- ============================================================================
-- VERIFICACIÓN FINAL
-- ============================================================================
-- Mostrar conteo de registros eliminados que aún quedan (debería ser 0)
-- ============================================================================

SELECT '===================' AS '';
SELECT 'VERIFICACIÓN FINAL:' AS Info;
SELECT '===================' AS '';

SELECT 'CheckInLog' AS Tabla, COUNT(*) AS RestantesConX 
FROM CheckInLog WHERE Estado = 'X'
UNION ALL
SELECT 'RFIDTag' AS Tabla, COUNT(*) AS RestantesConX 
FROM RFIDTag WHERE Estado = 'X'
UNION ALL
SELECT 'Reserva' AS Tabla, COUNT(*) AS RestantesConX 
FROM Reserva WHERE Estado = 'X'
UNION ALL
SELECT 'Vuelo' AS Tabla, COUNT(*) AS RestantesConX 
FROM Vuelo WHERE Estado = 'X'
UNION ALL
SELECT 'Pasajero' AS Tabla, COUNT(*) AS RestantesConX 
FROM Pasajero WHERE Estado = 'X'
UNION ALL
SELECT 'Avion' AS Tabla, COUNT(*) AS RestantesConX 
FROM Avion WHERE Estado = 'X'
UNION ALL
SELECT 'PaisDestino' AS Tabla, COUNT(*) AS RestantesConX 
FROM PaisDestino WHERE Estado = 'X'
UNION ALL
SELECT 'PaisOrigen' AS Tabla, COUNT(*) AS RestantesConX 
FROM PaisOrigen WHERE Estado = 'X'
UNION ALL
SELECT 'Sexo' AS Tabla, COUNT(*) AS RestantesConX 
FROM Sexo WHERE Estado = 'X';

SELECT '===================' AS '';
SELECT '✓ LIMPIEZA COMPLETADA' AS Info;
SELECT '===================' AS '';

-- ============================================================================
-- OPTIMIZACIÓN DE LA BASE DE DATOS
-- ============================================================================
-- Ejecutar VACUUM para recuperar espacio y optimizar la base de datos
-- ============================================================================

VACUUM;
SELECT '✓ Base de datos optimizada (VACUUM ejecutado)' AS Info;
