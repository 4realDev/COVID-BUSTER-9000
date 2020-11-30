/**
 * 
 * @file hwa_button.cpp
 * @author Stefan Wick
 * 
 * @brief Button HW Layer
 * 
 */

#include <hwa_button.h>
#include <Arduino.h>

#include <stdint.h>


/***************************************/
/*****       GLOBAL VARIABLES      *****/
/***************************************/

static void (*callbackFunction)() = 0x0;

/***************************************/
/***** PRIVATE FUNCTION PROTOTYPES *****/
/***************************************/

static void interrupt_buttonPress(void);

/***************************************/
/*****       PUBLIC FUNCTIONS      *****/
/***************************************/

/**
 * @brief Init the button
 * 
 * @param none
 * @return none
 * 
 */
void hwa_button_init(void){
    pinMode(BUTTONPIN, INPUT);
    pinMode(BUTTONPIN, INPUT_PULLUP);
    attachInterrupt(digitalPinToInterrupt(BUTTONPIN), interrupt_buttonPress, RISING);
}

/**
 * Get the button state (note: since it is pullup, the read is inverted!)
 * 
 * @param none
 * @return Button state
 * 
 */
uint8_t hwa_button_getButtonState(void){
    return !digitalRead(BUTTONPIN);
}

/**
 * @brief Defines the callback function if a interrupt triggers
 * 
 * @param externalCallbackFunction: this function is executed when interrupt triggers
 * @return none
 *
 */
void hwa_button_setCallbackFunction(void (*externalCallbackFunction)()){
    callbackFunction = externalCallbackFunction;
}

/***************************************/
/*****       PRIVATE FUNCTIONS     *****/
/***************************************/

static void interrupt_buttonPress(){
    if((*callbackFunction) != 0x0){
        (*callbackFunction)();
    }
}