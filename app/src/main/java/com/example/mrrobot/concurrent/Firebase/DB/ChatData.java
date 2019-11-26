package com.example.mrrobot.concurrent.Firebase.DB;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.mrrobot.concurrent.models.Chat;
import com.example.mrrobot.concurrent.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatData {
    long createAt;
    String key;
    String name;
    Double numOfParticipant;
    Double numOfMessage=0.0;

    public ChatData() {

    }
    public ChatData(Chat chat) {
        this.createAt =chat.getCreatedAt().getTime();
        this.key = chat.getKey();
        this.name = chat.getName();
        this.numOfParticipant = chat.getNumOfParticipants();
        this.numOfMessage=chat.getNumOfMessage();
    }
    public Chat toChat(){
        Chat chat = new Chat();
        Date date = new Date(this.createAt);
        chat.setCreatedAt(date);
        chat.setKey(this.key);
        chat.setName(this.name);
        chat.setNumOfParticipants(this.numOfParticipant);
        chat.setNumOfMessage(this.numOfMessage);
        return chat;
    }
    public static void getParticipants(String chatKey,ValueEventListener valueEventListener ){
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference dbReferenceChats = database.getReference("/RoomsChat/Chats/"+chatKey+"/participants");
        dbReferenceChats.addListenerForSingleValueEvent(valueEventListener);
    }

    public static  String getAnId(){

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference dbReferenceChats = database.getReference("/RoomsChat/Chats");
        final String idChat = dbReferenceChats.push().getKey();
        return idChat;
    }

    public static Task<Void> saveChat(final Chat chatSaved){
        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        // REFERENCES
        //  CHAT
        final DatabaseReference dbReferenceChats = database.getReference("/RoomsChat/Chats");
        final String idChat = chatSaved.getKey();


        chatSaved.setKey(idChat);
        //chatSaved.addParticipant(user); // user is participant
        ChatData chatData = new ChatData(chatSaved);
        // save chat
        return dbReferenceChats.child(idChat).setValue(chatData);

    }

    public static void setListenerOnNewMessage(String chatKey,ChildEventListener childEventListener){
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference dbReferenceChats = database.getReference("/RoomsChat/Messages/"+chatKey);

        dbReferenceChats.addChildEventListener(childEventListener);
    }
    public static void getMyMessages(String chatKey,ValueEventListener valueEventListener){
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference dbReferenceChats = database.getReference("/RoomsChat/Messages/"+chatKey);
        dbReferenceChats.addListenerForSingleValueEvent(valueEventListener);
    }

//
//    public static void saveChatAndAddUser(final User user, final Chat chatSaved) {
//        final FirebaseDatabase database = FirebaseDatabase.getInstance();
//
//        // REFERENCES
//        //  CHAT
//        final DatabaseReference dbReferenceChats = database.getReference("/RoomsChat/Chats");
//        final String idChat = dbReferenceChats.push().getKey();
//
//
//        chatSaved.setKey(idChat);
//        chatSaved.setCreatedBy(user.getGoogleId());
//        chatSaved.setCreatedAt(Chat.getNowDate());
//        chatSaved.addParticipant(user); // user is participant
//        ChatData chatData = new ChatData(chatSaved);
//        // save chat
//        dbReferenceChats.child(idChat).setValue(chatData)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        // update numChats in user and add chat
//                        DatabaseReference dbReferenceChats = database.getReference();
//                        Map<String, Object> toUpdate = new HashMap<>();
//                        user.addChat(chatSaved); // add tolist and increment
//
//                        User.save(user).addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                Log.i("CHAT","onSuccess add chat to user");
//                                user.listeners.onNewChat(chatSaved);
//                                //chatSaved.chatListener.(chatSaved);
//                            }
//                        }).addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Log.i("CHAT","onFailure "+e.getMessage());
//                            }
//                        });
//
//                        toUpdate.put("RoomsChat/Users/"+user.getGoogleId()+"/", user.toMap());
//
//                        dbReferenceChats.updateChildren(toUpdate).addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                Log.i("CHAT","onSuccess add chat to user");
//                                user.listeners.onNewChat(chatSaved);
//                            }
//                        }).addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Log.i("CHAT","onFailure add chat to user"+e.getMessage());
//                                user.myChats.remove(chatSaved);
//                            }
//                        });
//                    }
//                });
//    }

    public static Task<Void> addParticipant(final Chat chat, final User user) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dbReferenceChat;
        dbReferenceChat = database.getReference("/RoomsChat/Chats/"+chat.getKey()+"/participants");

        String numOfParticipants=chat.getNumOfParticipants().intValue()+"";
        UserData userData = new UserData(user);
        return dbReferenceChat.child(numOfParticipants).setValue(userData);
    }

}
