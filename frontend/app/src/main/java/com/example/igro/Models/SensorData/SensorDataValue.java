package com.example.igro.Models.SensorData;

public class SensorDataValue {
    private long time;
    private double valule;

    public SensorDataValue() {
    }

    public SensorDataValue(long time, double valule) {
        this.time = time;
        this.valule = valule;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public double getValule() {
        return valule;
    }

    public void setValule(double valule) {
        this.valule = valule;
    }
}
