/**
 * 
 * @file main.cpp
 * @author Stefan Wick
 * 
 * @brief Main file
 * 
 */
#include <Arduino.h>
#include <app_alert.h>
#include <app_measurement.h>
#include <com_ble.h>
#include "config.h"

/***************************************/
/*****       GLOBAL VARIABLES      *****/
/***************************************/

typedef enum{
    IDLE,
    MEASURE,
    CONTROL,
    ADVERTISE
} states_t;

static states_t state = IDLE;

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
    
    app_alert_init();
    app_measurement_init();

    com_ble_init();
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
    static measurementValue_t localValue;

    switch(state){
        case IDLE: {
            state = MEASURE;
            break;
        }
        case MEASURE: {
            app_measurement_check();
            state = CONTROL;
            break;
        }
        case CONTROL: {
            localValue = app_measurement_getCurrentValue();
            if(localValue.co2Value < MIN_WARNING_CO2_VALUE){
                app_alert_setAlert(NORMAL);
            } else if(localValue.co2Value >= MIN_WARNING_CO2_VALUE && localValue.co2Value < MIN_DANGER_CO2_VALUE){
                app_alert_setAlert(WARNING);
            } else if(localValue.co2Value >= MIN_DANGER_CO2_VALUE){
                app_alert_setAlert(DANGER);
            } else{
                //TODO: ERROR HANDLING: SHOULD NOT BE HERE
            }

            if(localValue.batteryValue < 20){
                app_alert_setBattery(CRITICAL);
            } else {
                app_alert_setBattery(OK);
            }

            state = ADVERTISE;
            break;
        }
        case ADVERTISE: {
            advData_t newPayload = com_ble_getPayload();
            newPayload.co2Value = localValue.co2Value;
            newPayload.temperatureValue = localValue.temperatureValue;
            newPayload.humidityValue = localValue.humidtyValue;
            newPayload.batteryLevel = localValue.batteryValue;
            Serial.println(localValue.co2Value);
            com_ble_setPayload(newPayload);
            delay(100);
            state = IDLE;
            break;
        }
        default: {
            //TODO: ERROR HANDLING: SHOULD NOT BE HERE!
            break;
        }

    }
    
}