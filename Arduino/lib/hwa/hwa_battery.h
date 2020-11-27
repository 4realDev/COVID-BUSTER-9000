/**
 * @file hwa_led.cpp
 * @author Stefan Wick
 * 
 * @brief .h File for battery
 * 
 */
#ifndef _HWA_BATTERY_h
#define _HWA_BATTERY_h

/***************************************/
/*****       PUBLIC VARIABLES      *****/
/***************************************/


/***************************************/
/***** PUBLIC FUNCTION PROTOTYPES  *****/
/***************************************/

void hwa_battery_init(void);
float hwa_button_getBatteryStatus(void);

#endif //_HWA_BATTERY_h

