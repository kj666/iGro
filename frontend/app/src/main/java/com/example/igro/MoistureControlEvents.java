package com.example.igro;

public class MoistureControlEvents {

    String moistEventId = null;
    String moistEventDateTime = null;
    Long moistEventUnixEpoch = null;
    Boolean moistEventOnOff = false;

    //Empty constructor
    public MoistureControlEvents(){

    }

    //Full constructor for moisture class
    public MoistureControlEvents(String moistEventId, String moistEventDateTime, Long moistEventUnixEpoch, Boolean moistEventOnOff) {
        this.moistEventId = moistEventId;
        this.moistEventDateTime = moistEventDateTime;
        this.moistEventUnixEpoch = moistEventUnixEpoch;
        this.moistEventOnOff = moistEventOnOff;
    }

    //just the date and on/off status constructor


    public MoistureControlEvents(String moistEventDateTime, Long moistEventUnixEpoch, Boolean moistEventOnOff) {
        this.moistEventDateTime = moistEventDateTime;
        this.moistEventUnixEpoch = moistEventUnixEpoch;
        this.moistEventOnOff = moistEventOnOff;
    }

    public String getMoistEventId() {
        return moistEventId;
    }

    public void setMoistEventId(String moistEventId) {
        this.moistEventId = moistEventId;
    }

    public String getMoistEventDateTime() {
        return moistEventDateTime;
    }

    public void setMoistEventDateTime(String moistEventDateTime) {
        this.moistEventDateTime = moistEventDateTime;
    }

    public Long getMoistEventUnixEpoch() {
        return moistEventUnixEpoch;
    }

    public void setMoistEventUnixEpoch(Long moistEventUnixEpoch) {
        this.moistEventUnixEpoch = moistEventUnixEpoch;
    }

    public Boolean getMoistEventOnOff() {
        return moistEventOnOff;
    }

    public void setMoistEventOnOff(Boolean moistEventOnOff) {
        this.moistEventOnOff = moistEventOnOff;
    }
}
