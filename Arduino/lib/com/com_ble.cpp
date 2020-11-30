/**
 * 
 * @file com_ble.c
 * @author Stefan Wick
 * 
 * @brief Bluetooth Low Energy (BLE)
 * 
 */
#include <com_ble.h>
#include <bluefruit.h>
#include <SPI.h>
#include "../../src/config.h"

/***************************************/
/*****       GLOBAL VARIABLES      *****/
/***************************************/

#define MANUFACTURER_ID 0x004D
#define BEACON_TYPE 0x02
#define IDENTIFIER 0x1337

static advData_t payloadData = {
        .manufacturer = MANUFACTURER_ID, //Change to manufacturer (0x004C = Apple)
        .beacon_type = BEACON_TYPE,    // proximity type
        .beacon_len = sizeof(payloadData)-4, // length without manufacturer, type and length
        .identifier = IDENTIFIER,       // identifier for check
        .roomValue = ROOMIDENTIFIER,    // Room ID (hex no.)
        .co2Value = 0x0,    // current CO2 value
        .temperatureValue = 0x0,    //current temperature value
        .humidityValue = 0x0,   // current humidity value
        .batteryLevel = 0x0,    //current battery level
        .rssi_at_1m = -40   // RSSI at 1m
};

/***************************************/
/*****       PUBLIC FUNCTIONS      *****/
/***************************************/

/**
 * Init BLE
 * 
 * @param none
 * @return none
 * 
 */
void com_ble_init(void){
    Bluefruit.begin();
    Bluefruit.setName(DEVICENAME);
    Bluefruit.ScanResponse.addName();
    Bluefruit.autoConnLed(true);
    Bluefruit.setTxPower(0); //dBm
}

/**
 * 
 * @brief Start Advertising
 * 
 * @param none
 * @return none
 * 
 */
void com_ble_startAdv(void){  
    // Advertising packet
    Bluefruit.Advertising.addData(BLE_GAP_AD_TYPE_MANUFACTURER_SPECIFIC_DATA,&payloadData,sizeof(payloadData));
    Bluefruit.Advertising.setType(BLE_GAP_ADV_TYPE_NONCONNECTABLE_SCANNABLE_UNDIRECTED);
    Bluefruit.Advertising.restartOnDisconnect(true);
    Bluefruit.Advertising.setInterval(160, 160);    // in unit of 0.625 ms
    Bluefruit.Advertising.start(0); 
}

/**
 * 
 * @brief set payload for advertising data
 * 
 * @param value: payload values
 * @return none
 * 
 */
void com_ble_setPayload(advData_t value){
    payloadData = value;
    Bluefruit.Advertising.clearData();
    Bluefruit.Advertising.addData(BLE_GAP_AD_TYPE_MANUFACTURER_SPECIFIC_DATA,&payloadData,sizeof(payloadData));
}

/**
 * @brief returns the current payload
 * 
 * @param none
 * @return: adv_data payload
 * 
 */
advData_t com_ble_getPayload(void){
    return payloadData;
}
