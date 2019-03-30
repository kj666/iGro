package com.example.igro.Models.SensorData;

public class SensorDataValue {
    private long time;
    private double value;

    public SensorDataValue() {
    }

    public SensorDataValue(long time, double value) {
        this.time = time;
        this.value = value;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
