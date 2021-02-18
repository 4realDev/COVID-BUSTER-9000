# Unit Tests
## Getting started
* Open up VS Code with the according project
* Plug in the microcontroller (CO2-sensor is not required)
* On the left sidebar in VS Code, click on the PlatformIO icon
* You will see the Project Tasks. On env:adafruit_feater_nrf52840_sense you will see "General", "Platform", "Advanced" and "Remove Development". Open up "Advanced"
* Click on "Test". The test will now start. 

## Test description
By clicking on "Test", PlatformIO will compile the test_main.cpp file, which contains all the tests. It consists of six different tests:

* **Basic test:** Test the basic funcionallity (digitalWrite and digitalRead)
* **LED test:** Test the two LEDs (blue and red) as well as the NeoPixel-RGB-LED with a random-choosen color
* **Sensor test:** Get the co2-, temperature- and humidity-value and checks if the value is in a valid range
* **Payload test:** Checks if the payload send over BLE is valid and does not contain any suspicious values
* **Alert test:** Checks if the alert functions changes the LEDs on the Adafruit-board
* **Measurement test:** Checks if the measurement function gets all new measurements.

