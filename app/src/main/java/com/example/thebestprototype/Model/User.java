package com.example.thebestprototype.Model;

import java.io.Serializable;

public class User implements Serializable {

    private int id;
    private String fullname;
    private String email;
    private String password;
    private double latitude;
    private double longtitude;
    private int signalpower;
    private String NetworkOperatorName;
    private int NetworkOperatorCode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(double longtitude) {
        this.longtitude = longtitude;
    }

    public int getSignalpower() {
        return signalpower;
    }

    public void setSignalpower(int signalpower) {
        this.signalpower = signalpower;
    }

    public String getNetworkOperatorName() {
        return NetworkOperatorName;
    }

    public void setNetworkOperatorName(String networkOperatorName) {
        NetworkOperatorName = networkOperatorName;
    }

    public int getNetworkOperatorCode() {
        return NetworkOperatorCode;
    }

    public void setNetworkOperatorCode(int networkOperatorCode) {
        NetworkOperatorCode = networkOperatorCode;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", fullname='" + fullname + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", latitude=" + latitude +
                ", longtitude=" + longtitude +
                ", signalpower=" + signalpower +
                ", NetworkOperatorName='" + NetworkOperatorName + '\'' +
                ", NetworkOperatorCode=" + NetworkOperatorCode +
                '}';
    }
}
