-- Script opcional para agregar eliminación en cascada a nivel de base de datos
-- NOTA: SQLite no soporta ALTER TABLE para modificar constraints existentes
-- Este script es solo para referencia o para recrear las tablas con CASCADE

-- Para implementar CASCADE en SQLite, se necesita recrear las tablas
-- Este es un script de referencia de cómo deberían estar las tablas

/*
-- Ejemplo de cómo debería estar la tabla Reserva con ON DELETE CASCADE:
CREATE TABLE Reserva (
    IdReserva INTEGER PRIMARY KEY AUTOINCREMENT,
    IdVuelo INTEGER NOT NULL REFERENCES Vuelo (IdVuelo) ON DELETE CASCADE,
    IdPasajero INTEGER NOT NULL REFERENCES Pasajero (IdPasajero) ON DELETE CASCADE,
    EstadoCheckin VARCHAR(1) NOT NULL DEFAULT 'F',
    Asiento VARCHAR(2) NOT NULL,
    Estado VARCHAR(1) NOT NULL DEFAULT 'A',
    FechaCreacion DATETIME NOT NULL DEFAULT (datetime('now', 'localtime')),
    FechaModifica DATETIME NOT NULL DEFAULT (datetime('now', 'localtime'))
);

-- Ejemplo de cómo debería estar la tabla RFIDTag con ON DELETE CASCADE:
CREATE TABLE RFIDTag (
    IdRFIDTag INTEGER PRIMARY KEY AUTOINCREMENT,
    IdPasajero INTEGER NOT NULL REFERENCES Pasajero (IdPasajero) ON DELETE CASCADE,
    Estado VARCHAR(1) NOT NULL DEFAULT 'A',
    FechaCreacion DATETIME NOT NULL DEFAULT (datetime('now', 'localtime')),
    FechaModifica DATETIME NOT NULL DEFAULT (datetime('now', 'localtime'))
);

-- Ejemplo de cómo debería estar la tabla CheckInLog con ON DELETE SET NULL:
CREATE TABLE CheckInLog (
    IdCheckInLog INTEGER PRIMARY KEY AUTOINCREMENT,
    IdRFIDTag TEXT NOT NULL,
    IdPasajero INTEGER REFERENCES Pasajero (IdPasajero) ON DELETE SET NULL,
    IdVuelo INTEGER,
    ResultadoCode TEXT NOT NULL,
    Mensaje TEXT,
    Estado VARCHAR(1) NOT NULL DEFAULT 'A',
    FechaCreacion DATETIME NOT NULL DEFAULT (datetime('now', 'localtime')),
    FechaModifica DATETIME NOT NULL DEFAULT (datetime('now', 'localtime'))
);
*/

-- IMPORTANTE:
-- La eliminación en cascada ya está implementada en el código Java
-- mediante el método deleteCascade() en PasajeroDAO.java
-- Este script es solo para referencia si se desea implementar a nivel de BD
