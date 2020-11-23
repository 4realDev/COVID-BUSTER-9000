#ifndef _HWA_CO2SENSOR_h
#define _HWA_CO2SENSOR_h



void hwa_co2sensor_init(void);
float hwa_co2sensor_getCO2(void);
float hwa_co2sensor_getTemperature(void);
float hwa_co2sensor_getHumidity(void);
bool hwa_co2sensor_dataAvailable(void);

#endif //_HWA_CO2SENSOR_h