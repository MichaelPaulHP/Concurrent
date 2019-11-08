package com.example.mrrobot.concurrent.models;


import com.example.mrrobot.concurrent.Services.SocketIO;
import com.example.mrrobot.concurrent.entityes.DestinationEntity;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;

public class DestinationData {


    public static void addParticipant(String destinationId, String userId) throws JSONException {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("destinationId", destinationId);
        jsonObject.put("userId", userId);

        Socket socket = SocketIO.getSocket();
        socket.emit("joinToDestination", jsonObject);

    }

    public static void findDestinations(Destination  destination) throws JSONException {
        Socket socket = SocketIO.getSocket();

        Gson destinationGson=new Gson();

        DestinationEntity destinationEntity= new DestinationEntity();
        destinationEntity.fill(destination);
        String destinationJSON=destinationGson.toJson(destinationEntity);
        socket.emit("findDestinations", destinationJSON);

    }

    public static void newDestination(Destination destination){

        Gson destinationGson=new Gson();

        DestinationEntity destinationEntity= new DestinationEntity();
        destinationEntity.fill(destination);
        String destinationJSON=destinationGson.toJson(destinationEntity);

        Socket socket = SocketIO.getSocket();
        socket.emit("newDestination", destinationJSON);
    }


}
