package com.example.igro.Models.ActuatorControl;

public class HeaterControlEvents {

    String eventId = null;
    String eventDateTime = null;
    Long eventUnixEpoch = null;
    Boolean eventOnOff = false;

    HeaterControlEvents(){

    }

    public HeaterControlEvents(String eventId, String eventDateTime, Long eventUnixEpoch, Boolean eventOnOff) {
        this.eventId = eventId;
        this.eventDateTime = eventDateTime;
        this.eventOnOff = eventOnOff;
        this.eventUnixEpoch = eventUnixEpoch;
    }


    public HeaterControlEvents(String eventDateTime, Long eventUnixEpoch, Boolean eventOnOff) {
        this.eventDateTime = eventDateTime;
        this.eventUnixEpoch = eventUnixEpoch;
        this.eventOnOff = eventOnOff;
    }

    public HeaterControlEvents(String eventDateTime, Boolean eventOnOff){
        this.eventDateTime = eventDateTime;
        this.eventOnOff = eventOnOff;
    }


    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventDateTime() {
        return eventDateTime;
    }

    public void setEventDateTime(String eventDateTime) {
        this.eventDateTime = eventDateTime;
    }

    public Long getEventUnixEpoch() {
        return eventUnixEpoch;
    }

    public void setEventUnixEpoch(Long eventUnixEpoch) {
        this.eventUnixEpoch = eventUnixEpoch;
    }

    public Boolean getEventOnOff() {
        return eventOnOff;
    }

    public void setEventOnOff(Boolean eventOnOff) {
        this.eventOnOff = eventOnOff;
    }
}
