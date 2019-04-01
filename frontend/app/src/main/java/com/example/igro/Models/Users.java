package com.example.igro.Models;

public class Users {

    private String Email;
    private String ID;
    private String Name;
    private String UserRole;
    private String GreenhouseID;

    public Users() {
    }

    public Users(String email, String ID, String name, String userRole, String greenhouseID) {
        Email = email;
        this.ID = ID;
        Name = name;
        UserRole = userRole;
        GreenhouseID = greenhouseID;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getUserRole() {
        return UserRole;
    }

    public void setUserRole(String userRole) {
        UserRole = userRole;
    }

    public String getGreenhouseID() {
        return GreenhouseID;
    }

    public void setGreenhouseID(String greenhouseID) {
        GreenhouseID = greenhouseID;
    }
}
