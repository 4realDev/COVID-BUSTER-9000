/**
 * @file app_alert.h
 * @author Stefan Wick
 * 
 * @brief .h File the alert part
 * 
 */
#ifndef _APP_ALERT_h
#define _APP_ALERT_h

#include <stdint.h>

/***************************************/
/*****       PUBLIC VARIABLES      *****/
/***************************************/

typedef enum{
    NORMAL,
    WARNING,
    DANGER
} alert_t;

typedef enum{
    OK,
    CRITICAL
} batteryState_t;

/***************************************/
/***** PUBLIC FUNCTION PROTOTYPES  *****/
/***************************************/

void app_alert_init(void);
void app_alert_checkAlertState(void);
void app_alert_setAlert(alert_t newAlertState);
void app_alert_setBattery(batteryState_t state);

#endif //_APP_ALERT_h