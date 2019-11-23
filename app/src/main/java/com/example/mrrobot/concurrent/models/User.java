package com.example.mrrobot.concurrent.models;

import android.arch.lifecycle.MutableLiveData;
import android.location.Location;
import android.support.annotation.NonNull;
import android.util.Log;


import com.example.mrrobot.concurrent.Firebase.Auth;
import com.example.mrrobot.concurrent.Firebase.DB.ChatData;
import com.example.mrrobot.concurrent.Firebase.DB.UserData;

import com.example.mrrobot.concurrent.Services.SocketIO;
import com.example.mrrobot.concurrent.Utils.Utils;
import com.example.mrrobot.concurrent.entityes.DestinationEntity;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import com.google.firebase.database.ValueEventListener;

import com.stfalcon.chatkit.commons.models.IUser;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;



public class User extends Participant implements IUser {

    private static User USER_CURRENT;

    private String id;
    private String idGoogle;
    private String name;
    private String avatar;
    private Double numChats = 0.0;


    private List<Chat> myChats = new ArrayList<>();
    private List<Destination> myDestinations = new ArrayList<>();
    public MutableLiveData<Destination> hasNewDestination= new MutableLiveData<>();

    public MutableLiveData<Location> myLocation = new MutableLiveData<>();
    public IUserListeners userListeners;
    //public Chat.IChatListener chatListener;

    public User(String id, String idGoogle, String name, String avatar) {
        this.id = id;
        this.idGoogle = idGoogle;
        this.name = name;
        this.avatar = avatar;
    }

    public static User getCurrentUser() {
        if (USER_CURRENT == null) {
            Auth auth = Auth.getInstance();
            String username=auth.getUserName();

            USER_CURRENT = new User(auth.getId(), auth.getId(), auth.getUserName(), "http://i.imgur.com/pv1tBmT.png");

        }
        return USER_CURRENT;
    }


    ///////////////////////////////////////////////////////
    /////////////// Destination
    /////////////////////////////////////////////


    public void addMyDestination(Destination destination){
        Destination destinationMemory=findDestinationById(destination.getId());
        if(destinationMemory==null){
            this.myDestinations.add(destination);
            hasNewDestination.postValue(destination);
        }
        else{
            destinationMemory.setNumUsers(destination.getNumUsers());
        }
    }

    public void updateMyDestination(Destination destination){
        Destination myDestination=findDestinationById(destination.getId());
        if(myDestination!=null)
            myDestination.setNumUsers(destination.getNumUsers());
    }

    public boolean isMyDestination(String id){

        return findDestinationById(id) != null;
    }

    public Destination findDestinationById(String id){
        for(Destination d:this.myDestinations){
            if(d.getId().equals(id))
                return d;
        }
        return null;
    }

    ///////////////////////////////////////////////////////
    /////////////// POSITION
    /////////////////////////////////////////////
    public void setLocation(Location location){
        this.myLocation.postValue(location);
        this.setLatitude(location.getLatitude());
        this.setLongitude(location.getLongitude());
        UserEmitter.emitChangeMyLocation();
    }





    ///////////////////////////////////////////////////////
    /////////////// CHAT
    /////////////////////////////////////////////


    private void joinToChat(final Chat chat) {

        UserData.jointToChat(this, chat).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                User.this.addChat(chat);
                chat.addParticipant(User.this);
            }
        });
    }

    public void saveChatAndJoin(final Chat x) {
        ChatData.saveChat(x).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                joinToChat(x);
            }
        });
    }

    public Task<Void> save() {
        return UserData.save(this).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                SocketIO.saveThisUser(User.getCurrentUser().getId());
            }
        });
    }

    public List<Chat> getMyChats() {
        return myChats;
    }

    public void requestMyChats() {
        UserData.getMyChats(this, getMyChatsFromDB);
    }

    private ValueEventListener getMyChatsFromDB = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                ChatData newChatData = postSnapshot.getValue(ChatData.class);
                Chat chat = newChatData.toChat();
                User.this.addChat(chat);///
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private void addChat(Chat chat) {
        Chat chatInMemory=Chat.findChat(this.myChats,chat);
        if(chatInMemory==null){
            this.myChats.add(chat);
            this.numChats = numChats + 1;
            this.userListeners.onNewChat();
            // todo: listener?
            chat.chatListener = this.userListeners;
            chat.requestParticipants();
            chat.requestMyMessages();

            chat.initListenerForNewMessagesFromDB();
        }
    }




    public Double getNumChats() {
        return numChats;
    }

    public String getIdGoogle() {
        return idGoogle;
    }

    public void setIdGoogle(String idGoogle) {
        this.idGoogle = idGoogle;
    }

    public List<Destination> getMyDestinations() {
        return myDestinations;
    }

    /**
     * Returns the user's id
     *
     * @return the user's id
     */
    @Override
    public String getId() {
        return this.id;
    }

    /**
     * Returns the user's name
     *
     * @return the user's name
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * Returns the user's avatar image url
     *
     * @return the user's avatar image url
     */
    @Override
    public String getAvatar() {
        return this.avatar;
    }

    public static  User findUserById(List<User> users,User user){

        for(User u:users){
            if(u.getId().equals(user.getId())){
                return u;
            }
        }
        return null;
    }


    public interface IUserListeners extends Chat.IChatListener {
        /**
         * when this user join to chat
         */
        void onNewChat();

    }
}
