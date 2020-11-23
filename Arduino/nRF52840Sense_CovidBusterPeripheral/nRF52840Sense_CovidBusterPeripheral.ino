#include <bluefruit.h>

#include "Adafruit_SHT31.h"
#include "SparkFun_SCD30_Arduino_Library.h"
#include "SPI.h"

#include "hwa_co2sensor.h"

// Custom peripheral, use 128-bit UUIDs
// 6b75fded-006c-4f1b-8e32-a20d9d19aa13 GUID =>
// 6b75xxxx-006c-4f1b-8e32-a20d9d19aa13 Base UUID =>
// 6b750001-006c-4f1b-8e32-a20d9d19aa13 Humidity Service
// 6b750002-006c-4f1b-8e32-a20d9d19aa13 Humidity Measurement Chr. [R, N]
// 6b750003-006c-4f1b-8e32-a20d9d19aa13 Temperature Measurement Chr. [R, N]
// 6b750004-006c-4f1b-8e32-a20d9d19aa13 Heater State Chr. [W]

// The arrays below are ordered "least significant byte first":
uint8_t const humidityServiceUuid[] = { 0x13, 0xaa, 0x19, 0x9d, 0x0d, 0xa2, 0x32, 0x8e, 0x1b, 0x4f, 0x6c, 0x00, 0x01, 0x00, 0x75, 0x6b };
uint8_t const humidityMeasurementCharacteristicUuid[] = { 0x13, 0xaa, 0x19, 0x9d, 0x0d, 0xa2, 0x32, 0x8e, 0x1b, 0x4f, 0x6c, 0x00, 0x02, 0x00, 0x75, 0x6b };
uint8_t const temperatureMeasurementCharacteristicUuid[] = { 0x13, 0xaa, 0x19, 0x9d, 0x0d, 0xa2, 0x32, 0x8e, 0x1b, 0x4f, 0x6c, 0x00, 0x03, 0x00, 0x75, 0x6b };
uint16_t const temperatureMeasurementCharacteristicName[] = { 0x53, 0x61, 0x6c, 0x69 };
uint8_t const heaterStateCharacteristicUuid[] = { 0x13, 0xaa, 0x19, 0x9d, 0x0d, 0xa2, 0x32, 0x8e, 0x1b, 0x4f, 0x6c, 0x00, 0x04, 0x00, 0x75, 0x6b };

Adafruit_SHT31 sht31 = Adafruit_SHT31();
BLEService humidityService = BLEService(humidityServiceUuid);
BLECharacteristic humidityMeasurementCharacteristic = BLECharacteristic(humidityMeasurementCharacteristicUuid);
BLECharacteristic temperatureMeasurementCharacteristic = BLECharacteristic(temperatureMeasurementCharacteristicUuid);
BLECharacteristic heaterStateCharacteristic = BLECharacteristic(heaterStateCharacteristicUuid);


#define MANUFACTURER_ID   0x004C 
 
// AirLocate UUID: E2C56DB5-DFFB-48D2-B060-D0F5A71096E0
uint8_t beaconUuid[16] = 
{ 
  0xE2, 0xC5, 0x6D, 0xB5, 0xDF, 0xFB, 0x48, 0xD2, 
  0xB0, 0x60, 0xD0, 0xF5, 0xA7, 0x10, 0x96, 0xE0, 
};
 
// A valid Beacon packet consists of the following information:
// UUID, Major, Minor, RSSI @ 1M
BLEBeacon beacon(beaconUuid, 0x0000, 0x0000, -54);
 

void connectedCallback(uint16_t connectionHandle) {
  char centralName[32] = { 0 };
  BLEConnection *connection = Bluefruit.Connection(connectionHandle);
  connection->getPeerName(centralName, sizeof(centralName));
  Serial.print(connectionHandle);
  Serial.print(", connected to ");
  Serial.print(centralName);
  Serial.println();
}

void disconnectedCallback(uint16_t connectionHandle, uint8_t reason) {
  Serial.print(connectionHandle);
  Serial.print(" disconnected, reason = ");
  Serial.println(reason); // see https://github.com/adafruit/Adafruit_nRF52_Arduino
  // /blob/master/cores/nRF5/nordic/softdevice/s140_nrf52_6.1.1_API/include/ble_hci.h
  Serial.println("Advertising ...");
}

void cccdCallback(uint16_t connectionHandle, BLECharacteristic* characteristic, uint16_t cccdValue) {
  if (characteristic->uuid == humidityMeasurementCharacteristic.uuid) {
    Serial.print("Humidity Measurement 'Notify', ");
    if (characteristic->notifyEnabled()) {
      Serial.println("enabled");
    } else {
      Serial.println("disabled");
    }
  }
  
  if (characteristic->uuid == temperatureMeasurementCharacteristic.uuid) {
    Serial.print("Temperature Measurement 'Notify', ");
    if (characteristic->notifyEnabled()) {
      Serial.println("enabled");
    } else {
      Serial.println("disabled");
    }
  }
}

void writeCallback(uint16_t connectionHandle, BLECharacteristic* characteristic, uint8_t* data, uint16_t len) {
  if (characteristic->uuid == heaterStateCharacteristic.uuid) {
    Serial.print("Heater State 'Write', heater ");
    bool enabled = data[0] != 0x00;
    sht31.heater(enabled);
    Serial.println(enabled ? "enabled" : "disabled");
  }
}

void setupHumidityService() {
  humidityService.begin(); // Must be called before calling .begin() on its characteristics

  humidityMeasurementCharacteristic.setProperties(CHR_PROPS_READ | CHR_PROPS_NOTIFY);
  humidityMeasurementCharacteristic.setPermission(SECMODE_OPEN, SECMODE_NO_ACCESS);
  humidityMeasurementCharacteristic.setFixedLen(2);
  humidityMeasurementCharacteristic.setCccdWriteCallback(cccdCallback);  // Optionally capture CCCD updates
  humidityMeasurementCharacteristic.begin();
  humidityMeasurementCharacteristic.addDescriptor(temperatureMeasurementCharacteristic.uuid, temperatureMeasurementCharacteristicName, sizeof(temperatureMeasurementCharacteristicName));
  // TODO: Try adding a description with
  // addDescriptor    err_t addDescriptor(BLEUuid bleuuid, void const * content, uint16_t len, BleSecurityMode read_perm = SECMODE_OPEN, BleSecurityMode write_perm = SECMODE_NO_ACCESS);

  temperatureMeasurementCharacteristic.setProperties(CHR_PROPS_READ | CHR_PROPS_NOTIFY);
  temperatureMeasurementCharacteristic.setPermission(SECMODE_OPEN, SECMODE_NO_ACCESS);
  temperatureMeasurementCharacteristic.setFixedLen(2);
  temperatureMeasurementCharacteristic.setCccdWriteCallback(cccdCallback);  // Optionally capture CCCD updates
  temperatureMeasurementCharacteristic.begin();

  heaterStateCharacteristic.setProperties(CHR_PROPS_WRITE | CHR_PROPS_WRITE_WO_RESP);
  heaterStateCharacteristic.setPermission(SECMODE_NO_ACCESS, SECMODE_OPEN);
  heaterStateCharacteristic.setFixedLen(1);
  heaterStateCharacteristic.setWriteCallback(writeCallback, true);
  heaterStateCharacteristic.begin();
}

void startAdvertising() {
  Bluefruit.Advertising.addFlags(BLE_GAP_ADV_FLAGS_LE_ONLY_GENERAL_DISC_MODE);
  Bluefruit.Advertising.addTxPower();
  Bluefruit.Advertising.addService(humidityService);
  Bluefruit.Advertising.addName();

  // See https://developer.apple.com/library/content/qa/qa1931/_index.html   
  const int fastModeInterval = 32; // * 0.625 ms = 20 ms
  const int slowModeInterval = 244; // * 0.625 ms = 152.5 ms
  const int fastModeTimeout = 30; // s
  Bluefruit.Advertising.restartOnDisconnect(true);
  Bluefruit.Advertising.setInterval(fastModeInterval, slowModeInterval);
  Bluefruit.Advertising.setFastTimeout(fastModeTimeout);
  // 0 = continue advertising after fast mode, until connected
  Bluefruit.Advertising.start(0);
  Serial.println("Advertising ...");
}

void setup() {
    Serial.begin(115200);
    // This line will only be visible when the serial monitor is started before starting the board
    Serial.println("Setup");

    hwa_co2sensor_init();


    sht31.begin(0x44);
    sht31.heater(true);

    Bluefruit.begin();
    Bluefruit.setTxPower(0);
    Bluefruit.setName("BLETest");
    beacon.setManufacturer(MANUFACTURER_ID);
    startAdv();
    Serial.println("Broadcasting beacon, open your beacon app to test");
 
    // Suspend Loop() to save power, since we didn't have any code there
    suspendLoop();
    // Bluefruit.Periph.setConnectCallback(connectedCallback);
    // Bluefruit.Periph.setDisconnectCallback(disconnectedCallback);

    // setupHumidityService();
    // startAdvertising();
}

void startAdv(void)
{  
  // Advertising packet
  // Set the beacon payload using the BLEBeacon class populated
  // earlier in this example
  Bluefruit.Advertising.setBeacon(beacon);
 
  // Secondary Scan Response packet (optional)
  // Since there is no room for 'Name' in Advertising packet
  Bluefruit.ScanResponse.addName();
  
  /* Start Advertising
   * - Enable auto advertising if disconnected
   * - Timeout for fast mode is 30 seconds
   * - Start(timeout) with timeout = 0 will advertise forever (until connected)
   * 
   * Apple Beacon specs
   * - Type: Non connectable, undirected
   * - Fixed interval: 100 ms -> fast = slow = 100 ms
   */
  //Bluefruit.Advertising.setType(BLE_GAP_ADV_TYPE_ADV_NONCONN_IND);
  Bluefruit.Advertising.restartOnDisconnect(true);
  Bluefruit.Advertising.setInterval(160, 160);    // in unit of 0.625 ms
  Bluefruit.Advertising.setFastTimeout(30);      // number of seconds in fast mode
  Bluefruit.Advertising.start(0);                // 0 = Don't stop advertising after n seconds  
}
 

void loop() {
  if (hwa_co2sensor_dataAvailable())
  {
    Serial.print("co2(ppm):");
    Serial.print(hwa_co2sensor_getCO2());

    Serial.print(" temp(C):");
    Serial.print(hwa_co2sensor_getTemperature(), 1);

    Serial.print(" humidity(%):");
    Serial.print(hwa_co2sensor_getHumidity(), 1);

    Serial.println();
  }
  else
    Serial.println("Waiting for new data123");

  delay(500);
  
  if (Bluefruit.connected()) {
    float h = sht31.readHumidity();
    int h2 = h * 100.0; // fixed precision
    uint8_t h2HiByte = (uint8_t) (h2 >> 8);
    uint8_t h2LoByte = (uint8_t) h2;
    uint8_t humidityData[2] = { h2HiByte, h2LoByte };
    //humidityMeasurementCharacteristic.write8(0);

    h = sht31.readTemperature();
    h2 = h * 100.0; // fixed precision
    h2HiByte = (uint8_t) (h2 >> 8);
    h2LoByte = (uint8_t) h2;
    uint8_t temperatureData[2] = { h2HiByte, h2LoByte };
    
    bool notifiedHumidity = humidityMeasurementCharacteristic.notify(humidityData, sizeof(humidityData));
    bool notifiedTemperature = temperatureMeasurementCharacteristic.notify(temperatureData, sizeof(temperatureData));
    
    if (notifiedHumidity) {
      Serial.print("Notified, humidity = ");
      Serial.println(h);
    } else if(notifiedTemperature) {
      Serial.print("Notified, temperature = ");
      Serial.println(h);
    }
    else {
      Serial.println("Notify not set, or not connected");
    }
    //if (heaterStateCharacteristic.read8()) {
    //  Serial.println("Heater enabled");
    //} else {
    //  Serial.println("Heater disabled");
    //}
  }
  delay(1000); // ms
}
