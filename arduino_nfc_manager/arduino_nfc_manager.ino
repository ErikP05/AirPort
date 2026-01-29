/*
 * ✈️ AIRPORT CHECK-IN - NFC CARD MANAGER
 * Sistema de gestión de tarjetas NFC para pasajeros
 * 
 * FUNCIONALIDADES:
 * - READ: Leer UID de tarjeta NFC
 * - WRITE: Escribir datos de pasajero en tarjeta
 * - UPDATE: Actualizar datos existentes
 * - DELETE: Borrar datos de la tarjeta
 * 
 * HARDWARE:
 * - Arduino Uno/Mega
 * - RFID-RC522 Module
 * - LCD 16x2 I2C (0x27)
 * - LED Verde (Pin 7) - Éxito
 * - LED Rojo (Pin 6) - Error
 * 
 * PROTOCOLO SERIAL (9600 baud):
 * Comandos desde Java:
 *   READ\n                           -> Lee UID y responde: UID:A1B2C3D4
 *   WRITE:IdPasajero|Nombre|Cedula\n -> Escribe datos y responde: OK o ERROR
 *   UPDATE:IdPasajero|Nombre|Cedula\n -> Actualiza datos y responde: OK o ERROR
 *   DELETE\n                         -> Borra tarjeta y responde: OK o ERROR
 *   LCD:Linea1|Linea2\n              -> Muestra texto en LCD
 * 
 * FORMATO DE DATOS EN TARJETA NFC (Bloques 4-6):
 * Bloque 4: IdPasajero (4 bytes)
 * Bloque 5: Nombre (16 bytes)
 * Bloque 6: Cedula (16 bytes)
 */

#include <SPI.h>
#include <MFRC522.h>
#include <Wire.h>
#include <LiquidCrystal_I2C.h>

// ===== CONFIGURACIÓN DE PINES =====
#define RST_PIN         9
#define SS_PIN          10
#define LED_GREEN       7
#define LED_RED         6

// ===== INICIALIZACIÓN DE COMPONENTES =====
MFRC522 mfrc522(SS_PIN, RST_PIN);
LiquidCrystal_I2C lcd(0x27, 16, 2);  // Dirección I2C 0x27, 16 columnas, 2 filas

// ===== VARIABLES GLOBALES =====
String comando = "";
bool cardPresent = false;

// ===== BLOQUES DE MEMORIA NFC =====
// Usamos los bloques 4, 5, 6 del Sector 1 (bloques 4-7)
// Bloque 7 es el trailer (no se puede escribir datos)
const byte BLOCK_ID = 4;      // IdPasajero
const byte BLOCK_NOMBRE = 5;  // Nombre
const byte BLOCK_CEDULA = 6;  // Cedula

// Clave por defecto de las tarjetas MIFARE
MFRC522::MIFARE_Key key;

void setup() {
  Serial.begin(9600);
  SPI.begin();
  mfrc522.PCD_Init();
  
  // Configurar LEDs
  pinMode(LED_GREEN, OUTPUT);
  pinMode(LED_RED, OUTPUT);
  digitalWrite(LED_GREEN, LOW);
  digitalWrite(LED_RED, LOW);
  
  // Inicializar LCD
  lcd.init();
  lcd.backlight();
  lcd.clear();
  lcd.setCursor(0, 0);
  lcd.print("Airport NFC");
  lcd.setCursor(0, 1);
  lcd.print("System Ready");
  
  // Configurar clave por defecto (FF FF FF FF FF FF)
  for (byte i = 0; i < 6; i++) {
    key.keyByte[i] = 0xFF;
  }
  
  Serial.println("READY");
  delay(2000);
  mostrarMensajeLCD("Esperando", "comando...");
}

void loop() {
  // Leer comandos desde Serial
  if (Serial.available() > 0) {
    comando = Serial.readStringUntil('\n');
    comando.trim();
    procesarComando(comando);
  }
  
  delay(100);
}

// ===== PROCESADOR DE COMANDOS =====
void procesarComando(String cmd) {
  if (cmd.startsWith("READ")) {
    comandoRead();
  } 
  else if (cmd.startsWith("WRITE:")) {
    String datos = cmd.substring(6);
    comandoWrite(datos);
  } 
  else if (cmd.startsWith("UPDATE:")) {
    String datos = cmd.substring(7);
    comandoUpdate(datos);
  } 
  else if (cmd.startsWith("DELETE")) {
    comandoDelete();
  } 
  else if (cmd.startsWith("LCD:")) {
    String texto = cmd.substring(4);
    int separador = texto.indexOf('|');
    if (separador > 0) {
      String linea1 = texto.substring(0, separador);
      String linea2 = texto.substring(separador + 1);
      mostrarMensajeLCD(linea1, linea2);
    }
  }
  else {
    Serial.println("ERROR:Comando desconocido");
  }
}

// ===== COMANDO READ: Leer UID de la tarjeta =====
void comandoRead() {
  mostrarMensajeLCD("Acerque su", "tarjeta NFC...");
  
  // Esperar hasta 5 segundos por una tarjeta
  unsigned long startTime = millis();
  while (millis() - startTime < 5000) {
    if (mfrc522.PICC_IsNewCardPresent() && mfrc522.PICC_ReadCardSerial()) {
      // Tarjeta detectada
      String uid = "";
      for (byte i = 0; i < mfrc522.uid.size; i++) {
        if (mfrc522.uid.uidByte[i] < 0x10) uid += "0";
        uid += String(mfrc522.uid.uidByte[i], HEX);
      }
      uid.toUpperCase();
      
      Serial.println("UID:" + uid);
      mostrarMensajeLCD("UID Leido:", uid);
      blinkLED(LED_GREEN, 2);
      
      mfrc522.PICC_HaltA();
      mfrc522.PCD_StopCrypto1();
      return;
    }
    delay(100);
  }
  
  // Timeout - no se detectó tarjeta
  Serial.println("ERROR:No se detectó tarjeta");
  mostrarMensajeLCD("Error:", "Sin tarjeta");
  blinkLED(LED_RED, 2);
}

// ===== COMANDO WRITE: Escribir datos en tarjeta =====
void comandoWrite(String datos) {
  // Parsear datos: IdPasajero|Nombre|Cedula
  int sep1 = datos.indexOf('|');
  int sep2 = datos.lastIndexOf('|');
  
  if (sep1 == -1 || sep2 == -1 || sep1 == sep2) {
    Serial.println("ERROR:Formato de datos incorrecto");
    mostrarMensajeLCD("Error:", "Datos invalidos");
    blinkLED(LED_RED, 2);
    return;
  }
  
  String idPasajero = datos.substring(0, sep1);
  String nombre = datos.substring(sep1 + 1, sep2);
  String cedula = datos.substring(sep2 + 1);
  
  mostrarMensajeLCD("Acerque tarjeta", "para escribir...");
  
  // Esperar tarjeta
  unsigned long startTime = millis();
  while (millis() - startTime < 5000) {
    if (mfrc522.PICC_IsNewCardPresent() && mfrc522.PICC_ReadCardSerial()) {
      
      // Escribir datos en los bloques
      bool success = true;
      
      // Bloque 4: IdPasajero (convertir a 4 bytes)
      byte dataId[16] = {0};
      int id = idPasajero.toInt();
      dataId[0] = (id >> 24) & 0xFF;
      dataId[1] = (id >> 16) & 0xFF;
      dataId[2] = (id >> 8) & 0xFF;
      dataId[3] = id & 0xFF;
      success &= writeBlock(BLOCK_ID, dataId);
      
      // Bloque 5: Nombre (máximo 16 caracteres)
      byte dataNombre[16] = {0};
      nombre.getBytes(dataNombre, 16);
      success &= writeBlock(BLOCK_NOMBRE, dataNombre);
      
      // Bloque 6: Cedula (máximo 16 caracteres)
      byte dataCedula[16] = {0};
      cedula.getBytes(dataCedula, 16);
      success &= writeBlock(BLOCK_CEDULA, dataCedula);
      
      mfrc522.PICC_HaltA();
      mfrc522.PCD_StopCrypto1();
      
      if (success) {
        Serial.println("OK");
        mostrarMensajeLCD("Datos escritos", "exitosamente!");
        blinkLED(LED_GREEN, 3);
      } else {
        Serial.println("ERROR:Fallo al escribir");
        mostrarMensajeLCD("Error:", "Escritura fallo");
        blinkLED(LED_RED, 3);
      }
      return;
    }
    delay(100);
  }
  
  Serial.println("ERROR:No se detectó tarjeta");
  mostrarMensajeLCD("Error:", "Sin tarjeta");
  blinkLED(LED_RED, 2);
}

// ===== COMANDO UPDATE: Actualizar datos en tarjeta =====
void comandoUpdate(String datos) {
  // UPDATE usa la misma lógica que WRITE
  // En NFC, actualizar es sobrescribir los bloques
  comandoWrite(datos);
}

// ===== COMANDO DELETE: Borrar datos de la tarjeta =====
void comandoDelete() {
  mostrarMensajeLCD("Acerque tarjeta", "para borrar...");
  
  unsigned long startTime = millis();
  while (millis() - startTime < 5000) {
    if (mfrc522.PICC_IsNewCardPresent() && mfrc522.PICC_ReadCardSerial()) {
      
      // Escribir ceros en todos los bloques de datos
      byte emptyData[16] = {0};
      bool success = true;
      
      success &= writeBlock(BLOCK_ID, emptyData);
      success &= writeBlock(BLOCK_NOMBRE, emptyData);
      success &= writeBlock(BLOCK_CEDULA, emptyData);
      
      mfrc522.PICC_HaltA();
      mfrc522.PCD_StopCrypto1();
      
      if (success) {
        Serial.println("OK");
        mostrarMensajeLCD("Tarjeta borrada", "exitosamente!");
        blinkLED(LED_GREEN, 3);
      } else {
        Serial.println("ERROR:Fallo al borrar");
        mostrarMensajeLCD("Error:", "Borrado fallo");
        blinkLED(LED_RED, 3);
      }
      return;
    }
    delay(100);
  }
  
  Serial.println("ERROR:No se detectó tarjeta");
  mostrarMensajeLCD("Error:", "Sin tarjeta");
  blinkLED(LED_RED, 2);
}

// ===== FUNCIÓN AUXILIAR: Escribir bloque en NFC =====
bool writeBlock(byte blockAddr, byte data[]) {
  MFRC522::StatusCode status;
  
  // Autenticar con clave A
  status = mfrc522.PCD_Authenticate(MFRC522::PICC_CMD_MF_AUTH_KEY_A, blockAddr, &key, &(mfrc522.uid));
  if (status != MFRC522::STATUS_OK) {
    return false;
  }
  
  // Escribir datos
  status = mfrc522.MIFARE_Write(blockAddr, data, 16);
  if (status != MFRC522::STATUS_OK) {
    return false;
  }
  
  return true;
}

// ===== FUNCIÓN AUXILIAR: Leer bloque de NFC =====
bool readBlock(byte blockAddr, byte buffer[]) {
  MFRC522::StatusCode status;
  byte size = 18;
  
  // Autenticar
  status = mfrc522.PCD_Authenticate(MFRC522::PICC_CMD_MF_AUTH_KEY_A, blockAddr, &key, &(mfrc522.uid));
  if (status != MFRC522::STATUS_OK) {
    return false;
  }
  
  // Leer datos
  status = mfrc522.MIFARE_Read(blockAddr, buffer, &size);
  if (status != MFRC522::STATUS_OK) {
    return false;
  }
  
  return true;
}

// ===== FUNCIÓN AUXILIAR: Mostrar mensaje en LCD =====
void mostrarMensajeLCD(String linea1, String linea2) {
  lcd.clear();
  lcd.setCursor(0, 0);
  lcd.print(linea1.substring(0, 16));  // Máximo 16 caracteres
  lcd.setCursor(0, 1);
  lcd.print(linea2.substring(0, 16));
}

// ===== FUNCIÓN AUXILIAR: Parpadear LED =====
void blinkLED(int pin, int times) {
  for (int i = 0; i < times; i++) {
    digitalWrite(pin, HIGH);
    delay(200);
    digitalWrite(pin, LOW);
    delay(200);
  }
}
