package com.example.mrrobot.concurrent.entityes;

import android.location.Location;

import com.example.mrrobot.concurrent.models.Destination;
import com.example.mrrobot.concurrent.models.User;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class DestinationEntity {
    public String destinationId;
    public String name;
    public int numUsers;
    public int color;
    public Double originLatitude;
    public Double originLongitude;
    public Double destinationLatitude;
    public Double destinationLongitude;
    public String userId;
    public String chatId;
    //public String originAddress;
    //public String destinationAddress;
    //toDestination(){ }
    public void fill(Destination destination) {
        this.destinationId = destination.getId();
        this.name = destination.getName();
        this.numUsers = destination.getNumUsers();
        this.color = destination.getColor();
        this.originLatitude = destination.getOrigin().getLatitude();
        this.originLongitude = destination.getOrigin().getLongitude();
        this.destinationLatitude = destination.getDestination().getLatitude();
        this.destinationLongitude = destination.getDestination().getLongitude();
        this.userId = User.getCurrentUser().getId();
        chatId=destination.getChat().getKey();
    }

    public static List<Destination> readDestinations(Object... args) {
        List<Destination> destinations = new ArrayList<>();
        if (args != null) {

            for (Object arg : args) {

                DestinationEntity entity = fromObject(arg);
                destinations.add(new Destination(entity));

            }

        }
        return destinations;
    }

    private  static DestinationEntity fromObject(Object object) {
        String json = (String) object;
        Gson gson = new Gson();
        return gson.fromJson(json, DestinationEntity.class);
    }

}
