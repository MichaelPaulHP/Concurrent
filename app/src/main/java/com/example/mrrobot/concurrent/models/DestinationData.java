package com.example.mrrobot.concurrent.models;


import com.example.mrrobot.concurrent.Services.SocketIO;
import com.example.mrrobot.concurrent.Utils.Utils;
import com.example.mrrobot.concurrent.entityes.DestinationEntity;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import timber.log.Timber;

public class DestinationData {


    public static void addParticipant(String destinationId, String userId) throws JSONException {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("destinationId", destinationId);
        jsonObject.put("userId", userId);

        Socket socket = SocketIO.getSocket();
        socket.emit("joinToDestination", jsonObject);

    }

    public static void findDestinations(Destination destination) throws JSONException {
        Socket socket = SocketIO.getSocket();

        Gson destinationGson = new Gson();

        DestinationEntity destinationEntity = new DestinationEntity();
        destinationEntity.fill(destination);
        String destinationJSON = destinationGson.toJson(destinationEntity);
        socket.emit("findDestinations", new JSONObject(destinationJSON));

    }

    public static void newDestination(Destination destination) {

        Gson destinationGson = new Gson();


        DestinationEntity destinationEntity = new DestinationEntity();
        destinationEntity.fill(destination);
        String destinationJSON = destinationGson.toJson(destinationEntity, DestinationEntity.class);

        Socket socket = SocketIO.getSocket();
        try {
            socket.emit("newDestination", new JSONObject(destinationJSON));
        } catch (JSONException e) {
            Timber.e(e);
        }
    }

    public static void requestParticipants(String destinationId) throws JSONException {
        Socket socket = SocketIO.getSocket();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("destinationId", destinationId);

        socket.emit("getParticipants",jsonObject);
    }






}
