package com.example.igro.Models;

public class Users {

    private String email;
    private String ID;
    private String name;
    private String userRole;
    private String greenhouseID;

    public Users() {
    }

    public Users(String email, String ID, String name, String userRole, String greenhouseID) {
        this.email = email;
        this.ID = ID;
        this.name = name;
        this.userRole = userRole;
        this.greenhouseID = greenhouseID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getGreenhouseID() {
        return greenhouseID;
    }

    public void setGreenhouseID(String greenhouseID) {
        this.greenhouseID = greenhouseID;
    }
}
