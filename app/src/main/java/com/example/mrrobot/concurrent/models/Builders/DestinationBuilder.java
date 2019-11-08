package com.example.mrrobot.concurrent.models.Builders;

import com.example.mrrobot.concurrent.models.Chat;
import com.example.mrrobot.concurrent.models.Destination;
import com.mapbox.mapboxsdk.geometry.LatLng;

public class DestinationBuilder {

    private String id;
    private int numUsers;
    private LatLng origin;
    private LatLng destination;
    private int color;
    private String name;
    private Chat chat;

    public DestinationBuilder setOrigin(LatLng origin){
        this.origin=origin;
        return this;
    }
    public DestinationBuilder setDestination(LatLng destination){
        this.destination=destination;
        return this;
    }
    public void build(Chat chat){
        this.numUsers=0;

    }

}
