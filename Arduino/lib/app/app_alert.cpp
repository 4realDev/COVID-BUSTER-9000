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
            //Set LED to GREEN
            hwa_led_setColor(0, 255, 0);
            hwa_led_setBrightness(50);
            break;
        }
        case WARNING: {
            //Set LED to ORANGE
            hwa_led_setColor(255, 69, 0);
            hwa_led_setBrightness(50);
            break;
        }
        case DANGER: {
            //Set LED to RED
            hwa_led_setColor(255, 0, 0);
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


