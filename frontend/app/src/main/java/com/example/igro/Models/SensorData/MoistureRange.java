package com.example.igro.Models.SensorData;

public class MoistureRange {

    private String lowMoistureValue;
    private String highMoistureValue;

    public MoistureRange(String lowMoistureValue, String highMoistureValue) {
        this.lowMoistureValue = lowMoistureValue;
        this.highMoistureValue = highMoistureValue;
    }

    public String getLowMoistureValue() {
        return lowMoistureValue;
    }

    public void setLowMoistureValue(String lowMoistureValue) {
        this.lowMoistureValue = lowMoistureValue;
    }

    public String getHighMoistureValue() {
        return highMoistureValue;
    }

    public void setHighMoistureValue(String highMoistureValue) {
        this.highMoistureValue = highMoistureValue;
    }
}