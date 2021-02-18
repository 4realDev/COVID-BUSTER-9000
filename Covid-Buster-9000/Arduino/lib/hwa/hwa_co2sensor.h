/**
 * @file hwa_co2sensor.cpp
 * @author Stefan Wick
 * 
 * @brief .h File for CO2 sensor
 * 
 */
#ifndef _HWA_CO2SENSOR_h
#define _HWA_CO2SENSOR_h

#include <stdint.h>

/***************************************/
/*****       PUBLIC VARIABLES      *****/
/***************************************/

/***************************************/
/***** PUBLIC FUNCTION PROTOTYPES  *****/
/***************************************/

void hwa_co2sensor_init(void);
uint16_t hwa_co2sensor_getCO2(void);
uint8_t hwa_co2sensor_getTemperature(void);
uint8_t hwa_co2sensor_getHumidity(void);
bool hwa_co2sensor_dataAvailable(void);
bool hwa_co2sensor_isAttached(void);

#endif //_HWA_CO2SENSOR_h