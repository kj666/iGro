package com.example.igro.Models.ActuatorControl;

public class HumidControlEvents {

    String humidEventId = null;
    String humidEventDateTime = null;
    Long humidEventUnixEpoch = null;
    Boolean humidEventOnOff = false;

    HumidControlEvents(){

    }

    public HumidControlEvents(String humidEventId, String humidEventDateTime, Long humidEventUnixEpoch, Boolean humidEventOnOff) {
        this.humidEventId = humidEventId;
        this.humidEventDateTime = humidEventDateTime;
        this.humidEventUnixEpoch = humidEventUnixEpoch;
        this.humidEventOnOff = humidEventOnOff;
    }

    public HumidControlEvents(String humidEventDateTime, Long humidEventUnixEpoch, Boolean humidEventOnOff) {
        this.humidEventDateTime = humidEventDateTime;
        this.humidEventUnixEpoch = humidEventUnixEpoch;
        this.humidEventOnOff = humidEventOnOff;
    }


    public String getHumidEventId() {
        return humidEventId;
    }

    public void setHumidEventId(String humidEventId) {
        this.humidEventId = humidEventId;
    }

    public String getHumidEventDateTime() {
        return humidEventDateTime;
    }

    public void setHumidEventDateTime(String humidEventDateTime) {
        this.humidEventDateTime = humidEventDateTime;
    }

    public Long getHumidEventUnixEpoch() {
        return humidEventUnixEpoch;
    }

    public void setHumidEventUnixEpoch(Long humidEventUnixEpoch) {
        this.humidEventUnixEpoch = humidEventUnixEpoch;
    }

    public Boolean getHumidEventOnOff() {
        return humidEventOnOff;
    }

    public void setHumidEventOnOff(Boolean humidEventOnOff) {
        this.humidEventOnOff = humidEventOnOff;
    }
}
