package com.example.igro.Models.SensorData;

public class UvRange {
    private String lowUvValue;
    private String highUvValue;

    public UvRange(String lowUvValue, String highUvValue) {
        this.lowUvValue = lowUvValue;
        this.highUvValue = highUvValue;
    }

    public String getLowUvValue() {
        return lowUvValue;
    }

    public void setLowUvValue(String lowUvValue) {
        this.lowUvValue = lowUvValue;
    }

    public String getHighUvValue() {
        return highUvValue;
    }

    public void setHighUvValue(String higUvValue) {
        this.highUvValue = highUvValue;
    }
}
