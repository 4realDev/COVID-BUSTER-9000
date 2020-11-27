/**
 * @file hwa_button.cpp
 * @author Stefan Wick
 * 
 * @brief .h File for button
 * 
 */
#ifndef _HWA_BUTTON_h
#define _HWA_BUTTON_h

#include <stdint.h>

/***************************************/
/*****       PUBLIC VARIABLES      *****/
/***************************************/

#define BUTTONPIN 7

/***************************************/
/***** PUBLIC FUNCTION PROTOTYPES  *****/
/***************************************/

void hwa_button_init(void);
uint8_t hwa_button_getButtonState(void);
void hwa_button_callbackFunction(void (*ptr)());
void hwa_button_setCallbackFunction(void (*externalCallbackFunction)());
#endif //_HWA_BUTTON_h

