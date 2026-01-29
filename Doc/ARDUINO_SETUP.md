# 🔌 Guía de Configuración Arduino - Sistema RFID NFC

## 📋 Tabla de Contenidos
1. [Materiales Necesarios](#materiales-necesarios)
2. [Instalación de Librerías](#instalación-de-librerías)
3. [Diagrama de Conexiones](#diagrama-de-conexiones)
4. [Carga del Firmware](#carga-del-firmware)
5. [Pruebas y Verificación](#pruebas-y-verificación)
6. [Solución de Problemas](#solución-de-problemas)

---

## 📦 Materiales Necesarios

### Hardware
| Componente | Cantidad | Especificaciones |
|------------|----------|------------------|
| Arduino Uno/Mega | 1 | 5V, 16MHz |
| Módulo RFID-RC522 | 1 | 13.56MHz, SPI |
| LCD 16x2 | 1 | Con adaptador I2C (0x27) |
| LED Verde | 1 | 5mm, 20mA |
| LED Rojo | 1 | 5mm, 20mA |
| Resistencias 220Ω | 2 | 1/4W |
| Tarjetas NFC MIFARE Classic 1K | 5-10 | 13.56MHz |
| Breadboard | 1 | 830 puntos |
| Cables Dupont M-M | 15 | 20cm |
| Cable USB A-B | 1 | Para Arduino |

### Software
- Arduino IDE 1.8.19 o superior
- Librería MFRC522
- Librería LiquidCrystal_I2C

---

## 📚 Instalación de Librerías

### Método 1: Gestor de Librerías (Recomendado)

1. Abrir **Arduino IDE**
2. Ir a **Sketch → Include Library → Manage Libraries...**
3. En el buscador, escribir: **MFRC522**
4. Instalar: **MFRC522 by GithubCommunity** (versión 1.4.10 o superior)
5. En el buscador, escribir: **LiquidCrystal I2C**
6. Instalar: **LiquidCrystal I2C by Frank de Brabander** (versión 1.1.2 o superior)
7. Cerrar el Gestor de Librerías

### Método 2: Manual

1. Descargar librerías:
   - MFRC522: https://github.com/miguelbalboa/rfid/archive/master.zip
   - LiquidCrystal_I2C: https://github.com/johnrickman/LiquidCrystal_I2C/archive/master.zip

2. Extraer archivos ZIP

3. Copiar carpetas a:
   - Windows: `C:\Users\[Usuario]\Documents\Arduino\libraries\`
   - Linux: `~/Arduino/libraries/`
   - Mac: `~/Documents/Arduino/libraries/`

4. Reiniciar Arduino IDE

### Verificar Instalación

```cpp
// Sketch → Include Library → debería aparecer:
// - MFRC522
// - LiquidCrystal_I2C
```

---

## 🔌 Diagrama de Conexiones

### RFID-RC522 → Arduino Uno

```
RFID-RC522          Arduino Uno
──────────          ───────────
SDA        ────────→ Pin 10 (SS)
SCK        ────────→ Pin 13 (SCK)
MOSI       ────────→ Pin 11 (MOSI)
MISO       ────────→ Pin 12 (MISO)
IRQ        ────────→ (No conectar)
GND        ────────→ GND
RST        ────────→ Pin 9
3.3V       ────────→ 3.3V ⚠️ NO 5V
```

> ⚠️ **IMPORTANTE**: El módulo RFID-RC522 funciona a **3.3V**. Conectarlo a 5V puede dañarlo permanentemente.

### LCD 16x2 I2C → Arduino Uno

```
LCD I2C             Arduino Uno
───────             ───────────
GND        ────────→ GND
VCC        ────────→ 5V
SDA        ────────→ A4 (SDA)
SCL        ────────→ A5 (SCL)
```

### LEDs → Arduino Uno

```
LED Verde:
Arduino Pin 7 → Resistencia 220Ω → LED (Ánodo +) → LED (Cátodo -) → GND

LED Rojo:
Arduino Pin 6 → Resistencia 220Ω → LED (Ánodo +) → LED (Cátodo -) → GND
```

### Diagrama Completo (Vista Superior)

```
                    ┌─────────────────┐
                    │   Arduino Uno   │
                    │                 │
    ┌───────────────┤ 3.3V       GND  ├───────┬─────────────┐
    │               │                 │       │             │
    │   ┌───────────┤ Pin 9      5V   ├───┐   │             │
    │   │           │                 │   │   │             │
    │   │   ┌───────┤ Pin 10     A4   ├─┐ │   │             │
    │   │   │       │                 │ │ │   │             │
    │   │   │   ┌───┤ Pin 11     A5   ├┐│ │   │             │
    │   │   │   │   │                 │││ │   │             │
    │   │   │   │ ┌─┤ Pin 12     Pin 7├┼┼─┼───┼─────┐       │
    │   │   │   │ │ │                 │││ │   │     │       │
    │   │   │   │ │┌┤ Pin 13     Pin 6├┼┼─┼───┼───┐ │       │
    │   │   │   │ ││└─────────────────┘││ │   │   │ │       │
    │   │   │   │ ││                   ││ │   │   │ │       │
    │   │   │   │ ││  ┌────────────┐   ││ │   │   │ │       │
    │   │   │   │ ││  │ RFID-RC522 │   ││ │   │   │ │       │
    └───┼───┼───┼─┼┼──┤ 3.3V   SDA ├───┘│ │   │   │ │       │
        │   │   │ ││  │ RST    SCK ├────┘ │   │   │ │       │
        └───┼───┼─┼┼──┤ GND   MOSI ├──────┘   │   │ │       │
            │   └─┼┼──┤       MISO ├──────────┘   │ │       │
            │     │└──┤       IRQ  │              │ │       │
            │     │   └────────────┘              │ │       │
            │     │                               │ │       │
            │     │   ┌────────────┐              │ │       │
            │     │   │  LCD 16x2  │              │ │       │
            │     │   │    I2C     │              │ │       │
            │     └───┤ SDA    VCC ├──────────────┘ │       │
            └─────────┤ SCL    GND ├────────────────┘       │
                      └────────────┘                        │
                                                            │
                      ┌─────┐  ┌─────┐                      │
                      │ 220Ω│  │ 220Ω│                      │
                      └──┬──┘  └──┬──┘                      │
                         │        │                         │
                      ┌──▼──┐  ┌──▼──┐                      │
                      │ LED │  │ LED │                      │
                      │Verde│  │Rojo │                      │
                      └──┬──┘  └──┬──┘                      │
                         └────────┴─────────────────────────┘
                                  GND
```

---

## 📤 Carga del Firmware

### Paso 1: Abrir el Sketch

1. Navegar a: `AiportProyect-GUI/myverpot-main/arduino_nfc_manager/`
2. Abrir archivo: `arduino_nfc_manager.ino`
3. Arduino IDE se abrirá automáticamente

### Paso 2: Configurar Arduino IDE

1. **Seleccionar Placa**:
   - Ir a **Tools → Board → Arduino AVR Boards → Arduino Uno**

2. **Seleccionar Puerto**:
   - Ir a **Tools → Port**
   - Seleccionar puerto donde está conectado Arduino
   - Windows: `COM3`, `COM4`, etc.
   - Linux: `/dev/ttyUSB0`, `/dev/ttyACM0`
   - Mac: `/dev/cu.usbmodem...`

3. **Verificar Configuración**:
   - Velocidad: 9600 baud (ya configurado en el código)

### Paso 3: Compilar y Cargar

1. Clic en **Verify** (✓) para compilar
2. Esperar mensaje: "Done compiling"
3. Clic en **Upload** (→) para cargar
4. Esperar mensaje: "Done uploading"

### Paso 4: Verificación Inicial

1. Abrir **Serial Monitor**: **Tools → Serial Monitor** (Ctrl+Shift+M)
2. Configurar velocidad: **9600 baud**
3. Deberías ver:
   ```
   READY
   ```
4. El LCD debe mostrar:
   ```
   Airport NFC
   System Ready
   ```

---

## 🧪 Pruebas y Verificación

### Prueba 1: Lectura de UID

1. En Serial Monitor, enviar: `READ`
2. Acercar tarjeta NFC al lector (< 3cm)
3. Deberías recibir:
   ```
   UID:A1B2C3D4
   ```
4. LCD mostrará:
   ```
   UID Leido:
   A1B2C3D4
   ```
5. LED Verde parpadeará 2 veces

### Prueba 2: Escritura de Datos

1. En Serial Monitor, enviar:
   ```
   WRITE:1|Juan Perez|1234567890
   ```
2. Acercar tarjeta NFC
3. Deberías recibir: `OK`
4. LCD mostrará:
   ```
   Datos escritos
   exitosamente!
   ```
5. LED Verde parpadeará 3 veces

### Prueba 3: Control de LCD

1. En Serial Monitor, enviar:
   ```
   LCD:Hola Mundo|Linea 2
   ```
2. LCD debe mostrar:
   ```
   Hola Mundo
   Linea 2
   ```

### Prueba 4: Borrado de Tarjeta

1. En Serial Monitor, enviar: `DELETE`
2. Acercar tarjeta NFC
3. Deberías recibir: `OK`
4. LCD mostrará:
   ```
   Tarjeta borrada
   exitosamente!
   ```

---

## 🔧 Solución de Problemas

### Problema 1: "MFRC522.h: No such file or directory"

**Causa**: Librería MFRC522 no instalada

**Solución**:
1. Ir a **Sketch → Include Library → Manage Libraries**
2. Buscar: **MFRC522**
3. Instalar la librería
4. Reiniciar Arduino IDE

### Problema 2: LCD en blanco

**Causa**: Contraste mal ajustado o dirección I2C incorrecta

**Solución**:
1. Ajustar potenciómetro en la parte trasera del LCD
2. Si no funciona, verificar dirección I2C:
   - Cargar sketch "I2C Scanner" (buscar en Google)
   - Anotar dirección encontrada (ej: 0x3F)
   - En el código Arduino, cambiar línea 42:
     ```cpp
     LiquidCrystal_I2C lcd(0x3F, 16, 2);  // Cambiar 0x27 por tu dirección
     ```

### Problema 3: "No se detectó tarjeta" (Timeout)

**Causas posibles**:
- Tarjeta no compatible (debe ser MIFARE Classic 1K)
- Conexiones flojas
- Tarjeta muy lejos del lector

**Solución**:
1. Verificar todas las conexiones del RFID-RC522
2. Acercar tarjeta a menos de 2cm
3. Probar con otra tarjeta NFC
4. Verificar que el LED del módulo RFID esté encendido

### Problema 4: LEDs no encienden

**Causa**: Polaridad invertida o resistencia faltante

**Solución**:
1. Verificar polaridad del LED:
   - Pata larga (+) → Resistencia → Pin Arduino
   - Pata corta (-) → GND
2. Verificar resistencia de 220Ω presente
3. Probar LED directamente con 3V para verificar que funciona

### Problema 5: Error al cargar firmware

**Causa**: Puerto COM incorrecto o Arduino no reconocido

**Solución**:
1. Desconectar y reconectar USB
2. Verificar drivers:
   - Windows: Instalar drivers CH340 si es clon
   - Linux: Agregar usuario a grupo `dialout`:
     ```bash
     sudo usermod -a -G dialout $USER
     ```
3. Probar con otro cable USB
4. Reiniciar Arduino IDE

### Problema 6: Caracteres extraños en Serial Monitor

**Causa**: Velocidad (baud rate) incorrecta

**Solución**:
1. Verificar que Serial Monitor esté en **9600 baud**
2. Verificar que el código tenga:
   ```cpp
   Serial.begin(9600);
   ```

---

## 📊 Especificaciones Técnicas

### RFID-RC522
- Frecuencia: 13.56 MHz
- Protocolo: ISO/IEC 14443 A/MIFARE
- Distancia de lectura: 0-60mm
- Voltaje: 3.3V DC
- Corriente: 13-26mA
- Interface: SPI

### LCD 16x2 I2C
- Caracteres: 16 columnas x 2 filas
- Voltaje: 5V DC
- Corriente: 120mA (con backlight)
- Interface: I2C (2 pines)
- Direcciones comunes: 0x27, 0x3F

### Tarjetas MIFARE Classic 1K
- Memoria: 1024 bytes
- Sectores: 16 (0-15)
- Bloques por sector: 4
- Bloques totales: 64
- Clave por defecto: FF FF FF FF FF FF

---

## 📝 Notas Adicionales

### Formato de Datos en Tarjeta

El sistema utiliza los bloques 4, 5 y 6 del Sector 1:

| Bloque | Contenido | Tamaño |
|--------|-----------|--------|
| 4 | ID Pasajero (entero) | 4 bytes |
| 5 | Nombre completo | 16 bytes |
| 6 | Número de cédula | 16 bytes |
| 7 | Trailer (no modificar) | 16 bytes |

### Comandos Disponibles

| Comando | Formato | Respuesta |
|---------|---------|-----------|
| READ | `READ\n` | `UID:XXXXXXXX` |
| WRITE | `WRITE:id\|nombre\|cedula\n` | `OK` o `ERROR:msg` |
| UPDATE | `UPDATE:id\|nombre\|cedula\n` | `OK` o `ERROR:msg` |
| DELETE | `DELETE\n` | `OK` o `ERROR:msg` |
| LCD | `LCD:linea1\|linea2\n` | (sin respuesta) |

---

**¿Necesitas más ayuda?** Consulta el README principal o revisa la documentación de las librerías:
- MFRC522: https://github.com/miguelbalboa/rfid
- LiquidCrystal_I2C: https://github.com/johnrickman/LiquidCrystal_I2C

✅ **¡Configuración completada!** Ahora puedes usar el sistema de gestión NFC.
