package com.example.igro.Models.SensorData;

public class Range {

    private String lowTempValue;
    private String highTempValue;

    public Range(){

    }


    public Range(String lowTempValue, String highTempValue){

        this.lowTempValue=lowTempValue;
        this.highTempValue=highTempValue;

    }



    public String getLowTempValue() {
        return lowTempValue;
    }

    public String getHighTempValue() {
        return highTempValue;
    }



    public void setLowTempValue(String lowTempValue) {
        this.lowTempValue = lowTempValue;
    }

    public void setHighTempValue(String highTempValue) {
        this.highTempValue = highTempValue; }

}
