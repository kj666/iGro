package com.example.igro.Models.SensorData.Range;

public class HumidityRange {
    private String lowHumidityValue;
    private String highHumidityValue;

    public HumidityRange(String lowHumidityValue, String highHumidityValue) {
        this.lowHumidityValue = lowHumidityValue;
        this.highHumidityValue = highHumidityValue;
    }

    public String getLowHumidityValue() {
        return lowHumidityValue;
    }

    public void setLowHumidityValue(String lowHumidityValue) {
        this.lowHumidityValue = lowHumidityValue;
    }

    public String getHighHumidityValue() {
        return highHumidityValue;
    }

    public void setHighHumidityValue(String highHumidityValue) {
        this.highHumidityValue = highHumidityValue;
    }
}
