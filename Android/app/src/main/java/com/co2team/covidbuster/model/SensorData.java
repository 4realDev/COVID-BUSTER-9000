package com.co2team.covidbuster.model;

public class SensorData {
    int roomValue;
    int co2Value;
    int temperatureValue;
    int humidityValue;
    int batteryValue;
    int rssiAt1m;

    public SensorData(int roomValue, int co2Value, int temperatureValue, int humidityValue, int batteryValue, int rssiAt1m){
        this.roomValue = roomValue;
        this.co2Value = co2Value;
        this.temperatureValue = temperatureValue;
        this.humidityValue = humidityValue;
        this.batteryValue = batteryValue;
        this.rssiAt1m = rssiAt1m;
    }

    public static SensorData processPayload(byte[] payload){
        int room = payload[4];
        int co2 = (int) ((payload[7] << 8)| (payload[6] & 0xFF));
        int temperature = payload[8];
        int humidity = payload[9];
        int battery = payload[10];
        int rssi = payload[11];
        return new SensorData(room, co2, temperature, humidity, battery, rssi);
    }

    public int getRoomValue() {
        return roomValue;
    }

    public void setRoomValue(int roomValue) {
        this.roomValue = roomValue;
    }

    public int getCo2Value(){
        return co2Value;
    }

    public void setCo2Value(int co2Value) {
        this.co2Value = co2Value;
    }

    public int getTemperatureValue() {
        return temperatureValue;
    }

    public void setTemperatureValue(int temperatureValue) {
        this.temperatureValue = temperatureValue;
    }

    public int getHumidityValue() {
        return humidityValue;
    }

    public void setHumidityValue(int humidityValue) {
        this.humidityValue = humidityValue;
    }

    public int getBatteryValue() {
        return batteryValue;
    }

    public void setBatteryValue(int batteryValue) {
        this.batteryValue = batteryValue;
    }


    public int getRssiAt1m() {
        return rssiAt1m;
    }

    public void setRssiAt1m(int rssiAt1m) {
        this.rssiAt1m = rssiAt1m;
    }

}
