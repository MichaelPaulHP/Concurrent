package com.example.mrrobot.concurrent.entityes;

import com.example.mrrobot.concurrent.models.Destination;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class DestinationsTest {

    @Test
    public void fill() {
    }

    @Test
    public void readDestinations() {
    }

    @Test
    public void fromObject() {
        int color=234243;
        String json="{destinationId:234234, name:place, numUsers:0, color:"+color+", originLatitude:333333333,"+
                "originLongitude:3222222222, " +
                "destinationLatitude:8888, " +
                "destinationLongitude:999999, " +
                "userId:2051242311}";
        List<Destination> destinations = DestinationEntity.readDestinations(json);
        Destination destination=destinations.get(0);
        assertEquals(destination.getColor(),color);
    }
}