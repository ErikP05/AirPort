-- database: ..\DataBase\AirPort.Sqlite
DROP TABLE IF EXISTS Sexo;

DROP TABLE IF EXISTS Avion;

DROP TABLE IF EXISTS Pais;

DROP TABLE IF EXISTS PaisOrigen;

DROP TABLE IF EXISTS PaisDestino;

DROP TABLE IF EXISTS Pasajero;

DROP TABLE IF EXISTS Vuelo;

DROP TABLE IF EXISTS Reserva;

CREATE TABLE
    Sexo (
        IdSexo INTEGER PRIMARY KEY AUTOINCREMENT,
        Nombre VARCHAR(15) NOT NULL UNIQUE,
        Descripcion VARCHAR(100) NULL,
        Estado VARCHAR(1) NOT NULL DEFAULT 'A',
        FechaCreacion DATETIME NOT NULL DEFAULT (datetime('now', 'localtime')),
        FechaModifica DATETIME NOT NULL DEFAULT (datetime('now', 'localtime'))
    );

CREATE TABLE
    Avion (
        IdAvion INTEGER PRIMARY KEY AUTOINCREMENT,
        Serie VARCHAR(15) NOT NULL UNIQUE,
        CantidadAsientos INTEGER NOT NULL,
        Estado VARCHAR(1) NOT NULL DEFAULT 'A',
        FechaCreacion DATETIME NOT NULL DEFAULT (datetime('now', 'localtime')),
        FechaModifica DATETIME NOT NULL DEFAULT (datetime('now', 'localtime'))
    );

CREATE TABLE
    PaisOrigen (
        IdPaisOrigen INTEGER PRIMARY KEY AUTOINCREMENT,
        Nombre VARCHAR(15) NOT NULL UNIQUE,
        Estado VARCHAR(1) NOT NULL DEFAULT 'A',
        FechaCreacion DATETIME NOT NULL DEFAULT (datetime('now', 'localtime')),
        FechaModifica DATETIME NOT NULL DEFAULT (datetime('now', 'localtime'))
    );

CREATE TABLE
    PaisDestino (
        IdPaisDestino INTEGER PRIMARY KEY AUTOINCREMENT,
        Nombre VARCHAR(15) NOT NULL UNIQUE,
        Estado VARCHAR(1) NOT NULL DEFAULT 'A',
        FechaCreacion DATETIME NOT NULL DEFAULT (datetime('now', 'localtime')),
        FechaModifica DATETIME NOT NULL DEFAULT (datetime('now', 'localtime'))
    );

CREATE TABLE
    Pasajero (
        IdPasajero INTEGER PRIMARY KEY AUTOINCREMENT,
        IdSexo INTEGER NOT NULL REFERENCES Sexo (IdSexo),
        Nombre VARCHAR(20) NOT NULL,
        Apellido VARCHAR(20) NOT NULL,
        Cedula VARCHAR(20) NOT NULL UNIQUE,
        UidRfid VARCHAR(30) NOT NULL UNIQUE,
        Email VARCHAR(30) NOT NULL,
        FechaNacimiento DATETIME NOT NULL,
        Estado VARCHAR(1) NOT NULL DEFAULT 'A',
        FechaCreacion DATETIME NOT NULL DEFAULT (datetime('now', 'localtime')),
        FechaModifica DATETIME NOT NULL DEFAULT (datetime('now', 'localtime'))
    );

CREATE TABLE
    Vuelo (
        IdVuelo INTEGER PRIMARY KEY AUTOINCREMENT,
        IdPaisOrigen INTEGER NOT NULL REFERENCES PaisOrigen (IdPaisOrigen),
        IdPaisDestino INTEGER NOT NULL REFERENCES PaisDestino (IdPaisDestino),
        IdAvion INTEGER NOT NULL REFERENCES Avion (IdAvion),
        FechaVuelo DATETIME NOT NULL,
        HoraVuelo TIME NOT NULL,
        Estado VARCHAR(1) NOT NULL DEFAULT 'A',
        FechaCreacion DATETIME NOT NULL DEFAULT (datetime('now', 'localtime')),
        FechaModifica DATETIME NOT NULL DEFAULT (datetime('now', 'localtime'))
    );

CREATE TABLE
    Reserva (
        IdReserva INTEGER PRIMARY KEY AUTOINCREMENT,
        IdVuelo INTEGER NOT NULL REFERENCES Vuelo (IdVuelo),
        IdPasajero INTEGER NOT NULL REFERENCES Pasajero (IdPasajero),
        EstadoCheckin VARCHAR(1) NOT NULL DEFAULT 'F',
        Asiento VARCHAR(2) NOT NULL,
        Estado VARCHAR(1) NOT NULL DEFAULT 'A',
        FechaCreacion DATETIME NOT NULL DEFAULT (datetime('now', 'localtime')),
        FechaModifica DATETIME NOT NULL DEFAULT (datetime('now', 'localtime'))
    );

-- Relación del tag con el pasajero
CREATE TABLE IF NOT EXISTS
    RFIDTag (
        IdRFIDTag INTEGER PRIMARY KEY AUTOINCREMENT,
        IdPasajero INTEGER NOT NULL REFERENCES Pasajero (IdPasajero),
        Estado VARCHAR(1) NOT NULL DEFAULT 'A',
        FechaCreacion DATETIME NOT NULL DEFAULT (datetime('now', 'localtime')),
        FechaModifica DATETIME NOT NULL DEFAULT (datetime('now', 'localtime'))
    );

-- Auditoría del proceso (opcional pero recomendable)
CREATE TABLE IF NOT EXISTS
    CheckInLog (
        IdCheckInLog INTEGER PRIMARY KEY AUTOINCREMENT,
        IdRFIDTag TEXT NOT NULL,
        IdPasajero INTEGER,
        IdVuelo INTEGER,
        ResultadoCode TEXT NOT NULL, -- OK, TAG_UNASSIGNED, INVALID_FLIGHT, etc.
        Mensaje TEXT,
        Estado VARCHAR(1) NOT NULL DEFAULT 'A',
        FechaCreacion DATETIME NOT NULL DEFAULT (datetime('now', 'localtime')),
        FechaModifica DATETIME NOT NULL DEFAULT (datetime('now', 'localtime'))
    );

INSERT INTO
    Sexo (Nombre, Descripcion)
VALUES
    ('Masculino', 'Sexo Masculino'),
    ('Femenino', 'Sexo Femenino'),
    ('Asexual', 'Sexo Asexual');

INSERT INTO
    PaisOrigen (Nombre)
VALUES
    ('USA'),
    ('Canada'),
    ('Mexico'),
    ('UK'),
    ('France');

INSERT INTO
    PaisDestino (Nombre)
VALUES
    ('USA'),
    ('Canada'),
    ('Mexico'),
    ('UK'),
    ('France');

INSERT INTO
    Avion (Serie, CantidadAsientos)
VALUES
    ('A320', 180),
    ('B737', 160),
    ('E190', 100);

INSERT INTO
    Pasajero (
        IdSexo,
        Nombre,
        Apellido,
        Cedula,
        UidRfid,
        Email,
        FechaNacimiento
    )
VALUES
    (
        1,
        'John',
        'Doe',
        '123456789',
        'RFID123456',
        '',
        '1990-01-01'
    ),
    (
        2,
        'Jane',
        'Smith',
        '987654321',
        'RFID654321',
        '',
        '1992-02-02'
    );
