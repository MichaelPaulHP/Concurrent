package com.example.mrrobot.concurrent.models;

import android.location.Location;

import com.example.mrrobot.concurrent.entityes.DestinationEntity;

import org.junit.Test;

import static org.junit.Assert.*;

public class DestinationTest {

    @Test
    public void getADestinationFromEntity() {

        DestinationEntity entity= new DestinationEntity();
        entity.destinationId = "RRRRRRRRRRR";
        entity.name ="nameeeeeeee";
        entity.numUsers = 5;
        entity.color = 984565132;
        entity.originLatitude = 164325416.0;
        entity.originLongitude = 65494984.0;
        entity.destinationLatitude = 9798498.0;
        entity.destinationLongitude = 56465.0;
        entity.userId = "FFFFFFFF";
        entity.chatId="CCCCCC";

        Destination destination = new Destination(entity);

        assertEquals(destination.getName(),entity.name);
        assertEquals(destination.getNumUsers(),entity.numUsers);
        assertEquals(destination.getColor(),entity.color);
    }
    @Test
    public void createLocation(){

        double latitude=4353453d;
        double longitude = 332d;
        Location location = new Location("X");
        location.setLatitude(latitude);
        location.setLongitude(longitude);

        assertEquals(latitude,location.getLatitude());

    }

}