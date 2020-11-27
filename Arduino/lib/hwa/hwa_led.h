/**
 * @file hwa_led.cpp
 * @author Stefan Wick
 * 
 * @brief .h File for LED
 * 
 */
#ifndef _HWA_LED_h
#define _HWA_LED_h

#include <stdint.h>

/***************************************/
/*****       PUBLIC VARIABLES      *****/
/***************************************/

#define BLUEPIN 4
#define REDPIN 13

typedef enum{
    RED,
    BLUE
} pinColor_t;

/***************************************/
/***** PUBLIC FUNCTION PROTOTYPES  *****/
/***************************************/

void hwa_led_init(void);
void hwa_led_setColor(uint8_t red, uint8_t green, uint8_t blue);
void hwa_led_setBrightness(float brightness);
void hwa_led_clearColor(void);
void hwa_led_setStatusLED(pinColor_t color);
void hwa_led_clearStatusLED(pinColor_t color);



#endif //_HWA_LED_h

