/**
 * 
 * @file hwa_led.cpp
 * @author Stefan Wick
 * 
 * @brief LED HW Layer
 * 
 */

#include <hwa_led.h>
#include <Arduino.h>

#include <stdint.h>

#include "Adafruit_NeoPixel.h"

/***************************************/
/*****       GLOBAL VARIABLES      *****/
/***************************************/

static Adafruit_NeoPixel pixel = Adafruit_NeoPixel(1, PIN_NEOPIXEL, NEO_GRB + NEO_KHZ800);
static uint32_t color = 0;

/***************************************/
/*****       PUBLIC FUNCTIONS      *****/
/***************************************/

/**
 * Init the build-in LED
 * 
 * @param none
 * @return none
 * 
 */
void hwa_led_init(void){
    pinMode(REDPIN, OUTPUT);
    pinMode(BLUEPIN, OUTPUT);
    hwa_led_clearColor();
}


/**
 * 
 * Set LED to color
 * 
 * @param red: Red LED [0...255]
 * @param green: Green LED [0...255]
 * @param blue: Blue LED [0...255]
 * @return none
 * 
 */
void hwa_led_setColor(uint8_t red, uint8_t green, uint8_t blue){
    color = pixel.Color(red, green, blue);
    pixel.setPixelColor(0, color);
    pixel.show();

}


/**
 * 
 * Brightness in percent (0 = off, 100 full power)
 * 
 * @param brightness: Brightness of LEDs (0 - 100%)
 * @return none
 * 
 */
void hwa_led_setBrightness(float brightness){
    if(brightness > 100){
        brightness = 100;
    }
    pixel.setBrightness(brightness/100.0*255);
    pixel.show();
}

/**
 * 
 * Clears LED (0,0,0)
 * 
 * @param none
 * @return none
 * 
 */
void hwa_led_clearColor(void){
    pixel.clear();   
    pixel.show(); 
}

/**
 * Set build-in status LED
 * 
 * @param color (RED or BLUE)
 * @return none
 * 
 */
void hwa_led_setStatusLED(pinColor_t color){
    switch(color){
        case RED: {
            digitalWrite(REDPIN, HIGH);
            break;
            }
        case BLUE: {
            digitalWrite(BLUEPIN, HIGH);
            break;
        }
        default: {
            //ERROR
            break;
        }
    }
}

/**
 * 
 * Clear build-in status LED
 * 
 * @param color (RED or BLUE)
 * @return none
 * 
 */
void hwa_led_clearStatusLED(pinColor_t color){
    switch(color){
        case RED: {
            digitalWrite(REDPIN, LOW);
            break;
            }
        case BLUE: {
            digitalWrite(BLUEPIN, LOW);
            break;
        }
        default: {
            //ERROR
            break;
        }
    }
}