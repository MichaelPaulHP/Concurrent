package com.example.mrrobot.concurrent.models;

import android.support.annotation.Nullable;

import com.google.gson.JsonObject;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Geometry;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.HashMap;

public class Participant {

    protected String googleId;
    protected String userName;

    private String destinationId;

    private double latitude;
    private double longitude;
    private double altitude;
    private Feature feature;

    public Feature getFeature(){
        if(this.feature==null)
            buildFeature();
        return feature;
    }

    public void set(Participant participant)
    {
        this.latitude=participant.latitude;
        this.latitude=participant.longitude;
        this.altitude=participant.altitude;
        buildFeature();
    }

    private void buildFeature(){
        Geometry geometry= Point.fromLngLat(this.longitude,this.latitude);
        String id=this.googleId;
        JsonObject jsonObject= new JsonObject();
        jsonObject.addProperty("googleId",id);
        jsonObject.addProperty("userName",this.userName);
        this.feature = Feature.fromGeometry(geometry, jsonObject, id);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(this==obj)
            return true;
        if(obj instanceof Participant){
            Participant participant=(Participant)obj;
            return participant.googleId.equals(this.googleId);
        }
        return false;
    }


    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDestinationId() {
        return destinationId;
    }

    public void setDestinationId(String destinationId) {
        this.destinationId = destinationId;
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

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }
}
