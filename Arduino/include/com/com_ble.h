/**
 * @file hwa_led.cpp
 * @author Stefan Wick
 * 
 * @brief .h File for LED
 * 
 */
#ifndef _COM_BLE_h
#define _COM_BLE_h

#include <stdint.h>

/***************************************/
/*****       PUBLIC VARIABLES      *****/
/***************************************/

typedef struct {
    uint16_t manufacturer;
    uint8_t  beacon_type;
    uint8_t  beacon_len;
    uint16_t identifier;
    uint16_t roomValue;
    uint16_t co2Value;
    uint8_t temperatureValue;
    uint8_t humidityValue;
    uint8_t batteryLevel;
    int8_t rssi_at_1m;
} advData_t;

/***************************************/
/***** PUBLIC FUNCTION PROTOTYPES  *****/
/***************************************/

void com_ble_init(void);
void com_ble_startAdv(void);
void com_ble_setPayload(advData_t value);
advData_t com_ble_getPayload(void);

#endif //_COM_BLE_h