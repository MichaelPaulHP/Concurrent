package com.example.mrrobot.concurrent.models;

import android.databinding.BindingAdapter;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;

import com.example.mrrobot.concurrent.Firebase.DB.ChatData;
import com.example.mrrobot.concurrent.Firebase.DB.MessageData;
import com.example.mrrobot.concurrent.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IgnoreExtraProperties
public class Chat {

    // firebase


    private String name;
    private String createdBy;
    private Date createdAt;
    private String key;
    private Double numOfParticipants=0.0;

    private List<Message> messages = new ArrayList<>();
    private List<User> participants = new ArrayList<>();

    public IChatListener chatListener;

    public ObservableField<String> persons = new ObservableField<>("0");
    public int icon = R.drawable.ic_location_on_black_24dp;

    public Chat() {

    }

    public Chat(String name, String createdBy) {
        this.name = name;
        this.createdBy = createdBy;
        this.numOfParticipants = 0.0;
        this.createdAt = Calendar.getInstance().getTime();
    }



    /*public static void saveMessage(Chat chat,Message m){
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        // REFERENCES
        //  CHAT
        final DatabaseReference dbReferenceChat = database.getReference("/RoomsChat/Chats/"+chat.getKey()+"/");
        String keyMessage = dbReferenceChat.push().getKey();
        m.setId(keyMessage);
        // add message to chat
        chat.addMessage(m);
        dbReferenceChat.child("messages").setValue(chat.getMessages());// EDIT solo add a message
        //save message
        final DatabaseReference dbReferenceMessage = database.getReference("/RoomsChat/Messages/"+chat.getKey()+"/");
        dbReferenceMessage.child(keyMessage).setValue(m);

    }*/





    /**
     * set listener for child Message of this chat
     *
     */
    private void addMessagesListener() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference dbReferenceMessages;
        dbReferenceMessages = database.getReference("/RoomsChat/Messages/"+this.getKey());
        dbReferenceMessages.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                MessageData messageData=dataSnapshot.getValue(MessageData.class);
                // messageData is this user?
                //message.setId(dataSnapshot.getKey());
                //chatListener.onNewMessage(Chat.this,message);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

//    public void addUsersListener(ChildEventListener childEventListener) {
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        final DatabaseReference dbReferenceMessages;
//        dbReferenceMessages = database.getReference("/RoomsChat/Users");
//        dbReferenceMessages.child(this.key).addChildEventListener(childEventListener);
//    }

    public static Date getNowDate() {
        return Calendar.getInstance().getTime();
    }

    /**
     * add a user in list y DB,
     *
     * @param user
     */
    public void addParticipants(final User user) {

        if(this.numOfParticipants==null){
            this.numOfParticipants=0.0;
        }
        /*ChatData.addParticipant(this,user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                participants.add(user);
                numOfParticipants = numOfParticipants + 1;
                user.joinToChat(Chat.this);
            }
        });*/
        this.participants.add(user);
        this.numOfParticipants=numOfParticipants+1;
        // add message type LOG
    }

    public void saveThisChat(){
        ChatData.saveChat(this);
    }
    public void addMessage(Message message) {
        this.messages.add(message);

    }


    public User findParticipantById(String id) {
        for (User user : participants) {
            if (user.getId().equals(id)) {
                return user;
            }
        }
        return null;
    }

    public static Chat findChat(List<Chat> list, Chat x) {
        for (Chat chat : list) {
            if (chat.equals(x)) {
                return chat;
            }
        }
        return null;
    }


    ///////////////////////////////////////////////////////
    /////////////// GETTERS
    /////////////////////////////////////////////

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Double getNumOfParticipants() {
        return numOfParticipants;
    }

    public void setNumOfParticipants(Double numOfParticipants) {
        this.numOfParticipants = numOfParticipants;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    public List<Message> getMessages() {
        return messages;
    }


    @Override
    public boolean equals(Object obj) {
        Chat a = (Chat) obj;
        return this.key.equals(a.key);
    }


    @BindingAdapter("android:src")
    public static void setImageResource(ImageView imageView, int resource) {
        imageView.setImageResource(resource);
    }

//    public static void main(String args[]){
//
//        Chat chat1 = new Chat();
//        User a = new User("Michael");
//
//        chat1.name="mi primer chat";
//
//        chat1.save();
//        chat1.addUser(a);
//        chat1.addMessage(new Message("hola mundo",a,Message.YOUR_MESSAGE));
//        chat1.addMessage(new Message("eeeeeeeeeeee",a,Message.YOUR_MESSAGE));
//
//    }
    public interface IChatListener{
    /**
     * new message x in chat chat
     * @param chat  chat
     * @param x message
     */
    void onNewMessage(Chat chat,Message x);



    }

}
