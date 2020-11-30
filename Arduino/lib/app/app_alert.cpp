/**
 * 
 * @file app_altert.c
 * @author Stefan Wick
 * 
 * @brief Applicationfile for the alert state
 * 
 */

#include <Arduino.h>
#include <app_alert.h>

#include <hwa_co2sensor.h>
#include <hwa_battery.h>
#include <hwa_led.h>
#include <hwa_button.h>

/***************************************/
/*****       GLOBAL VARIABLES      *****/
/***************************************/

#define RGB_GREEN 0,255,0
#define RGB_ORANGE 255,69,0
#define RGB_RED 255,0,0

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
void app_alert_init(void){
    hwa_led_init();
}


/**
 * 
 * Set the new alert state
 * 
 * @param newAlertState: the new alert state
 * @return none
 * 
 */
void app_alert_setAlert(alert_t newAlertState){
    switch(newAlertState){
        case NORMAL: {
            hwa_led_setColor(RGB_GREEN);
            hwa_led_setBrightness(50);
            break;
        }
        case WARNING: {
            hwa_led_setColor(RGB_ORANGE);
            hwa_led_setBrightness(50);
            break;
        }
        case DANGER: {
            hwa_led_setColor(RGB_RED);
            hwa_led_setBrightness(50);
            break;
        }
        default: {
            break;
        }
    }
}

/**
 * 
 * Set the new battery state
 * 
 * @param state: new battery state
 * @return none
 * 
 */
void app_alert_setBattery(batteryState_t state){
    if(state == OK){
        hwa_led_clearStatusLED(RED);
    }else if (state == CRITICAL){
        hwa_led_setStatusLED(RED);
    }
    
}


