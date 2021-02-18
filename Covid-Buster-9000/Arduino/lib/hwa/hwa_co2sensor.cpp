/**
 * 
 * @file hwa_co2sensor.cpp
 * @author Stefan Wick
 * 
 * @brief CO2-Sensor HW Layer
 * 
 */

#include <hwa_co2sensor.h>
#include <Wire.h>
#include "SparkFun_SCD30_Arduino_Library.h"
#include <Arduino.h>
#include <stdbool.h>
#include <stdint.h>

#include <hwa_button.h>
#include "../../src/config.h"


/***************************************/
/*****       GLOBAL VARIABLES      *****/
/***************************************/

static SCD30 co2Sensor;
static bool sensorAttached;

static uint16_t randomCo2Value = 800;
static uint8_t randomTemperatureValue = 23;
static uint8_t randomHumidityValue = 35;


/***************************************/
/***** PRIVATE FUNCTION PROTOTYPES *****/
/***************************************/

static void setToDangerousCallback(void);

/***************************************/
/*****       PUBLIC FUNCTIONS      *****/
/***************************************/

/**
 * Init the CO2 Sensor
 * 
 * @param none
 * @return none
 * 
 */
void hwa_co2sensor_init(void){
    Wire.begin();
    if (co2Sensor.begin() == false) {
        sensorAttached = false;
        hwa_button_init();
        hwa_button_setCallbackFunction(&setToDangerousCallback);
    } else {
        sensorAttached = true;
    }
}

/**
 * 
 * Returns the CO2 value from SCD30
 * 
 * @param none
 * @return float: current CO2 value
 * 
 */
uint16_t hwa_co2sensor_getCO2(void){
    if(sensorAttached){
        return co2Sensor.getCO2();
    } else {
        randomCo2Value += (uint16_t)random(-1, 2);
        return randomCo2Value;
    }
}

/**
 * 
 * Returns the temperature value
 * 
 * @param none
 * @return float: current temperature value
 * 
 */
uint8_t hwa_co2sensor_getTemperature(void){
    if(sensorAttached){
        return co2Sensor.getTemperature();
    } else {
        randomTemperatureValue += (uint8_t)random(0, 0);
        return randomTemperatureValue;
    } 
}

/**
 * 
 * Returns the humidity value
 * 
 * @param none
 * @return float: current CO2 value
 * 
 */
uint8_t hwa_co2sensor_getHumidity(void){
    if(sensorAttached){
        return co2Sensor.getHumidity();
    } else {
        randomHumidityValue += (uint8_t)random(0, 0);
        return randomHumidityValue;
    }
}

/**
 * 
 * Returns if data is available
 * 
 * @param none
 * @return bool: data is available if true
 * 
 */
bool hwa_co2sensor_dataAvailable(void){
    if(sensorAttached){
        return co2Sensor.dataAvailable();
    } else {
        return true;
    }
}

/**
 * 
 * Returns if sensor is attached
 * 
 * @param none
 * @return bool: sensor is attached
 * 
 */
bool hwa_co2sensor_isAttached(void){
    return sensorAttached;
}

/***************************************/
/*****       PRIVATE FUNCTIONS     *****/
/***************************************/

void setToDangerousCallback(void){
    randomCo2Value = MIN_DANGER_CO2_VALUE+20;
}

