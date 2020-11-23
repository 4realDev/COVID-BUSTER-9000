/**
 * 
 * @file main.cpp
 * @author Stefan Wick
 * 
 * @brief Main file
 * 
 */
#include <Arduino.h>

#include "hwa/hwa_co2sensor.h"
#include "com/com_ble.h"
#include "hwa/hwa_led.h"
#include "hwa/hwa_button.h"
#include "hwa/hwa_battery.h"
#include "config.h"    

/***************************************/
/*****       GLOBAL VARIABLES      *****/
/***************************************/


/***************************************/
/***** PRIVATE FUNCTION PROTOTYPES *****/
/***************************************/


/***************************************/
/*****       PUBLIC FUNCTIONS      *****/
/***************************************/

/**
 * @brief Setup the microcontroller
 * 
 * @param none
 * @return none
 * 
 */
void setup() {
    Serial.begin(115200);
    hwa_co2sensor_init();
    com_ble_init();
    hwa_button_init();
    hwa_led_init();
    hwa_battery_init();

    hwa_led_setColor(0, 0xff, 0);
    hwa_led_setBrightness(50);

    // Setup the advertising packet
    com_ble_startAdv();
}
    
   
/**
 * 
 * @brief Loop (aka. while(1))
 * 
 * @param none
 * @return none
 * 
 */
void loop() {   
    advData_t newPayload = com_ble_getPayload();
    newPayload.co2Value = hwa_co2sensor_getCO2();
    newPayload.temperatureValue = hwa_co2sensor_getTemperature();
    newPayload.humidityValue = hwa_co2sensor_getHumidity();
    newPayload.batteryLevel = hwa_button_getBatteryStatus();
    com_ble_setPayload(newPayload);
    delay(100);
}