package com.example.mrrobot.concurrent.Firebase.DB;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.mrrobot.concurrent.models.Chat;
import com.example.mrrobot.concurrent.models.User;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserData {
    public String avatar;
    public String idGoogle;
    public String name;
    public Double numChats;
    public List<String> myChats;
    public UserData() {
    }

    public UserData(User user) {
        this.avatar = user.getAvatar();
        this.idGoogle = user.getIdGoogle();
        this.name = user.getName();
        this.numChats=user.getNumChats();
    }
    public User toUser(){
        User user = new User(this.idGoogle,this.idGoogle,this.name,this.avatar);
        return user;
    }

    public static Task<Void> save(User user) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference dbReferenceMessages;
        dbReferenceMessages = database.getReference("/RoomsChat/Users");

        UserData userData = new UserData(user);
        return dbReferenceMessages.child(user.getIdGoogle()).setValue(userData);
    }

    public static Task<Void> addChat(final User user,final Chat chat){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dbReferenceChat;
        dbReferenceChat = database.getReference("/RoomsChat/Users/"+user.getIdGoogle()+"/myChats");


        String numOfChats=user.getNumChats().intValue()+"";
        ChatData chatData = new ChatData(chat);
        return dbReferenceChat.child(numOfChats).setValue(chatData);
    }
    public static Task<Void> jointToChat(User user,Chat chat){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dbReferenceChat;
        dbReferenceChat = database.getReference("/RoomsChat");

        ChatData chatData = new ChatData(chat);
        UserData userData = new UserData(user);

        // in user

        String numChatsOfUser=user.getNumChats().intValue()+"";
        // in Chat

        String numParticipant=chat.getNumOfParticipants().intValue()+"";
        //update values
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/Users/" + user.getIdGoogle()+"/myChats/"+numChatsOfUser, chatData);// USER
        childUpdates.put("/Users/" + user.getIdGoogle()+"/numChats", user.getNumChats()+1);// USER

        childUpdates.put("/Chats/" + chat.getKey() + "/participants/"+numParticipant, userData);//CHAT
        childUpdates.put("/Chats/" + chat.getKey()+"/numOfParticipant" , chat.getNumOfParticipants()+1);// Chat add cant

        return dbReferenceChat.updateChildren(childUpdates);
    }
    public static void getMyChats(User user,ValueEventListener listener){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dbReferenceChat;
        dbReferenceChat = database.getReference("/RoomsChat/Users/"+user.getIdGoogle()+"/myChats");
        dbReferenceChat.addListenerForSingleValueEvent(listener);
        /*dbReferenceChat.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Chat> myChats= new ArrayList<>();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    ChatData newChat = postSnapshot.getValue(ChatData.class);
                    myChats.add(newChat);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/
    }


//    /**
//     * find user and update if it not exist save user
//     * @param user
//     */
//    public static void findAndUpdateOrSave(final User user){
//        final FirebaseDatabase database = FirebaseDatabase.getInstance();
//        final DatabaseReference dbReferenceMessages;
//        dbReferenceMessages = database.getReference("/RoomsChat/Users");
//        dbReferenceMessages.child(user.getIdGoogle()).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if(dataSnapshot.exists()){
//
//                    Log.i("CHAT",dataSnapshot.toString());
//                    User user1=dataSnapshot.getValue(User.class);
//                    if(user1.myChats!=null && user1.numChats!=null ){
//                        user.myChats.addAll(user1.myChats);
//                        user.numChats=user1.numChats;
//                        user.listeners.onJoinToChat(null);
//                    }
//
//                }
//                else {
//                    Log.i("CHAT","dataSnapshot.exists() is false");
//                    // SAVE
//                    dbReferenceMessages.child(user.getIdGoogle()).setValue(user.toMap())
//                            .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void aVoid) {
//                                    Log.i("CHAT","saved user");
//                                }
//                            }).addOnCanceledListener(new OnCanceledListener() {
//                        @Override
//                        public void onCanceled() {
//                            Log.i("CHAT","onCanceled");
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Log.i("CHAT","onFailure "+e.getMessage());
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Log.i("CHAT",databaseError.toString());
//            }
//        });
//    }

    public static Task<Void> updateUser(User user){
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference dbReferenceMessages;
        dbReferenceMessages = database.getReference();
        Map<String, Object> toUpdate = new HashMap<>();
        toUpdate.put("//RoomsChat/Users/"+user.getIdGoogle(),user);
        return dbReferenceMessages.updateChildren(toUpdate);
//        dbReferenceMessages.child(user.getIdGoogle()).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if(dataSnapshot.exists()){
//
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

    }

}
