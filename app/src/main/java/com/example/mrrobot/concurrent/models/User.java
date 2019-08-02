package com.example.mrrobot.concurrent.models;

import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;


import com.example.mrrobot.concurrent.Firebase.Auth;
import com.example.mrrobot.concurrent.Firebase.DB.ChatData;
import com.example.mrrobot.concurrent.Firebase.DB.UserData;

import com.example.mrrobot.concurrent.Services.SocketIO;
import com.example.mrrobot.concurrent.Utils.Utils;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import com.google.firebase.database.ValueEventListener;
import com.stfalcon.chatkit.commons.models.IUser;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class User implements IUser {

    private static User USER_CURRENT;

    private String id;
    private String idGoogle;
    private String name;
    private String avatar;
    private Double numChats = 0.0;
    private List<Chat> myChats = new ArrayList<>();
    private List<Destination> myDestinations = new ArrayList<>();
    public MutableLiveData<Destination> hasNewDestination= new MutableLiveData<>();
    public IUserListeners userListeners;
    public Chat.IChatListener chatListener;

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

    public void requestMyDestinations() {
        Socket socket = SocketIO.getSocket();

        socket.emit("getMyDestinations", Utils.toJsonObject("userID",getIdGoogle()));

        socket.on("getMyDestinations", onGetMyDestinations);//getMyDestinations
        boolean c=socket.connected();
        boolean lister= socket.hasListeners("getMyDestinations");
    }

    Emitter.Listener onGetMyDestinations = new Emitter.Listener() {

        @Override
        public void call(Object... args) {

            try {
                //last item of args is a ACK
                for (int i =0;i<args.length;i++){

                    JSONObject data = (JSONObject) args[i];
                    Destination destination = Destination.get(data);
                    getCurrentUser().addDestination(destination);
                }
                //destination.setDestinationListener(HomeViewModel.this);
            } catch (Exception e) {
                Log.e("USER",e.toString());
            }
        }
    };

    private void addDestination(Destination destination){
        Destination destinationMemory=findDestinationById(destination.getId());
        if(destinationMemory==null){
            this.myDestinations.add(destination);
            hasNewDestination.postValue(destination);
        }
        else{
            // update destination?
            destinationMemory.setNumUsers(destination.getNumUsers());
        }
    }

    public void createDestination(Destination destination){
        String idChat= ChatData.getAnId();
        User current = User.getCurrentUser();
        Chat chat = new Chat(destination.getName(),current.getIdGoogle());
        chat.setKey(idChat);
        current.saveChatAndJoint(chat);
        Destination.emitNewDestination(destination,idChat);
    }
    public void startOnJoinToDestination(){
        Socket socket = SocketIO.getSocket();
        socket.on("joinToDestination",onJoinToDestination);
    }

    Emitter.Listener onJoinToDestination = new Emitter.Listener() {

        @Override
        public void call(Object... args) {
            Destination destination;

            try {
                JSONObject data = (JSONObject) args[0];
                if(data==null) {
                    return;
                }
                destination=Destination.get(data);
                //destination.setDestinationListener(HomeViewModel.this);
                addDestination(destination);

            } catch (Exception e) {

                return;
            }
        }
    };
    public boolean isMyDestination(String id){

        return findDestinationById(id) != null;
    }

    private Destination findDestinationById(String id){
        for(Destination d:this.myDestinations){
            if(d.getId().equals(id))
                return d;
        }
        return null;
    }
    ///////////////////////////////////////////////////////
    /////////////// CHAT
    /////////////////////////////////////////////

    public void joinToChat(final Chat chat) {

        UserData.jointToChat(this, chat).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                User.this.addChat(chat);
                chat.addParticipants(User.this);

            }
        });
    }

    public void saveChatAndJoint(final Chat x) {
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

    ValueEventListener getMyChatsFromDB = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                ChatData newChatData = postSnapshot.getValue(ChatData.class);
                Chat chat = newChatData.toChat();
                chat.requestParticipants();
                User.this.addChat(chat);///
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    public void addChat(Chat chat) {
        this.myChats.add(chat);
        this.numChats = numChats + 1;
        chat.requestMyMessages();
        chat.chatListener = this.chatListener;
        chat.setListenerForNewMessagesFromDB();
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


    public interface IUserListeners {
        /**
         * when this user join to chat
         */
        void onJoinToChat();

    }
}
