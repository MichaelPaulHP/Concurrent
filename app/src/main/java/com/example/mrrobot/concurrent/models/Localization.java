package com.example.mrrobot.concurrent.models;

import com.mapbox.mapboxsdk.geometry.LatLng;

public class Localization {
    private String name;
    private double latitude;
    private double longitude;

    public Localization(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Localization() {

    }
    public LatLng toLatLng(){
        return new LatLng(this.latitude,longitude);
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
