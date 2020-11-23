package com.co2team.covidbuster.model;

public class SensorData {
    /**
     * Id of the room as it is set for each peripheral in config.h
     */
    int roomId;
    int co2Value;
    int temperatureValue;
    int humidityValue;
    int batteryValue;
    int rssiAt1m;

    public SensorData(int roomId, int co2Value, int temperatureValue, int humidityValue, int batteryValue, int rssiAt1m){
        this.roomId = roomId;
        this.co2Value = co2Value;
        this.temperatureValue = temperatureValue;
        this.humidityValue = humidityValue;
        this.batteryValue = batteryValue;
        this.rssiAt1m = rssiAt1m;
    }

    public static SensorData processPayload(byte[] payload){
        int roomId = payload[4];
        int co2 = (payload[7] << 8)| (payload[6] & 0xFF);
        int temperature = payload[8];
        int humidity = payload[9];
        int battery = payload[10];
        int rssi = payload[11];
        return new SensorData(roomId, co2, temperature, humidity, battery, rssi);
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
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
