package com.example.igro.Models.SensorData;

public class SensorData {
    private double humidity;
    private double temperatureC;
    private double pressure;
    private double temperatureF;
    private int uv;
    private long time;

    public SensorData(){

    }

    public SensorData(double humidity, double temperatureC, int uv, long time) {
        this.humidity = humidity;
        this.temperatureC = temperatureC;
        this.uv = uv;
        this.time = time;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public double getTemperatureC() {
        return temperatureC;
    }

    public void setTemperatureC(double temperatureC) {
        this.temperatureC = temperatureC;
    }

    public int getUv() {
        return uv;
    }

    public void setUv(int uv) {
        this.uv = uv;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public double getPressure() {
        return pressure;
    }

    public void setPressure(double pressure) {
        this.pressure = pressure;
    }

    public double getTemperatureF() {
        return temperatureF;
    }

    public void setTemperatureF(double temperatureF) {
        this.temperatureF = temperatureF;
    }
}
