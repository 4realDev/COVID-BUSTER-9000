/**
 * 
 * @file app_measurement.cpp
 * @author Stefan Wick
 * 
 * @brief Applicationfile for the measurements
 * 
 */

#include <Arduino.h>

#include <app_measurement.h>

#include <hwa_co2sensor.h>
#include <hwa_battery.h>

/***************************************/
/*****       GLOBAL VARIABLES      *****/
/***************************************/

static measurementValue_t value;

/***************************************/
/*****       PUBLIC FUNCTIONS      *****/
/***************************************/

/**
 * Init Measurement
 * 
 * @param none
 * @return none
 * 
 */
void app_measurement_init(void){
    hwa_co2sensor_init();
    hwa_battery_init();
}

/**
 * 
 * @brief If data is available, get all the sensor data
 * 
 * @param none
 * @return none
 * 
 */
void app_measurement_check(void){
    if(hwa_co2sensor_dataAvailable()){
        value.co2Value = hwa_co2sensor_getCO2();
        value.temperatureValue = hwa_co2sensor_getTemperature();
        value.humidtyValue = hwa_co2sensor_getHumidity();
        value.batteryValue = hwa_button_getBatteryStatus();
    }
}

/**
 * 
 * @brief Returns the current value of the sensors
 * 
 * @param none
 * @return measurementValue_t current value
 * 
 */
measurementValue_t app_measurement_getCurrentValue(void){
    return value;
}