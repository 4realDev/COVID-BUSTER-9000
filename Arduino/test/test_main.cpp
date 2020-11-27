/**
 * 
 * @file test_main.cpp
 * @author Stefan Wick
 * 
 * @brief Unit test file
 * 
 */

#include <Arduino.h>
#include <unity.h>
#include <Wire.h>

#include <hwa_co2sensor.h>
#include <com_ble.h>
#include <hwa_led.h>
#include <hwa_button.h>
#include <hwa_battery.h>

#include "Adafruit_NeoPixel.h"
#include "SparkFun_SCD30_Arduino_Library.h"

/***************************************/
/*****       GLOBAL VARIABLES      *****/
/***************************************/

uint8_t i = 0;
uint8_t max_blinks = 5;

/***************************************/
/*****    UNIT TESTS PROTOTYPES    *****/
/***************************************/

void test_basicTest(void);
void test_led(void);
void test_sensor(void);

/***************************************/
/*****            SETUP            *****/
/***************************************/

void setUp(void) {
// set stuff up here
}

void tearDown(void) {
// clean stuff up here
}

/**
 * @brief Setup the microcontroller
 * 
 * @param none
 * @return none
 * 
 */
void setup() {
    //Init hardware

    com_ble_init();
    hwa_button_init();
    hwa_led_init();
    hwa_battery_init();
    pinMode(LED_BUILTIN, OUTPUT);

    // this is needed since mc does not support restart by serial
    delay(2000);

    UNITY_BEGIN();
    
}

/***************************************/
/*****          UNIT TEST          *****/
/***************************************/

/**
 * 
 * @brief Loop (aka. while(1))
 * 
 * @param none
 * @return none
 * 
 */
void loop() {
    RUN_TEST(test_basicTest);
    RUN_TEST(test_led);
    RUN_TEST(test_sensor);
    UNITY_END(); // stop unit testing
}

/***************************************/
/*****     UNIT TEST DEFINTION     *****/
/***************************************/

/**
 * @brief This test checks the funcionality of the basic
 * functions of the LEDs assuming digitalRead is 
 * working (and was being tested by adafruit).
 * 
 * @test 
 *      Step 1: Check function digitalWrite HIGH
 *      Step 2: Check function digitalWrite LOW
 */
void test_basicTest(void){
    digitalWrite(LED_BUILTIN, HIGH);
    TEST_ASSERT_EQUAL(HIGH, digitalRead(LED_BUILTIN));
    digitalWrite(LED_BUILTIN, LOW);
    TEST_ASSERT_EQUAL(LOW, digitalRead(LED_BUILTIN));
}

/**
 * @brief The controller has several LEDs. Two status LEDs (red and blue)
 * and a bigger RGB-LED. Since the NeoPixel does not "offer" a readback
 * the test of the NeoPixel LED is out of this scope.
 * 
 * @test 
 *      Step 1: Check Status LED Red (on and off)
 *      Step 2: Check Status LED Blue (on and off)
 */
void test_led(void){
    hwa_led_setStatusLED(RED);
    TEST_ASSERT_EQUAL(HIGH, digitalRead(REDPIN));
    hwa_led_clearStatusLED(RED);
    TEST_ASSERT_EQUAL(LOW, digitalRead(REDPIN));
    hwa_led_setStatusLED(BLUE);
    TEST_ASSERT_EQUAL(HIGH, digitalRead(BLUEPIN));
    hwa_led_clearStatusLED(BLUE);
    TEST_ASSERT_EQUAL(LOW, digitalRead(BLUEPIN));
}


/**
 * @brief This test checks if the sensors return some meaningful values (or if they are out of range)
 * 
 * @test 
 *      The following tests are repeated five times
 *      Step 1: Check CO2-sensor
 *          - if sensor not attached: check if range is between 300 and 600ppm
 *          - if sensor is attached: check is value returns 0 (sensor not working)
 *      Step 2: Check Temperature sensor
 *          - if sensor not attached: check if range is between 20 and 26 degrees
 *          - if sensor is attached: check is value returns 0 (sensor not working)
 *      Step 3: Check humidity sensor
 *          - if sensor not attached: check if range is between 30 and 40 degrees
 *          - if sensor is attached: check is value returns 0 (sensor not working)
 * 
 */
void test_sensor(void){
    for(uint8_t counter = 0; counter < 5; counter++){
        if(!hwa_co2sensor_isAttached()){
            TEST_ASSERT_INT_WITHIN(150, 450, hwa_co2sensor_getCO2());
            TEST_ASSERT_INT_WITHIN(3, 23, hwa_co2sensor_getTemperature());
            TEST_ASSERT_INT_WITHIN(5, 35, hwa_co2sensor_getHumidity());
        } else {
            TEST_ASSERT(0 != hwa_co2sensor_getCO2());
            TEST_ASSERT(0 != hwa_co2sensor_getTemperature());
            TEST_ASSERT(0 != hwa_co2sensor_getHumidity());
        }
    }
}

