package com.example.mrrobot.concurrent.models;

import android.support.annotation.NonNull;


import com.example.mrrobot.concurrent.Firebase.DB.ChatData;
import com.example.mrrobot.concurrent.Firebase.DB.UserData;

import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import com.google.firebase.database.ValueEventListener;
import com.stfalcon.chatkit.commons.models.IUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User implements IUser {

    private static User USER_CURRENT;

    private String id;
    private String idGoogle;
    private String name;
    private String avatar;
    private Double numChats = 0.0;
    private List<Chat> myChats = new ArrayList<>();

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
            USER_CURRENT = new User("0", "IgGoogle220619", "UserName Current", "http://i.imgur.com/pv1tBmT.png");
        }
        return USER_CURRENT;
    }

    public void joinToChat(final Chat chat) {

        /*UserData.addChat(this,chat).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                myChats.add(chat);
                numChats=numChats+1;
            }
        });
        */
        UserData.jointToChat(this, chat).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                User.this.addChat(chat);
                chat.addParticipants(User.this);
                User.this.userListeners.onJoinToChat();
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

    public void save() {
        UserData.save(this);
    }

    public List<Chat> getMyChats() {
        return myChats;
    }

    public void requestMyChats() {
        UserData.getMyChats(this, getMyChatsFromDB);
    }

    public void addChat(Chat chat) {
        this.myChats.add(chat);
        this.numChats=numChats+1;
        chat.requestMyMessages();
        chat.chatListener=this.chatListener;
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


    public interface IUserListeners {
        /**
         * when this user join to chat
         */
        void onJoinToChat();

    }
}
