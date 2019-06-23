package com.example.mrrobot.concurrent.Firebase.DB;

import com.example.mrrobot.concurrent.models.Chat;
import com.example.mrrobot.concurrent.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatData {
    long createAt;
    String key;
    String name;
    Double numOfParticipant;
    public ChatData() {

    }
    public ChatData(Chat chat) {
        this.createAt =chat.getCreatedAt().getTime();
        this.key = chat.getKey();
        this.name = chat.getName();
        this.numOfParticipant = chat.getNumOfParticipants();

    }

    public static Task<Void> saveChat(final Chat chatSaved){
        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        // REFERENCES
        //  CHAT
        final DatabaseReference dbReferenceChats = database.getReference("/RoomsChat/Chats");
        final String idChat = dbReferenceChats.push().getKey();


        chatSaved.setKey(idChat);
        //chatSaved.addParticipants(user); // user is participant
        ChatData chatData = new ChatData(chatSaved);
        // save chat
        return dbReferenceChats.child(idChat).setValue(chatData);

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
//        chatSaved.setCreatedBy(user.getIdGoogle());
//        chatSaved.setCreatedAt(Chat.getNowDate());
//        chatSaved.addParticipants(user); // user is participant
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
//                                user.listeners.onJoinToChat(chatSaved);
//                                //chatSaved.chatListener.(chatSaved);
//                            }
//                        }).addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Log.i("CHAT","onFailure "+e.getMessage());
//                            }
//                        });
//
//                        toUpdate.put("RoomsChat/Users/"+user.getIdGoogle()+"/", user.toMap());
//
//                        dbReferenceChats.updateChildren(toUpdate).addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                Log.i("CHAT","onSuccess add chat to user");
//                                user.listeners.onJoinToChat(chatSaved);
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
