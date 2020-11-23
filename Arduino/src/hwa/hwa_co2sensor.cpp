/**
 * 
 * @file hwa_co2sensor.c
 * @author Stefan Wick
 * 
 * @brief CO2-Sensor HW Layer
 * 
 */

#include "hwa/hwa_co2sensor.h"
#include <Wire.h>
#include "SparkFun_SCD30_Arduino_Library.h"
#include <Arduino.h>
#include <stdbool.h>


/***************************************/
/*****       GLOBAL VARIABLES      *****/
/***************************************/

#define MINCO2RANDOM 300
#define MAXCO2RANDOM 600

#define MINTEMPRANDOM 20
#define MAXTEMPRANDOM 26

#define MINHUMIDRANDOM 30
#define MAXHUMIDRANDOM 40

static SCD30 co2Sensor;
static bool sensorAttached;

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
float hwa_co2sensor_getCO2(void){
    if(sensorAttached){
        return co2Sensor.getCO2();
    } else {
        return (float)random(MINCO2RANDOM, MAXCO2RANDOM);
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
float hwa_co2sensor_getTemperature(void){
    if(sensorAttached){
        return co2Sensor.getTemperature();
    } else {
        return (float)random(MINTEMPRANDOM, MAXTEMPRANDOM);
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
float hwa_co2sensor_getHumidity(void){
    if(sensorAttached){
        return co2Sensor.getHumidity();
    } else {
        return (float)random(MINHUMIDRANDOM, MAXHUMIDRANDOM);
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