/**
 * @file hwa_co2sensor.cpp
 * @author Stefan Wick
 * 
 * @brief .h File for CO2 sensor
 * 
 */
#ifndef _HWA_CO2SENSOR_h
#define _HWA_CO2SENSOR_h

/***************************************/
/*****       PUBLIC VARIABLES      *****/
/***************************************/

/***************************************/
/***** PUBLIC FUNCTION PROTOTYPES  *****/
/***************************************/

void hwa_co2sensor_init(void);
float hwa_co2sensor_getCO2(void);
float hwa_co2sensor_getTemperature(void);
float hwa_co2sensor_getHumidity(void);
bool hwa_co2sensor_dataAvailable(void);

#endif //_HWA_CO2SENSOR_h