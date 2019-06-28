package com.example.mrrobot.concurrent.Services;

import android.util.Log;

import com.example.mrrobot.concurrent.models.Destination;
import com.example.mrrobot.concurrent.models.Localization;
import com.example.mrrobot.concurrent.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class SocketIO {
    private static Socket mSocket;
    private static final String CHAT_SERVER_URL= com.example.mrrobot.concurrent.Config.SocketIO.URL;

    public static Socket getSocket() {
        if (mSocket==null){
            try {
                mSocket = IO.socket(CHAT_SERVER_URL);

                if(!mSocket.connected()){
                    mSocket.connect();

                }
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
        return mSocket;
    }
    public static void emitFindDestinations(Localization localization){
        Socket socket = getSocket();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", localization.getName());
            jsonObject.put("latitude", localization.getLatitude()+"");
            jsonObject.put("longitude", localization.getLongitude()+"");
            jsonObject.put("userID", User.getCurrentUser().getIdGoogle());

            socket.emit("findDestinations", jsonObject);
        } catch (JSONException e) {
            Log.d("JSONException", e.getMessage());
        }

    }
    public static void emitMyLocalizationChange(Localization localization){
        Socket socket = getSocket();
        JSONObject jsonObject = new JSONObject();
        try {
            // String name;
            // double latitude;
            // double longitude;
            // var data={"id":$socket.id,"publicKey":$myKeyPublic}
            jsonObject.put("name", localization.getName());
            jsonObject.put("latitude", localization.getLatitude()+"");
            jsonObject.put("longitude", localization.getLongitude()+"");
            jsonObject.put("userID", User.getCurrentUser().getIdGoogle());
            socket.emit("myLocalizationChange", jsonObject);
        } catch (JSONException e) {
            Log.d("JSONException", e.getMessage());
        }
    }

    public static void emitNewTempDestination(Destination destination){
        Socket socket = getSocket();
        JSONObject jsonObject = new JSONObject();
        try {
            // int numUsers;
            // Localization localization;
            // int color;
            // String name;
            Localization localization = destination.getLocalization();
            jsonObject.put("numUsers", destination.getNumUsers()+"");
            jsonObject.put("name", destination.getName());
            jsonObject.put("color", destination.getColor()+"");
            jsonObject.put("latitude", localization.getLatitude()+"");
            jsonObject.put("longitude", localization.getLongitude()+"");
            jsonObject.put("userID", User.getCurrentUser().getIdGoogle());
            socket.emit("newTempDestination", jsonObject);
        } catch (JSONException e) {
            Log.d("JSONException", e.getMessage());
        }

    }

    public static void emitNewDestination(Destination destination){
        Socket socket = getSocket();
        JSONObject jsonObject = new JSONObject();
        try {
            // int numUsers;
            // Localization localization;
            // int color;
            // String name;
            Localization localization = destination.getLocalization();
            jsonObject.put("numUsers", destination.getNumUsers()+"");
            jsonObject.put("name", destination.getName());
            jsonObject.put("color", destination.getColor()+"");
            jsonObject.put("latitude", localization.getLongitude()+"");
            jsonObject.put("longitude", localization.getLongitude()+"");
            jsonObject.put("userID", User.getCurrentUser().getIdGoogle());
            socket.emit("newDestination", jsonObject);
        } catch (JSONException e) {
            Log.d("JSONException", e.getMessage());
        }

    }
    public static void emitJoinToDestination(Destination destination){
        Socket socket = getSocket();
        JSONObject jsonObject = new JSONObject();
        try {
            // int numUsers;
            // Localization localization;
            // int color;
            // String name;

            jsonObject.put("idDestination", destination.getId());
            jsonObject.put("userID", User.getCurrentUser().getIdGoogle());
            socket.emit("joinToDestination", jsonObject);
        } catch (JSONException e) {
            Log.d("JSONException", e.getMessage());
        }

    }
    public static void saveThisUser(String userID){
        //socket.emit("testSaveUser",{userID:userID});
        Socket socket = getSocket();
        JSONObject jsonObject = new JSONObject();
        try {
            // int numUsers;
            // Localization localization;
            // int color;
            // String name;

            jsonObject.put("userID", userID);
            socket.emit("testSaveUser", jsonObject);
        } catch (JSONException e) {
            Log.d("JSONException", e.getMessage());
        }
    }
}
