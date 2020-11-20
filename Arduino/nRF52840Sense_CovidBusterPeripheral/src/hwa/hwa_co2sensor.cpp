/**
 * 
 * @file hwa_co2sensor.c
 * @author Stefan Wick
 * 
 * @brief CO2-Sensor HW Layer
 * 
 */
#include "hwa_co2sensor.h"

#include <Arduino.h>
#include <Wire.h>
#include <stdbool.h>

#include "SparkFun_SCD30_Arduino_Library.h"




/***************************************/
/*****       GLOBAL VARIABLES      *****/
/***************************************/

static SCD30 airSensor;

/***************************************/
/*****       PUBLIC FUNCTIONS      *****/
/***************************************/

/**
 * Init the CO2 Sensor
 * 
 * @author Stefan Wick
 * @param none
 * @return noney
 * 
 */
void hwa_co2sensor_init(void){
    Wire.begin();

    if (airSensor.begin() == false) {
        Serial.println("Air sensor not detected. Please check wiring. Freezing...");
        while (1);
    }
    //init stuff!
    //lets say _sample.data2 = importantParam
}

float hwa_co2sensor_getCO2(void){
    return airSensor.getCO2();
}

float hwa_co2sensor_getTemperature(void){
    return airSensor.getTemperature();
}

float hwa_co2sensor_getHumidity(void){
    return airSensor.getHumidity();
}

bool hwa_co2sensor_dataAvailable(void){
    return airSensor.dataAvailable();
}