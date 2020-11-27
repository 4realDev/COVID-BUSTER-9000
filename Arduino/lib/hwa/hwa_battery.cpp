/**
 * 
 * @file hwa_battery.c
 * @author Stefan Wick
 * 
 * @brief Battery (basically only for battery read out)
 * 
 */
#include <hwa_battery.h>
#include <Arduino.h>

/***************************************/
/*****       GLOBAL VARIABLES      *****/
/***************************************/

#define BATTERYPIN A6
#define BATTERYFULLVOLTAGE 3.3 // 2*1.65V
#define BATTERYEMPTYVOLTAGE 2.8 // 2*1.40V

/***************************************/
/*****       PUBLIC FUNCTIONS      *****/
/***************************************/

/**
 * Init Battery
 * 
 * @author Stefan Wick
 * @param none
 * @return none
 * 
 */
void hwa_battery_init(void){
    uint32_t measuredvbat = analogRead(BATTERYPIN);
    Serial.print(measuredvbat);
}

/**
 * Get battery status
 * 
 * @param none
 * @return Button state
 * 
 */
float hwa_button_getBatteryStatus(void){
    // 2xAA is 2x 1.65V when full, 2x1.4V when empty
    float measuredVBat = analogRead(BATTERYPIN); //max value is 512
    measuredVBat *= BATTERYFULLVOLTAGE;
    measuredVBat /= 512;
    int batteryStorage = ((measuredVBat-BATTERYEMPTYVOLTAGE)*100)/(BATTERYFULLVOLTAGE-BATTERYEMPTYVOLTAGE);

    // 3.3V is a reference value, if connected via USB manually ajust to 100%, and if undervoltage, adjust to 0%
    if(batteryStorage < 0){
        batteryStorage = 0;
    } else if(batteryStorage > 100){
        batteryStorage = 100;
    }

    return (batteryStorage);
}
