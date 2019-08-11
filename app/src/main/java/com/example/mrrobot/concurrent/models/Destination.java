package com.example.mrrobot.concurrent.models;

import android.arch.lifecycle.MutableLiveData;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.BindingAdapter;
import android.util.Log;
import android.widget.ImageView;

import com.example.mrrobot.concurrent.BR;
import com.example.mrrobot.concurrent.Firebase.DB.ChatData;
import com.example.mrrobot.concurrent.Services.SocketIO;
import com.example.mrrobot.concurrent.Utils.RandomColors;
import com.example.mrrobot.concurrent.Utils.Utils;
import com.mapbox.mapboxsdk.geometry.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.socket.client.Socket;

public class Destination extends BaseObservable {

    public static MutableLiveData<Destination> destinationSelected = new MutableLiveData<>();

    private String id;
    private int numUsers;
    private LatLng localization;
    private int color;
    private String name;
    private Chat chat;

    private IDestinationListener destinationListener;

    public Destination() {
        this.color = new RandomColors().getColor();
    }


    public static void emitJoinToDestination(String idDestination) {
        String idUser= User.getCurrentUser().getIdGoogle();
        Socket socket = SocketIO.getSocket();
        socket.emit("joinToDestination", Utils.toJsonObject("idDestination", idDestination, "userID", idUser));
    }

    public static void emitFindDestinations(Destination origin,Destination destination) {
        Socket socket = SocketIO.getSocket();
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("originLatitude", origin.getLocalization().getLatitude() + "");
            jsonObject.put("originLongitude", origin.getLocalization().getLongitude() + "");
            jsonObject.put("name", destination.getName());
            jsonObject.put("latitude", destination.getLocalization().getLatitude() + "");
            jsonObject.put("longitude", destination.getLocalization().getLongitude() + "");
            jsonObject.put("userID", User.getCurrentUser().getIdGoogle());

            socket.emit("findDestinations", jsonObject);
        } catch (JSONException e) {
            Log.d("JSONException", e.getMessage());
        }

    }

    public static void emitNewDestination(Destination destination){
        String idChat= ChatData.getAnId();
        User current = User.getCurrentUser();
        Chat chat = new Chat(destination.getName(),current.getIdGoogle());
        chat.setKey(idChat);
        current.saveChatAndJoint(chat);
        Destination.emitNewDestination(destination,idChat);
    }

    private static void emitNewDestination(Destination destination,String idChat) {
        Socket socket = SocketIO.getSocket();
        JSONObject jsonObject = new JSONObject();
        try {
            // int numUsers;
            // Localization localization;
            // int color;
            // String name;
            LatLng localization = destination.getLocalization();
            jsonObject.put("numUsers", destination.getNumUsers() + "");
            jsonObject.put("name", destination.getName());
            jsonObject.put("color", destination.getColor() + "");
            jsonObject.put("latitude", localization.getLongitude() + "");
            jsonObject.put("longitude", localization.getLongitude() + "");
            jsonObject.put("userID", User.getCurrentUser().getIdGoogle());
            jsonObject.put("idChat",idChat);

            socket.emit("newDestination", jsonObject);
        } catch (JSONException e) {
            Log.d("JSONException", e.getMessage());
        }

    }


    public static List<Destination> destinationsToList(Object... args) {
        List<Destination> destinations = new ArrayList<>();
        if (args != null) {
                try{
                    for (int i = 0; i < args.length; i++) {
                        JSONObject data = (JSONObject) args[0];
                        Destination destination = get(data);
                        destinations.add(destination);
                    }
                }catch (Exception e){

                }
        }
        return destinations;
    }
    public static Destination findDestinationInListById(List<Destination> list,Destination x){

        for (Destination destination:  list) {
            if(destination.getId().equals(x.getId())){
                return destination;
            }
        }
        return null;
    }
    public static Destination get(JSONObject data) throws Exception {
        Destination destination = null;
            String name;
        try {
            name = data.getString("name");
            String id = data.getString("idDestination");
            int numUsers = Integer.parseInt(data.getString("numUsers"));
            String latitudeStr = data.getString("latitude");

            double latitude = Double.parseDouble(latitudeStr);
            double longitude = Double.parseDouble(data.getString("longitude"));
            destination = new Destination();
            destination.setName(name);
            destination.setId(id);
            destination.setNumUsers(numUsers);
            destination.setLocalization(new LatLng( latitude, longitude));
        } catch (JSONException e) {
            throw new Exception("this isn't a destination");
        }


        return destination;
    }
    public boolean isOwn(){
        if(this.id==null)
            return false;
        return User.getCurrentUser().isMyDestination(this.id);
    };

    /////////GETTERS AND SETTER


    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public IDestinationListener getDestinationListener() {
        return destinationListener;
    }

    public void setDestinationListener(IDestinationListener iDestinationListener) {
        this.destinationListener = iDestinationListener;
    }

   @Bindable
    public int getNumUsers() {
        return numUsers;
    }

    public void setNumUsers(int numUsers) {
        this.numUsers = numUsers;
        notifyPropertyChanged(BR.numUsers);
    }

    public LatLng getLocalization() {
        return localization;
    }

    public void setLocalization(LatLng localization) {
        this.localization = localization;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public interface IDestinationListener {

        void onClick(Destination destination);
        //void onSelected(Destination destination);
        //void goToDestination(Destination destination);


    }

    @BindingAdapter("android:tint")
    public static void setColorFilter(ImageView imageView, int color) {
        imageView.setColorFilter(color);
    }
}
