/**
 * @file app_measurement.h
 * @author Stefan Wick
 * 
 * @brief .h File the measurement part
 * 
 */
#ifndef _APP_MEASUREMENT_h
#define _APP_MEASUREMENT_h

#include <stdint.h>

/***************************************/
/*****       PUBLIC VARIABLES      *****/
/***************************************/

typedef struct{
    uint16_t co2Value;
    uint8_t temperatureValue;
    uint8_t humidtyValue;
    uint8_t batteryValue;
} measurementValue_t;


/***************************************/
/***** PUBLIC FUNCTION PROTOTYPES  *****/
/***************************************/

void app_measurement_init(void);
void app_measurement_check(void);
measurementValue_t app_measurement_getCurrentValue(void);

#endif //_APP_MEASUREMENT_h