package com.example.igro.Models.SensorData;

public class UV {
    private String lowUvValue;
    private String higUvValue;

    public UV(String lowUvValue, String higUvValue) {
        this.lowUvValue = lowUvValue;
        this.higUvValue = higUvValue;
    }

    public String getLowUvValue() {
        return lowUvValue;
    }

    public void setLowUvValue(String lowUvValue) {
        this.lowUvValue = lowUvValue;
    }

    public String getHigUvValue() {
        return higUvValue;
    }

    public void setHigUvValue(String higUvValue) {
        this.higUvValue = higUvValue;
    }
}
