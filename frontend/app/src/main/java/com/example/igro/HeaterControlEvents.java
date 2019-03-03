package com.example.igro;

public class HeaterControlEvents {

    String heaterEventId = null;
    String heaterEventDateTime = null;
    Boolean heaterEventOnOff = false;

    HeaterControlEvents(){

    }

    public HeaterControlEvents(String heaterEventId, String heaterEventDateTime, Boolean heaterEventOnOff) {
        this.heaterEventId = heaterEventId;
        this.heaterEventDateTime = heaterEventDateTime;
        this.heaterEventOnOff = heaterEventOnOff;
    }


    public HeaterControlEvents(String heaterEventDateTime, Boolean heaterEventOnOff) {
        this.heaterEventDateTime = heaterEventDateTime;
        this.heaterEventOnOff = heaterEventOnOff;
    }

    public String getHeaterEventId() {
        return heaterEventId;
    }

    public void setHeaterEventId(String heaterEventId) {
        this.heaterEventId = heaterEventId;
    }

    public String getHeaterEventDateTime() {
        return heaterEventDateTime;
    }

    public void setHeaterEventDateTime(String heaterEventDateTime) {
        this.heaterEventDateTime = heaterEventDateTime;
    }

    public Boolean getHeaterEventOnOff() {
        return heaterEventOnOff;
    }

    public void setHeaterEventOnOff(Boolean heaterEventOnOff) {
        this.heaterEventOnOff = heaterEventOnOff;
    }
}
