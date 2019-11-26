package com.example.mrrobot.concurrent.models;

import android.arch.lifecycle.MutableLiveData;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.BindingAdapter;
import android.location.Location;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.example.mrrobot.concurrent.BR;
import com.example.mrrobot.concurrent.Firebase.DB.ChatData;
import com.example.mrrobot.concurrent.Utils.RandomColors;
import com.example.mrrobot.concurrent.entityes.DestinationEntity;
import com.mapbox.geojson.Feature;
import com.mapbox.mapboxsdk.geometry.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Destination extends BaseObservable {


    public static final String ADDRESS_SEPARATOR = "-";


    private MutableLiveData<Destination> mutableLiveData;


    private Location origin;
    private Location destination;
    private Chat chat;
    private DestinationEntity entity;
    private IListener listener;

    private List<Participant> participants = new ArrayList<>();
    private HashMap<String,Feature> features=new HashMap<>();

    public Destination(String userId) {

        this.entity = new DestinationEntity();
        this.entity.chatId = ChatData.getAnId();
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
    public List<Feature> getFeatures(){
        return new ArrayList<>(features.values());
    }


    public void setParticipant(Participant aParticipant){

        Participant  participant= this.findParticipant(aParticipant);
        if(participant==null)
            this.addParticipant(aParticipant);
        else
            updateParticipant(participant,aParticipant);
    }

    public Participant findParticipant(Participant aParticipant){
        for (Participant participant : this.participants) {
            if (participant.equals(aParticipant)) {
                return participant;
            }
        }
        return null;
    }

    private void updateParticipant(Participant target,Participant newParticipant){
        target.set(newParticipant);
        Feature feature= target.getFeature();
        this.features.put(target.getGoogleId(),feature);
        if(hasListener())
            this.listener.emitParticipantChange(this,target);
    }

    private void addParticipant(Participant participant){
        this.participants.add(participant);
        Feature feature = participant.getFeature();
        this.features.put(participant.getGoogleId(),feature);
        if(hasListener())
            this.listener.emitNewParticipant(this,participant);
    }

    private boolean hasListener(){
        return this.listener!=null;
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
        notifyPropertyChanged(com.example.mrrobot.concurrent.BR.numUsers);
    }
    public String createBy(){
        return entity.userId;
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
    public String getChatId(){
        if(this.chat==null)
            return entity.chatId;
        return this.chat.getKey();

    }

    public void setOriginAddress(String name) {
        String newName;
        if (entity.name != null) {
            String[] split = this.entity.name.split(ADDRESS_SEPARATOR,2);
            newName = split[0] + ADDRESS_SEPARATOR + name;
        } else {
            newName = ADDRESS_SEPARATOR + name;
        }
        setName(newName);

    }
    public String getOriginAddress(){
        if(this.getName()!=null) {
            String name = getName().split(ADDRESS_SEPARATOR,2)[1];
            return name.isEmpty()? null:name;
        }
        return null;
    }
    public String getDestinationAddress(){
        if(this.getName()!=null) {
            String name = getName().split(ADDRESS_SEPARATOR,2)[0];
            return name.isEmpty()? null:name;
        }
        return null;
    }

    public void setDestinationAddress(String name) {
        String newName;
        if (entity.name != null) {
            String[] split = this.entity.name.split(ADDRESS_SEPARATOR,2);
            newName = name + ADDRESS_SEPARATOR + split[1];
        } else {
            newName = name + ADDRESS_SEPARATOR;
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


    private void notifyChangeAttribute() {
        if (this.mutableLiveData != null)
            this.mutableLiveData.postValue(this);
    }


    @Override
    public boolean equals(@Nullable Object obj) {
        try{
            if(this==obj)
                return true;
            if(obj instanceof Destination){
                Destination destination=(Destination)obj;
                return destination.getId().equals(this.getId());
            }

        }catch (NullPointerException e){
            return false;
        }
        return false;
    }

    public interface IListener {

        void emitParticipantChange(Destination destination,Participant participant);
        void emitNewParticipant(Destination destination,Participant participant);
        //void onSelected(Destination destination);
        //void goToDestination(Destination destination);
    }


    @BindingAdapter("android:tint")
    public static void setColorFilter(ImageView imageView, int color) {
        imageView.setColorFilter(color);
    }
}
