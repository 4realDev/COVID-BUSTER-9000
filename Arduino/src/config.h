/**
 * 
 * @file config.h
 * @author Stefan Wick
 * 
 * @brief Configuration file for firmware
 * 
 */

/***************************************/
/*****       PUBLIC VARIABLES      *****/
/***************************************/

#define DEVICENAME "COVID BUSTER PERIPHERAL"

//To be changed
#define ROOMNAME "ZL6.0"
#define ROOMIDENTIFIER 0x1

// Health definitions
#define MIN_WARNING_CO2_VALUE 1000    // in ppm
#define MIN_DANGER_CO2_VALUE 2000    // in ppm