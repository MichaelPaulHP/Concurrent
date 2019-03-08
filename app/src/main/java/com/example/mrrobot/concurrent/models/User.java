package com.example.mrrobot.concurrent.models;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.stfalcon.chatkit.commons.models.IUser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User implements IUser {

    private String id;
    private String idGoogle;

    // firebase
    public String name;
    public String avatar;
    public Double numChats;
    public List<Chat> myChats;

    public User() {

    }

    public User(String name, String avatar) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;
    }

    public String getIdGoogle() {
        return idGoogle;
    }
    public void setIdGoogle(String idGoogle) {
        this.idGoogle = idGoogle;
    }

    public void setId(String id) {
        this.id = id;
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

    /**
     * save user in DB
     * @param user with chats' list
     * @return task<Void>
     */
    public static Task<Void> save(User user){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference dbReferenceMessages;
        dbReferenceMessages = database.getReference("/RoomsChat/Users");
        return dbReferenceMessages.child(user.getIdGoogle()).setValue(user);
    }

//    public static List<String> myChats(String idUser){
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        final DatabaseReference dbReferenceMessages;
//        dbReferenceMessages = database.getReference("/RoomsChat/Users/"+idUser);
//        dbReferenceMessages.child("chats");
//    }

    /**
     *  fields  to save in DB user reference indirect, exclude Lists
     * @return
     */
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        //result.put("id", this.id);
        result.put("name", this.name);
        result.put("avatar", this.avatar);
        result.put("numChats", this.numChats);


        return result;
    }
}
