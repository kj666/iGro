package com.example.igro.Models.SensorData;

public class TemperatureRange {
    private String tempId;
    private String lowTempValue;
    private String highTempValue;


    public TemperatureRange(String tempId,String lowTempValue, String highTempValue){

        this.lowTempValue=lowTempValue;
        this.highTempValue=highTempValue;
        this.tempId=tempId;


    }

    public String getTempId() {
        return tempId;
    }

    public String getLowTempValue() {
        return lowTempValue;
    }

    public String getHighTempValue() {
        return highTempValue;
    }

    public void setTempId(String tempId) {
        this.tempId = tempId;
    }

    public void setLowTempValue(String lowTempValue) {
        this.lowTempValue = lowTempValue;
    }

    public void setHighTempValue(String highTempValue) {
        this.highTempValue = highTempValue; }

}
