package com.example.igro.Models.ActuatorControl;

public class UvControlEvents {

    String uvEventId = null;
    String uvEventDateTime = null;
    Long uvEventUnixEpoch = null;
    Boolean uvEventOnOff = false;

    //Empty constructor
    public UvControlEvents(){

    }

    // full UV Switch constructor
    public UvControlEvents(String uvEventId, String uvEventDateTime, Long uvEventUnixEpoch, Boolean uvEventOnOff) {
        this.uvEventId = uvEventId;
        this.uvEventDateTime = uvEventDateTime;
        this.uvEventUnixEpoch = uvEventUnixEpoch;
        this.uvEventOnOff = uvEventOnOff;
    }

    // just date and on/off uv switch constructor
    public UvControlEvents(String uvEventDateTime, Long uvEventUnixEpoch, Boolean uvEventOnOff) {
        this.uvEventDateTime = uvEventDateTime;
        this.uvEventUnixEpoch = uvEventUnixEpoch;
        this.uvEventOnOff = uvEventOnOff;
    }

    public String getUvEventId() {
        return uvEventId;
    }

    public void setUvEventId(String uvEventId) {
        this.uvEventId = uvEventId;
    }

    public String getUvEventDateTime() {
        return uvEventDateTime;
    }

    public void setUvEventDateTime(String uvEventDateTime) {
        this.uvEventDateTime = uvEventDateTime;
    }

    public Long getUvEventUnixEpoch() {
        return uvEventUnixEpoch;
    }

    public void setUvEventUnixEpoch(Long uvEventUnixEpoch) {
        this.uvEventUnixEpoch = uvEventUnixEpoch;
    }

    public Boolean getUvEventOnOff() {
        return uvEventOnOff;
    }

    public void setUvEventOnOff(Boolean uvEventOnOff) {
        this.uvEventOnOff = uvEventOnOff;
    }
}
