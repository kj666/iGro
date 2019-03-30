package com.example.igro.Models.SensorData;

/**
 * Historical data for all sensors
 */
public class SensorData {
    private double humidity;
    private double temperatureC;
    private double temperatureF;
    private double soil;
    private int uv;
    private long time;
    private int IR; //Infra red
    private double uvIndex;
    private int visibleLight;

    public SensorData(){
    }

    public SensorData(double humidity, double temperatureC, double temperatureF
            ,int uv, double soil, long time) {
        this.humidity = humidity;
        this.temperatureC = temperatureC;
        this.temperatureF = temperatureF;
        this.uv = uv;
        this.soil = soil;
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
        if(uv<=5)
            return 0;
        else
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


    public double getTemperatureF() {
        return temperatureF;
    }

    public void setTemperatureF(double temperatureF) {
        this.temperatureF = temperatureF;
    }

    public double getSoil() {
        return soil;
    }

    public void setSoil(double soilMoisture) {
        this.soil = soilMoisture;
    }

    public int getIR() {
        return IR;
    }

    public void setIR(int IR) {
        this.IR = IR;
    }

    public double getUvIndex() {
        return uvIndex;
    }

    public void setUvIndex(double uvIndex) {
        this.uvIndex = uvIndex;
    }

    public int getVisibleLight() {
        return visibleLight;
    }

    public void setVisibleLight(int visibleLight) {
        this.visibleLight = visibleLight;
    }
}
