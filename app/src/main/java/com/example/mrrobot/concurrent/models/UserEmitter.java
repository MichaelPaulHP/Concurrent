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

public class UserEmitter {

    public static void requestMyDestinations() {
        Socket socket = SocketIO.getSocket();
        User user = User.getCurrentUser();
        socket.on("myDestinations", onGetMyDestinations);//getMyDestinations

        socket.emit("getMyDestinations", Utils.toJsonObject("userId", user.getIdGoogle()));

    }


    private static Emitter.Listener onGetMyDestinations = new Emitter.Listener() {

        @Override
        public void call(Object... args) {

            try {
                User user = User.getCurrentUser();

                List<Destination> destinations = DestinationEntity.readDestinations(args);
                for (Destination destination : destinations) {
                    user.addMyDestination(destination);
                }
                //destination.setDestinationListener(HomeViewModel.this);
                UserEmitter.startListenerOnChangeLocation();
            } catch (Exception e) {
                Timber.e(e);
                throw e;
            }
        }
    };

    public static void startListenerJoinMyDestinations() {
        Socket socket = SocketIO.getSocket();
        socket.on("joinToDestination", onJoinToDestination);
    }


    private static Emitter.Listener onJoinToDestination = new Emitter.Listener() {

        @Override
        public void call(Object... args) {

            User user = User.getCurrentUser();
            List<Destination> destinations = DestinationEntity.readDestinations(args);
            for (Destination destination : destinations) {
                user.updateMyDestination(destination);
            }


        }
    };

    public static void emitChangeMyLocation() {
        try {

            Participant participant = (Participant) User.getCurrentUser();
            Gson gson = new Gson();
            String participantJson = gson.toJson(participant, Participant.class);

            SocketIO.getSocket().emit("changeLocation", new JSONObject(participantJson));
        } catch (JSONException e) {
            Timber.e(e);

        }
    }

    public static void startListenerOnChangeLocation(){
        Socket socket = SocketIO.getSocket();
        socket.on("changeLocation", onChangeLocation);
    }

    private static Emitter.Listener onChangeLocation = new Emitter.Listener() {

        @Override
        public void call(Object... args) {

            try {

                JSONObject json = (JSONObject) args[0];
                Gson gson = new Gson();
                Participant participant = gson.fromJson(json.toString(), Participant.class);

                // find
                String id = participant.getDestinationId();
                User userCurrent = User.getCurrentUser();
                Destination destination = userCurrent.findDestinationById(id);
                destination.setParticipant(participant);

            } catch (Exception e) {
                Timber.e(e);
                throw e;
            }
        }
    };

}
