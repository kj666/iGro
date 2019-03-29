package com.example.igro.Models.ActuatorControl;

public class ApplianceControlEvents {

    String eventId = null;
    String eventDateTime = null;
    Long eventUnixEpoch = null;
    String userIdWhoTriggeredEvent = null;
    String userEmailWhoTriggeredEvent = null;
    Boolean eventOnOff = false;

    ApplianceControlEvents(){

    }

    public ApplianceControlEvents(String eventId, String eventDateTime, Long eventUnixEpoch, String userIdWhoTriggeredEvent, String userEmailWhoTriggeredEvent, Boolean eventOnOff) {
        this.eventId = eventId;
        this.eventDateTime = eventDateTime;
        this.eventOnOff = eventOnOff;
        this.userIdWhoTriggeredEvent = userIdWhoTriggeredEvent;
        this.userEmailWhoTriggeredEvent = userEmailWhoTriggeredEvent;
        this.eventUnixEpoch = eventUnixEpoch;
    }


    public ApplianceControlEvents(String eventDateTime, Long eventUnixEpoch, String userIdWhoTriggeredEvent, String userEmailWhoTriggeredEvent, Boolean eventOnOff) {
        this.eventDateTime = eventDateTime;
        this.eventUnixEpoch = eventUnixEpoch;
        this.userIdWhoTriggeredEvent = userIdWhoTriggeredEvent;
        this.userEmailWhoTriggeredEvent = userEmailWhoTriggeredEvent;
        this.eventOnOff = eventOnOff;
    }

    public ApplianceControlEvents(String eventDateTime, Long eventUnixEpoch, String userIdWhoTriggeredEvent, Boolean eventOnOff) {
        this.eventDateTime = eventDateTime;
        this.eventUnixEpoch = eventUnixEpoch;
        this.userIdWhoTriggeredEvent = userIdWhoTriggeredEvent;
        this.eventOnOff = eventOnOff;
    }

    public ApplianceControlEvents(String eventDateTime, String userEmailWhoTriggeredEvent, Long eventUnixEpoch, Boolean eventOnOff) {
        this.eventDateTime = eventDateTime;
        this.userEmailWhoTriggeredEvent = userEmailWhoTriggeredEvent;
        this.eventUnixEpoch = eventUnixEpoch;
        this.eventOnOff = eventOnOff;
    }

    public ApplianceControlEvents(String eventDateTime, String userIdWhoTriggeredEvent, String userEmailWhoTriggeredEvent, Boolean eventOnOff){
        this.eventDateTime = eventDateTime;
        this.userIdWhoTriggeredEvent = userIdWhoTriggeredEvent;
        this.userEmailWhoTriggeredEvent = userEmailWhoTriggeredEvent;
        this.eventOnOff = eventOnOff;
    }



    public ApplianceControlEvents(String eventDateTime, String userEmailWhoTriggeredEvent, Boolean eventOnOff){
        this.eventDateTime = eventDateTime;
        this.userEmailWhoTriggeredEvent = userEmailWhoTriggeredEvent;
        this.eventOnOff = eventOnOff;
    }

    public ApplianceControlEvents( Long eventUnixEpoch, String userId, Boolean eventOnOff) {

        this.eventUnixEpoch = eventUnixEpoch;
        this.userIdWhoTriggeredEvent = userId;
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

    public String getUserIdWhoTriggeredEvent() {
        return userIdWhoTriggeredEvent;
    }

    public void setUserIdWhoTriggeredEvent(String userIdWhoTriggeredEvent) {
        this.userIdWhoTriggeredEvent = userIdWhoTriggeredEvent;
    }

    public String getUserEmailWhoTriggeredEvent() {
        return userEmailWhoTriggeredEvent;
    }

    public void setUserEmailWhoTriggeredEvent(String userEmailWhoTriggeredEvent) {
        this.userEmailWhoTriggeredEvent = userEmailWhoTriggeredEvent;
    }

    public Boolean getEventOnOff() {
        return eventOnOff;
    }

    public void setEventOnOff(Boolean eventOnOff) {
        this.eventOnOff = eventOnOff;
    }
}
