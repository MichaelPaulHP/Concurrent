package com.example.mrrobot.concurrent.models;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stfalcon.chatkit.commons.models.IUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User implements IUser {

    private static User USERCURRENT;
    //private String id;
    private String idGoogle;
    public IUserListeners listeners;
    // firebase
    public String name;
    public String avatar;
    public Double numChats = 0.0;
    public List<Chat> myChats = new ArrayList<>();

    public User() {

    }

    public static User getCurrentUser() {
        if (USERCURRENT == null) {
            // if I am saved
            USERCURRENT = new User("UserCurrent", "http://i.imgur.com/pv1tBmT.png");
            //USERCURRENT.setId("0");
            USERCURRENT.setIdGoogle("5555555555");
            //User.findAndUpdateOrSave(USERCURRENT);// get chats,
        }
        return USERCURRENT;
    }

    public User(String name, String avatar) {
        this.name = name;
        this.avatar = avatar;
    }

    public User(User user) {
        this.idGoogle = user.idGoogle;
        this.name = user.name;
        this.avatar = user.avatar;
        this.numChats = user.numChats;
    }

    public void addChat(Chat chat) {
        myChats.add(chat);
        this.numChats = numChats + 1;
    }

    public String getIdGoogle() {
        return idGoogle;
    }
    public void setIdGoogle(String idGoogle) {
        this.idGoogle = idGoogle;
    }

    /*public void setId(String id) {
        this.id = id;
    }*/

    /**
     * Returns the user's id
     *
     * @return the user's id
     */
    @Override
    public String getId() {
        return "0";
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

    /**
     * save user in DB
     *
     * @param user with chats' list
     * @return task<Void>
     */
    public static Task<Void> save(User user) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference dbReferenceMessages;
        dbReferenceMessages = database.getReference("/RoomsChat/Users");
        return dbReferenceMessages.child(user.getIdGoogle()).setValue(user.toMap());
    }

    /**
     * find user and update if it not exist save user
     * @param user
     */
    public static void findAndUpdateOrSave(final User user){
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference dbReferenceMessages;
        dbReferenceMessages = database.getReference("/RoomsChat/Users");
        dbReferenceMessages.child(user.getIdGoogle()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    Log.i("CHAT",dataSnapshot.toString());
                    User user1=dataSnapshot.getValue(User.class);
                    if(user1.myChats!=null && user1.numChats!=null ){
                        user.myChats.addAll(user1.myChats);
                        user.numChats=user1.numChats;
                        user.listeners.onJoinToChat(null);
                    }

                }
                else {
                    Log.i("CHAT","dataSnapshot.exists() is false");
                    // SAVE
                    dbReferenceMessages.child(user.getIdGoogle()).setValue(user.toMap())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.i("CHAT","saved user");
                        }
                    }).addOnCanceledListener(new OnCanceledListener() {
                        @Override
                        public void onCanceled() {
                            Log.i("CHAT","onCanceled");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i("CHAT","onFailure "+e.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i("CHAT",databaseError.toString());
            }
        });
    }
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


    /**
     * fields  to save in DB user reference indirect, exclude Lists
     *
     * @return
     */
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        //result.put("id", this.id);
        result.put("idGoogle", this.idGoogle);
        result.put("name", this.name);
        result.put("avatar", this.avatar);
        result.put("numChats", this.numChats);
        result.put("myChats", this.listOfMyChatsToSave());

        return result;
    }

    /**
     * public String name;
     * public Long createdAtLong;
     * public String createdBy;
     * public Integer numOfParticipants;
     *
     * @return
     */
    @Exclude
    public List<Map<String, Object>> listOfMyChatsToSave() {
        List<Map<String, Object>> toSave = new ArrayList<>();
        for (Chat chat : myChats) {
            //Chat temp = new Chat(chat); //temp  not contains lists
            toSave.add(chat.toMapOmitLists());
        }
        return toSave;
    }

    public interface IUserListeners{
        /**
         * when this user join to chat x
         * @param x chat saved in DB
         */
        void onJoinToChat(Chat x);

    }
}
