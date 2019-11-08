package com.example.mrrobot.concurrent.Services;

import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import android.util.Log;

import com.example.mrrobot.concurrent.models.Destination;
import com.example.mrrobot.concurrent.models.Localization;
import com.example.mrrobot.concurrent.models.User;
import com.mapbox.mapboxsdk.geometry.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class SocketIO {
    private static Socket mSocket;
    private static final String CHAT_SERVER_URL= com.example.mrrobot.concurrent.Config.SocketIO.URL;
    public static MutableLiveData<Boolean> isConnected = new MutableLiveData<>();
    public static Socket getSocket() {
        if (mSocket==null){
            try {
                mSocket = IO.socket(CHAT_SERVER_URL);
                //ConnectVerifyTask connectVerifyTask= new ConnectVerifyTask();
                //connectVerifyTask.execute(true);
                if(!mSocket.connected()){
                    mSocket.connect();

                }
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
        return mSocket;
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
    private static class ConnectVerifyTask extends AsyncTask<Boolean,Object,Boolean>{

        @Override
        protected Boolean doInBackground(Boolean... booleans) {

            Socket socket=getSocket();
            Boolean isConnected=socket.connected();
            SocketIO.isConnected.postValue(isConnected);
            Boolean temp=false;
            while(true){
                isConnected=socket.connected();
                if(!isConnected.equals(temp)){
                    SocketIO.isConnected.postValue(isConnected);
                    temp=isConnected;
                }

            }
        }
    }
}
