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

#include <app_alert.h>
#include <app_measurement.h>
#include <hwa_co2sensor.h>
#include <com_ble.h>
#include <hwa_led.h>
#include <hwa_button.h>
#include <hwa_battery.h>
#include "../src/config.h"

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
void test_payload(void);
void test_alert(void);

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
    app_alert_init();
    app_measurement_init();
    com_ble_init();

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
    RUN_TEST(test_payload);
    RUN_TEST(test_alert);
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
 *      Step 3: Check NeoPixel Init state (0x0)
 *      Step 4: Check setting NeoPixel
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

    TEST_ASSERT_EQUAL(0x0, hwa_led_getColor());

    uint8_t randomRed = (uint8_t)random(0,255);
    uint8_t randomGreen = (uint8_t)random(0,255);
    uint8_t randomBlue = (uint8_t)random(0,255);
    hwa_led_setColor(randomRed,randomGreen,randomBlue);
    uint32_t color = (uint32_t)randomRed << 16 | (uint16_t)randomGreen << 8 | randomBlue;
    TEST_ASSERT_EQUAL(color, hwa_led_getColor());
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
            TEST_ASSERT_INT_WITHIN(780, 820, hwa_co2sensor_getCO2());
            TEST_ASSERT_INT_WITHIN(23, 23, hwa_co2sensor_getTemperature());
            TEST_ASSERT_INT_WITHIN(35, 35, hwa_co2sensor_getHumidity());
        } else {
            TEST_ASSERT(0 != hwa_co2sensor_getCO2());
            TEST_ASSERT(0 != hwa_co2sensor_getTemperature());
            TEST_ASSERT(0 != hwa_co2sensor_getHumidity());
        }
    }
}

/**
 * 
 * @brief Check if the payload is valid
 * 
 * @test 
 *      Step 1: Check whole old payload
 *      Step 2: Check whole new payload
 *        
 */
void test_payload(void){
    advData_t newPayload = com_ble_getPayload();
    TEST_ASSERT_EQUAL_MESSAGE(0x004D, newPayload.manufacturer, "Payload inital manufacturer is wrong!");
    TEST_ASSERT_EQUAL_MESSAGE(0x02, newPayload.beacon_type, "Payload inital beacon type is wrong!");
    TEST_ASSERT_EQUAL_MESSAGE(10, newPayload.beacon_len, "Payload inital length is wrong!");
    TEST_ASSERT_EQUAL_MESSAGE(0x1337, newPayload.identifier, "Payload inital identifier is wrong!");
    TEST_ASSERT_EQUAL_MESSAGE(ROOMIDENTIFIER, newPayload.roomValue, "Payload inital identifier is wrong!");
    TEST_ASSERT_EQUAL_MESSAGE(0, newPayload.co2Value, "Payload inital CO2-value is wrong!");
    TEST_ASSERT_EQUAL_MESSAGE(0, newPayload.temperatureValue, "Payload inital temperature-value is wrong!");
    TEST_ASSERT_EQUAL_MESSAGE(0, newPayload.humidityValue, "Payload inital humidity-value is wrong!");
    TEST_ASSERT_EQUAL_MESSAGE(0, newPayload.batteryLevel, "Payload inital battery-value is wrong!");
    TEST_ASSERT_EQUAL_MESSAGE(-40, newPayload.rssiAt1m, "Payload inital RSSI at 1m is wrong!");

    uint8_t randomTemperature = (uint8_t)random(10,30);
    uint8_t randomHumidity = (uint8_t)random(20,80);
    uint16_t randomCo2 = (uint16_t)random(500,3000);
    uint8_t randomBatteryLevel = (uint8_t)random(0,100);
    newPayload.co2Value = randomCo2;
    newPayload.temperatureValue = randomTemperature;
    newPayload.humidityValue = randomHumidity;
    newPayload.batteryLevel = randomBatteryLevel;
    com_ble_setPayload(newPayload);

    advData_t testPayload = com_ble_getPayload();
    TEST_ASSERT_EQUAL_MESSAGE(0x004D, testPayload.manufacturer, "Payload manufacturer is wrong!");
    TEST_ASSERT_EQUAL_MESSAGE(0x02, testPayload.beacon_type, "Payload beacon type is wrong!");
    TEST_ASSERT_EQUAL_MESSAGE(newPayload.beacon_len,testPayload.beacon_len, "Payload Length is wrong!");
    TEST_ASSERT_EQUAL_MESSAGE(0x1337, testPayload.identifier, "Payload identifier is wrong!");
    TEST_ASSERT_EQUAL_MESSAGE(ROOMIDENTIFIER, testPayload.roomValue, "Payload identifier is wrong!");
    TEST_ASSERT_EQUAL_MESSAGE(randomCo2, testPayload.co2Value, "Payload CO2-value is wrong!");
    TEST_ASSERT_EQUAL_MESSAGE(randomTemperature, testPayload.temperatureValue, "Payload temperature-value is wrong!");
    TEST_ASSERT_EQUAL_MESSAGE(randomHumidity, testPayload.humidityValue, "Payload humidity-value is wrong!");
    TEST_ASSERT_EQUAL_MESSAGE(randomBatteryLevel, testPayload.batteryLevel, "Payload battery-value is wrong!");
    TEST_ASSERT_EQUAL_MESSAGE(-40, testPayload.rssiAt1m, "Payload RSSI at 1m is wrong!");
}

/**
 * @brief Test if the alert states will be applied to LED
 * 
 * @test
 *      Step 1: Check normal state
 *      Step 2: Check warning state
 *      Step 3: Check danger state
 * 
 */
void test_alert(void){
    app_alert_setAlert(NORMAL);
    TEST_ASSERT_EQUAL(0x00ff00, hwa_led_getColor());
    app_alert_setAlert(WARNING);
    TEST_ASSERT_EQUAL(0xff4500, hwa_led_getColor());
    app_alert_setAlert(DANGER);
    TEST_ASSERT_EQUAL(0xff0000, hwa_led_getColor());
}