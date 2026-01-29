# ✈️ Airport Management System - Sistema de Gestión Aeroportuaria

Sistema completo de gestión aeroportuaria con **check-in automático mediante tarjetas NFC/RFID**. Permite administrar vuelos, pasajeros, aviones, reservas y realizar check-in automático usando Arduino con lector RFID-RC522.

## 🎯 Características Principales

### Módulos de Administración
- ✅ **Gestión de Pasajeros**: CRUD completo con soporte de tarjetas RFID
- ✅ **Gestión de Vuelos**: Registro y administración de vuelos
- ✅ **Gestión de Aviones**: Control de flota aérea
- ✅ **Gestión de Países**: Origen y destino de vuelos
- ✅ **Gestión de Reservas**: Asignación de asientos y control de check-in
- ✅ **Gestión de Tarjetas NFC**: Escribir, actualizar y eliminar datos en tarjetas

### Funcionalidades RFID/NFC
- 📖 **Leer UID** de tarjetas NFC
- ✍️ **Escribir datos** de pasajero en tarjeta (ID, Nombre, Cédula)
- 🔄 **Actualizar datos** existentes en tarjeta
- 🗑️ **Borrar/Limpiar** tarjeta completamente
- ✈️ **Check-in automático** escaneando tarjeta NFC

## 🏗️ Arquitectura del Sistema

```
┌─────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                    │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌─────────┐ │
│  │ Pasajero │  │  Vuelos  │  │ Reservas │  │ NFC Mgr │ │
│  └──────────┘  └──────────┘  └──────────┘  └─────────┘ │
└─────────────────────────────────────────────────────────┘
                          ↕
┌─────────────────────────────────────────────────────────┐
│                   BUSINESS LOGIC LAYER                   │
│  ┌──────────────┐  ┌─────────────┐  ┌────────────────┐ │
│  │ PasajeroSvc  │  │  VueloBL    │  │ CheckInService │ │
│  └──────────────┘  └─────────────┘  └────────────────┘ │
└─────────────────────────────────────────────────────────┘
                          ↕
┌─────────────────────────────────────────────────────────┐
│                    DATA ACCESS LAYER                     │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌─────────┐ │
│  │ SQLite   │  │   DAOs   │  │   DTOs   │  │ RFID IO │ │
│  └──────────┘  └──────────┘  └──────────┘  └─────────┘ │
└─────────────────────────────────────────────────────────┘
                          ↕
┌─────────────────────────────────────────────────────────┐
│                    HARDWARE LAYER                        │
│  ┌──────────────────────────────────────────────────┐   │
│  │  Arduino + RFID-RC522 + LCD 16x2 + LEDs         │   │
│  └──────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────┘
```

## 📊 Esquema de Base de Datos

```sql
Pasajero (IdPasajero, IdSexo, Nombre, Apellido, Cedula, UidRfid, Email, FechaNacimiento)
Vuelo (IdVuelo, IdPaisOrigen, IdPaisDestino, IdAvion, FechaVuelo, HoraVuelo)
Reserva (IdReserva, IdVuelo, IdPasajero, EstadoCheckin, Asiento)
Avion (IdAvion, Serie, CantidadAsientos)
PaisOrigen (IdPaisOrigen, Nombre)
PaisDestino (IdPaisDestino, Nombre)
Sexo (IdSexo, Nombre, Descripcion)
CheckInLog (IdCheckInLog, IdRFIDTag, IdPasajero, IdVuelo, ResultadoCode, Mensaje)
```

## 🔧 Requisitos Previos

### Software
- **Java JDK 11+** - [Descargar](https://www.oracle.com/java/technologies/downloads/)
- **Visual Studio Code** con extensión Java
- **Arduino IDE 1.8+** - [Descargar](https://www.arduino.cc/en/software)
- **SQLite** (incluido en el proyecto)

### Hardware
- **Arduino Uno/Mega** (1 unidad)
- **Módulo RFID-RC522** (1 unidad)
- **LCD 16x2 con adaptador I2C** (dirección 0x27)
- **LED Verde** (1 unidad) + Resistencia 220Ω
- **LED Rojo** (1 unidad) + Resistencia 220Ω
- **Tarjetas NFC MIFARE Classic 1K** (varias)
- **Cables Dupont** y **Breadboard**
- **Cable USB** para Arduino

### Librerías Arduino
- **MFRC522** (para RFID-RC522)
- **LiquidCrystal_I2C** (para LCD)
- **Wire** (incluida en Arduino IDE)
- **SPI** (incluida en Arduino IDE)

## 📥 Instalación

### Paso 1: Clonar/Descargar el Proyecto

```bash
# Opción 1: Clonar repositorio
git clone https://github.com/tu-usuario/AiportProyect-GUI.git
cd AiportProyect-GUI

# Opción 2: Descargar ZIP y extraer
```

### Paso 2: Configurar Java

1. Abrir el proyecto en **Visual Studio Code**
2. Verificar que las librerías estén en la carpeta `lib/`:
   - `sqlite-jdbc-3.40.0.0.jar`
   - `jSerialComm-2.3.0.jar`

3. Si faltan, descargar:
   - jSerialComm: https://github.com/Fazecast/jSerialComm/releases
   - SQLite JDBC: https://github.com/xerial/sqlite-jdbc/releases

### Paso 3: Inicializar Base de Datos

```bash
# Navegar a la carpeta de scripts
cd Storage/Script

# Ejecutar el script SQL (usando SQLite)
sqlite3 ../Database/AirPort.sqlite < DDL_DML.sql

# Volver a la raíz del proyecto
cd ../..
```

**Nota**: El script crea todas las tablas e inserta datos de prueba.

### Paso 4: Configurar Puerto COM

Editar el archivo `src/app.properties`:

```properties
# RFID Arduino
rfid.port=COM3          # Cambiar según tu puerto (COM3, COM4, etc.)
rfid.baudrate=9600
rfid.timeout=5000
```

**Para encontrar tu puerto COM**:
- Windows: Administrador de Dispositivos → Puertos (COM y LPT)
- Linux: `ls /dev/ttyUSB*` o `ls /dev/ttyACM*`
- Mac: `ls /dev/tty.*`

## 🔌 Configuración de Hardware Arduino

### Diagrama de Conexiones

```
Arduino Uno          RFID-RC522
-----------          ----------
Pin 10 (SS)    →     SDA
Pin 13 (SCK)   →     SCK
Pin 11 (MOSI)  →     MOSI
Pin 12 (MISO)  →     MISO
Pin 9          →     RST
3.3V           →     3.3V
GND            →     GND

Arduino Uno          LCD 16x2 I2C
-----------          -------------
A4 (SDA)       →     SDA
A5 (SCL)       →     SCL
5V             →     VCC
GND            →     GND

Arduino Uno          LEDs
-----------          ----
Pin 7          →     LED Verde (Ánodo) → Resistencia 220Ω → GND
Pin 6          →     LED Rojo (Ánodo) → Resistencia 220Ω → GND
```

### Instalación de Librerías Arduino

1. Abrir **Arduino IDE**
2. Ir a **Sketch → Include Library → Manage Libraries**
3. Buscar e instalar:
   - **MFRC522** by GithubCommunity
   - **LiquidCrystal I2C** by Frank de Brabander

### Cargar Firmware en Arduino

1. Abrir archivo: `myverpot-main/arduino_nfc_manager/arduino_nfc_manager.ino`
2. Seleccionar placa: **Tools → Board → Arduino Uno**
3. Seleccionar puerto: **Tools → Port → COM3** (tu puerto)
4. Hacer clic en **Upload** (→)
5. Esperar mensaje: "Done uploading"

### Verificar Funcionamiento

1. Abrir **Serial Monitor** (Ctrl+Shift+M)
2. Configurar: **9600 baud**
3. Deberías ver: `READY`
4. El LCD debe mostrar: "Airport NFC" / "System Ready"

## ▶️ Ejecución del Sistema

### Compilar el Proyecto

```bash
# Desde la raíz del proyecto
javac -d bin -cp "lib/*" src/**/*.java
```

### Ejecutar la Aplicación

```bash
# Windows
java -cp "bin;lib/*" App

# Linux/Mac
java -cp "bin:lib/*" App
```

### Inicio de Sesión

**Modo Cliente** (sin contraseña):
- Clic en "Cliente"
- Acceso a: Check-in RFID

**Modo Administrador**:
- Clic en "Administrador"
- Usuario: `admin`
- Contraseña: `1234`
- Acceso a: Todos los módulos CRUD + Gestión NFC

## 📖 Guía de Uso

### 1. Registrar Pasajero con Tarjeta NFC

#### Opción A: Registro Manual
1. Login como **Admin**
2. Ir a **"Pasajeros Registrados"**
3. Clic en **"Nuevo"**
4. Llenar datos:
   - Nombre: Juan
   - Apellido: Pérez
   - Cédula: 1234567890
   - **UidRfid**: (leer de la tarjeta primero)
   - Email: juan@example.com
   - Fecha Nacimiento: 1990-01-01
   - Sexo: Masculino
5. Clic en **"Guardar"**

#### Opción B: Obtener UID de Tarjeta
1. Ir a **"💳 Gestión Tarjetas NFC"**
2. Seleccionar puerto COM y **"Conectar Arduino"**
3. Clic en **"Leer UID de Tarjeta"**
4. Acercar tarjeta al lector
5. Copiar el UID mostrado (ej: `A1B2C3D4`)
6. Usar este UID al registrar el pasajero

### 2. Escribir Datos en Tarjeta NFC

1. Ir a **"💳 Gestión Tarjetas NFC"**
2. Conectar Arduino
3. Seleccionar pasajero de la lista
4. Clic en **"✍ Escribir en Tarjeta"**
5. Acercar tarjeta NFC al lector
6. Esperar confirmación: "Datos escritos exitosamente!"

**Datos grabados en la tarjeta**:
- Bloque 4: ID del Pasajero
- Bloque 5: Nombre completo
- Bloque 6: Número de cédula

### 3. Crear Vuelo

1. Login como **Admin**
2. Ir a **"Mostrar Vuelos"**
3. Clic en **"Nuevo"**
4. Seleccionar:
   - País Origen: USA
   - País Destino: Mexico
   - Avión: A320
5. Clic en **"Guardar"**

### 4. Crear Reserva

1. Ir a **"📋 Reservas"**
2. Clic en **"Nuevo"**
3. Seleccionar:
   - Vuelo: [1] Vuelo
   - Pasajero: [1] Juan Pérez
   - Asiento: 12A
   - Estado Check-in: F - Pendiente
4. Clic en **"Guardar"**

### 5. Realizar Check-in con RFID

1. Cambiar a **Modo Cliente**
2. Ir a **"✈️ Check-in RFID"**
3. Seleccionar puerto COM
4. Clic en **"🔌 Conectar Arduino"**
5. Clic en **"📖 Escanear Tarjeta"**
6. **Acercar tarjeta NFC del pasajero**
7. El sistema automáticamente:
   - Lee el UID de la tarjeta
   - Busca al pasajero en la base de datos
   - Verifica que tenga reserva activa
   - Actualiza el estado de check-in a "Realizado"
   - Muestra información del vuelo y asiento

**Pantalla LCD mostrará**:
```
Check-in OK!
Asiento: 12A
```

### 6. Actualizar Datos en Tarjeta

Si cambias información del pasajero:
1. Ir a **"💳 Gestión Tarjetas NFC"**
2. Seleccionar pasajero actualizado
3. Clic en **"🔄 Actualizar Tarjeta"**
4. Acercar la misma tarjeta
5. Los datos se sobrescriben

### 7. Borrar Tarjeta (Reasignar)

Para usar la tarjeta con otro pasajero:
1. Ir a **"💳 Gestión Tarjetas NFC"**
2. Clic en **"🗑 Borrar Tarjeta"**
3. Acercar tarjeta a borrar
4. Confirmar acción
5. La tarjeta queda limpia para reutilizar

## 🔍 Protocolo de Comunicación Serial

### Comandos desde Java → Arduino

| Comando | Formato | Descripción |
|---------|---------|-------------|
| READ | `READ\n` | Lee UID de tarjeta NFC |
| WRITE | `WRITE:IdPasajero\|Nombre\|Cedula\n` | Escribe datos en tarjeta |
| UPDATE | `UPDATE:IdPasajero\|Nombre\|Cedula\n` | Actualiza datos en tarjeta |
| DELETE | `DELETE\n` | Borra todos los datos |
| LCD | `LCD:Linea1\|Linea2\n` | Muestra texto en LCD |

### Respuestas Arduino → Java

| Respuesta | Significado |
|-----------|-------------|
| `READY` | Arduino inicializado |
| `UID:A1B2C3D4` | UID de tarjeta leído |
| `OK` | Operación exitosa |
| `ERROR:mensaje` | Error con descripción |

## 🐛 Solución de Problemas

### Arduino no se conecta

**Problema**: "Puerto COM3 no encontrado"

**Solución**:
1. Verificar que Arduino esté conectado por USB
2. Abrir Administrador de Dispositivos (Windows)
3. Buscar en "Puertos (COM y LPT)" el puerto correcto
4. Actualizar `app.properties` con el puerto correcto
5. Reiniciar la aplicación

### RFID no lee tarjetas

**Problema**: "No se detectó tarjeta" o timeout

**Solución**:
1. Verificar conexiones del RFID-RC522
2. Asegurar que la tarjeta es **MIFARE Classic 1K**
3. Acercar la tarjeta a menos de 3cm del lector
4. Verificar que el LED del módulo RFID esté encendido
5. Probar con otra tarjeta

### LCD no muestra nada

**Problema**: LCD en blanco o caracteres extraños

**Solución**:
1. Ajustar el potenciómetro del LCD (contraste)
2. Verificar dirección I2C (usar `I2C Scanner` sketch)
3. Si la dirección no es 0x27, cambiar en el código Arduino:
   ```cpp
   LiquidCrystal_I2C lcd(0x3F, 16, 2);  // Probar 0x3F
   ```
4. Verificar conexiones SDA/SCL

### Error de base de datos bloqueada

**Problema**: "Database is locked"

**Solución**:
1. Cerrar todas las instancias de la aplicación
2. Eliminar archivo `AirPort.sqlite-journal` si existe
3. Reiniciar la aplicación

### Check-in falla con "Tarjeta no registrada"

**Problema**: Tarjeta válida pero no reconocida

**Solución**:
1. Verificar que el UID en la base de datos coincida exactamente
2. El UID es case-insensitive pero sin espacios
3. Re-leer el UID con "Leer UID de Tarjeta"
4. Actualizar el pasajero con el UID correcto

## 📁 Estructura del Proyecto

```
AiportProyect-GUI/
├── src/
│   ├── App/
│   │   └── DesktopApp/
│   │       ├── Forms/
│   │       │   ├── PPasajero.java      # CRUD Pasajeros
│   │       │   ├── PVuelos.java        # CRUD Vuelos
│   │       │   ├── PReserva.java       # CRUD Reservas
│   │       │   ├── PNFCManager.java    # Gestión Tarjetas NFC
│   │       │   └── PCheckIn.java       # Check-in RFID
│   │       └── CustomControl/          # Componentes UI personalizados
│   ├── BusinessLogic/
│   │   ├── Entities/
│   │   │   ├── CheckInServiceimp.java
│   │   │   └── Pasajeroimp.java
│   │   └── Interfaces/
│   ├── DataAccess/
│   │   ├── DAOs/                       # Data Access Objects
│   │   ├── DTOs/                       # Data Transfer Objects
│   │   └── Helpers/
│   └── Infrastructure/
│       ├── Ports/
│       │   ├── RFIDPorts.java          # Interface RFID
│       │   └── RFIDArduinoAdapter.java # Implementación Serial
│       └── resources/
├── myverpot-main/
│   └── arduino_nfc_manager/
│       └── arduino_nfc_manager.ino     # Firmware Arduino
├── Storage/
│   ├── Database/
│   │   └── AirPort.sqlite              # Base de datos
│   └── Script/
│       └── DDL_DML.sql                 # Script de inicialización
├── lib/
│   ├── sqlite-jdbc-3.40.0.0.jar
│   └── jSerialComm-2.3.0.jar
└── README.md
```

## 🎓 Créditos

**Desarrollado por**: Grupo 4  
**Proyecto**: Sistema de Gestión Aeroportuaria con RFID  
**Tecnologías**: Java Swing, SQLite, Arduino, RFID-RC522  

## 📄 Licencia

Este proyecto es de código abierto para fines educativos.

---

## 🚀 Próximas Mejoras

- [ ] Reportes en PDF de vuelos y pasajeros
- [ ] Gráficos de estadísticas de check-in
- [ ] Notificaciones por email al realizar check-in
- [ ] Soporte para múltiples lectores RFID
- [ ] Aplicación móvil para pasajeros
- [ ] Integración con APIs de aerolíneas

---

**¿Necesitas ayuda?** Revisa la sección de [Solución de Problemas](#-solución-de-problemas) o consulta la documentación técnica en la carpeta `Doc/`.

✈️ **¡Buen vuelo con tu sistema de gestión aeroportuaria!** ✈️
