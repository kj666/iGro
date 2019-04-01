package com.example.igro.Models.SensorData.Range;

public class TempRange {

    private String lowTempValue;
    private String highTempValue;

    public TempRange(){

    }


    public TempRange(String lowTempValue, String highTempValue){

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
