package com.example.igro.Models.ActuatorControl;

public class ApplianceControlEvents {

    String eventId = null;
    String eventDateTime = null;
    Long eventUnixEpoch = null;
    Long previousEventUnixEpoch;
    String eventUserID = null;
    String eventUserName = null;
    String eventUserEmail = null;
    Boolean eventOnOff = false;

    ApplianceControlEvents(){

    }

    public ApplianceControlEvents(String eventId, String eventDateTime, Long eventUnixEpoch, Long previousEventUnixEpoch, String eventUserID, String eventUserName, String eventUserEmail, Boolean eventOnOff) {
        this.eventId = eventId;
        this.eventDateTime = eventDateTime;
        this.eventUnixEpoch = eventUnixEpoch;
        this.previousEventUnixEpoch = previousEventUnixEpoch;
        this.eventUserID = eventUserID;
        this.eventUserName = eventUserName;
        this.eventUserEmail = eventUserEmail;
        this.eventOnOff = eventOnOff;
    }

    public ApplianceControlEvents(String eventId, String eventDateTime, String eventUserID, String eventUserName, String eventUserEmail, Boolean eventOnOff) {
        this.eventId = eventId;
        this.eventDateTime = eventDateTime;
        this.eventUserID = eventUserID;
        this.eventUserName = eventUserName;
        this.eventUserEmail = eventUserEmail;
        this.eventOnOff = eventOnOff;
    }

    public ApplianceControlEvents(String eventId, String eventDateTime, String eventUserID, String eventUserName, String eventUserEmail) {
        this.eventId = eventId;
        this.eventDateTime = eventDateTime;
        this.eventUserID = eventUserID;
        this.eventUserName = eventUserName;
        this.eventUserEmail = eventUserEmail;
    }

    public ApplianceControlEvents(String eventDateTime, Long eventUnixEpoch, Long previousEventUnixEpoch, String eventUserID, String eventUserName, Boolean eventOnOff) {
        this.eventDateTime = eventDateTime;
        this.eventUnixEpoch = eventUnixEpoch;
        this.previousEventUnixEpoch = previousEventUnixEpoch;
        this.eventUserID = eventUserID;
        this.eventUserName = eventUserName;
        this.eventOnOff = eventOnOff;
    }

    public ApplianceControlEvents( String eventDateTime, Long eventUnixEpoch, Long previousEventUnixEpoch, String eventUserName, Boolean eventOnOff) {
        this.eventDateTime = eventDateTime;
        this.eventUnixEpoch = eventUnixEpoch;
        this.previousEventUnixEpoch = previousEventUnixEpoch;
        this.eventUserName = eventUserName;
        this.eventOnOff = eventOnOff;
    }

    public ApplianceControlEvents(String eventId, String eventDateTime, Long eventUnixEpoch, Boolean eventOnOff) {
        this.eventId = eventId;
        this.eventDateTime = eventDateTime;
        this.eventOnOff = eventOnOff;
        this.eventUnixEpoch = eventUnixEpoch;
    }


    public ApplianceControlEvents(String eventDateTime, Long eventUnixEpoch, Boolean eventOnOff) {
        this.eventDateTime = eventDateTime;
        this.eventUnixEpoch = eventUnixEpoch;
        this.eventOnOff = eventOnOff;
    }

    public ApplianceControlEvents(String eventDateTime, Boolean eventOnOff){
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

    public Long getPreviousEventUnixEpoch() {
        return previousEventUnixEpoch;
    }

    public void setPreviousEventUnixEpoch(Long previousEventUnixEpoch) {
        this.previousEventUnixEpoch = previousEventUnixEpoch;
    }

    public String getEventUserID() {
        return eventUserID;
    }

    public void setEventUserID(String eventUserID) {
        this.eventUserID = eventUserID;
    }

    public String getEventUserName() {
        return eventUserName;
    }

    public void setEventUserName(String eventUserName) {
        this.eventUserName = eventUserName;
    }

    public String getEventUserEmail() {
        return eventUserEmail;
    }

    public void setEventUserEmail(String eventUserEmail) {
        this.eventUserEmail = eventUserEmail;
    }

    public Boolean getEventOnOff() {
        return eventOnOff;
    }

    public void setEventOnOff(Boolean eventOnOff) {
        this.eventOnOff = eventOnOff;
    }
}
