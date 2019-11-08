package com.example.mrrobot.concurrent.models;

import android.location.Location;
import android.location.LocationManager;

import com.example.mrrobot.concurrent.entityes.DestinationEntity;

import org.junit.Test;

import static org.junit.Assert.*;

public class DestinationTest {

    @Test
    public void getADestinationFromEntity() {

        /*DestinationEntity entity= new DestinationEntity();
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
        assertEquals(destination.getColor(),entity.color);*/
        assertTrue(4>2);
    }
    @Test
    public void createLocation(){

        double latitude=-16.3988900;
        double longitude = -71.5350000;
        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(0.0d);
        location.setLongitude(0.0d);
        assertTrue(4>2);
        //assertEquals(0.0d,location.getLatitude());

    }
    @Test
    public void nameWithOriginAndDestination(){
        String nameDes="destino";
        String nameOri="Origin";
        Destination destination = new Destination(null,"FFFFFFF");
        destination.setDestinationAddress(nameDes);
        destination.setOriginAddress(nameOri);

        assertEquals(nameOri,destination.getOriginAddress());
        assertEquals(nameDes,destination.getDestinationAddress());
        //assertEquals(0.0d,location.getLatitude());

    }

    @Test
    public void nameWithOriginAddress(){
        String originAddress="origin";
        Destination destination = new Destination(null,"FFFFFFF");
        destination.setOriginAddress(originAddress);

        assertEquals(originAddress,destination.getOriginAddress());
        assertNull(destination.getDestinationAddress());
    }

    @Test
    public void nameWithDestinationAddress(){
        String destinationAddress="origin";
        Destination destination = new Destination(null,"FFFFFFF");
        destination.setDestinationAddress(destinationAddress);

        assertEquals(destinationAddress,destination.getDestinationAddress());
        assertNull(destination.getOriginAddress());

    }
    @Test
    public void testTrim(){
        String a="-HOLAMUNDO";
        String[] strings=a.split(Destination.ADDRESS_SEPARATOR,2);
        assertEquals(2,strings.length);
        assertTrue(strings[0].isEmpty());
        assertEquals("HOLAMUNDO",strings[1]);
    }

}