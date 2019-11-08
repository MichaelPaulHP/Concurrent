package com.example.mrrobot.concurrent.models;

import android.arch.lifecycle.MutableLiveData;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.BindingAdapter;
import android.location.Location;
import android.widget.ImageView;

import com.example.mrrobot.concurrent.Utils.RandomColors;
import com.example.mrrobot.concurrent.entityes.DestinationEntity;
import com.mapbox.mapboxsdk.geometry.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Destination extends BaseObservable {


    public static final String ADDRESS_SEPARATOR = "-";
    public static MutableLiveData<Destination> destinationSelected = new MutableLiveData<>();

    private MutableLiveData<Destination> mutableLiveData;


    private Location origin;
    private Location destination;
    private Chat chat;
    private DestinationEntity entity;
    private IListener listener;


    public Destination(Chat chat, String userId) {
        this.chat = chat;


        this.entity = new DestinationEntity();
        this.entity.userId = userId;
        this.entity.numUsers = 0;
        this.entity.color = new RandomColors().getColor();
    }

    public Destination(DestinationEntity entity) {

        this.entity = entity;

        this.chat = new Chat(entity.chatId);
        this.origin = createLocation(
                entity.originLatitude,
                entity.originLongitude);

        this.destination = createLocation(
                entity.destinationLatitude,
                entity.destinationLongitude);
    }

    private Location createLocation(Double latitude, Double longitude) {
        Location location = new Location("");
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        return location;
    }



    public static Destination findDestinationInListById(List<Destination> list, Destination x) {

        for (Destination destination : list) {
            if (destination.getId().equals(x.getId())) {
                return destination;
            }
        }
        return null;
    }



    public boolean isOwn() {

        if (this.entity.destinationId == null)
            return false;
        return User.getCurrentUser().isMyDestination(entity.destinationId);
    }

    public void initMutableLiveData() {
        this.mutableLiveData = new MutableLiveData<>();
    }


    /////////GETTERS AND SETTER
    public MutableLiveData<Destination> getObservable() {
        return this.mutableLiveData;
    }

    public Chat getChat() {
        return chat;
    }


    public String getId() {
        return entity.destinationId;
    }

    public void setId(String id) {
        this.entity.destinationId = id;
    }


    public void setListener(IListener iDestinationListener) {
        this.listener = iDestinationListener;
    }


    @Bindable
    public int getNumUsers() {
        return entity.numUsers;
    }

    public void setNumUsers(int numUsers) {
        entity.numUsers = numUsers;
        //notifyPropertyChanged(BR.numUsers);
    }


    public int getColor() {
        return entity.color;
    }

    public String getName() {
        return entity.name;
    }

    public void setName(String name) {
        entity.name = name;
        notifyChangeAttribute();
    }

    public void setOriginAddress(String name) {

        String newName;
        if (entity.name != null) {
            String[] split = this.entity.name.split(ADDRESS_SEPARATOR,2);
            newName = name + ADDRESS_SEPARATOR + split[1];
        } else {
            newName = name + ADDRESS_SEPARATOR;
        }
        setName(newName);
    }
    public String getOriginAddress(){
        if(this.getName()!=null) {
            String name = getName().split(ADDRESS_SEPARATOR,2)[0];
            return name.isEmpty()? null:name;
        }
        return null;
    }
    public String getDestinationAddress(){
        if(this.getName()!=null) {
            String name = getName().split(ADDRESS_SEPARATOR,2)[1];
            return name.isEmpty()? null:name;
        }
        return null;
    }
    public void setDestinationAddress(String name) {
        String newName;
        if (entity.name != null) {
            String[] split = this.entity.name.split(ADDRESS_SEPARATOR,2);
            newName = split[0] + ADDRESS_SEPARATOR + name;
        } else {
            newName = ADDRESS_SEPARATOR + name;
        }
        setName(newName);

    }

    public Location getOrigin() {
        return origin;
    }

    public LatLng getOriginLatLng() {
        return new LatLng(origin.getLatitude(), origin.getLongitude());
    }

    public void setOrigin(Location origin) {
        this.origin = origin;
        notifyChangeAttribute();
    }

    public void setOrigin(LatLng latLng) {
        Location location = new Location("X");
        location.setLongitude(latLng.getLongitude());
        location.setLatitude(latLng.getLatitude());
        this.origin = location;
        notifyChangeAttribute();
    }

    public Location getDestination() {
        return destination;
    }

    public LatLng getDestinationLatLng() {
        return new LatLng(destination.getLatitude(), destination.getLongitude());
    }

    public void setDestination(Location destination) {
        this.destination = destination;
        notifyChangeAttribute();
    }

    public void setDestination(LatLng latLng) {
        Location location = new Location("X");
        location.setLongitude(latLng.getLongitude());
        location.setLatitude(latLng.getLatitude());
        this.destination = location;
        notifyChangeAttribute();
    }

    public interface IListener {

        void onClick(Destination destination);
        //void onSelected(Destination destination);
        //void goToDestination(Destination destination);
    }

    private void notifyChangeAttribute() {
        if (this.mutableLiveData != null)
            this.mutableLiveData.postValue(this);
    }


    @BindingAdapter("android:tint")
    public static void setColorFilter(ImageView imageView, int color) {
        imageView.setColorFilter(color);
    }
}
