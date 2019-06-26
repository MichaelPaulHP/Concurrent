package com.example.mrrobot.concurrent.models;

import android.databinding.BindingAdapter;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;

import com.example.mrrobot.concurrent.Firebase.DB.ChatData;
import com.example.mrrobot.concurrent.Firebase.DB.MessageData;
import com.example.mrrobot.concurrent.Firebase.DB.UserData;
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
import com.google.firebase.database.ValueEventListener;

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
    private Double numOfParticipants = 0.0;
    private Double numOfMessage = 0.0;

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
        this.numOfMessage = 0.0;
        this.createdAt = Calendar.getInstance().getTime();
    }

    public void saveMessage(final Message message) {
        MessageData.saveMessage(this.key, this.numOfMessage, message);
    }

    public void requestMyMessages() {
        ChatData.getMyMessages(this.key, myMessagesSaved);
    }

    public void setListenerForNewMessagesFromDB() {
        ChatData.setListenerOnNewMessage(this.key, onNewMessageListener);
    }
    public void requestParticipants(){
        ChatData.getParticipants(this.key,getParticipantsFromDB);
    }
    /**
     * add a message in list
     */
    public void addMessage(Message message) {
        this.messages.add(message);
        this.numOfMessage=numOfMessage+1;
        chatListener.onNewMessage(Chat.this, message);
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

        if (this.numOfParticipants == null) {
            this.numOfParticipants = 0.0;
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
        this.numOfParticipants = numOfParticipants + 1;
        // add message type LOG
    }

    public void saveThisChat() {
        ChatData.saveChat(this);
    }


    public User findParticipantByIdGoogle(String id) {
        for (User user : participants) {
            if (user.getIdGoogle().equals(id)) {
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

    private void addMessageFromDB(MessageData messageData) {
        Message message;
        // get  a Message clone
        if (isMyMessage(messageData.userId)) {
            message = MessagePrototypeFactory.getPrototype("myMessage");
        } else {
            message = MessagePrototypeFactory.getPrototype("otherMessage");
            User user = findParticipantByIdGoogle(messageData.userId);
            message.setUser(user);

        }
        message.setCreateAt(new Date(messageData.createAtLong));
        message.setText(messageData.text);
        Chat.this.addMessage(message);
    }

    ChildEventListener onNewMessageListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            MessageData messageData = dataSnapshot.getValue(MessageData.class);
            addMessageFromDB(messageData);
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
    };

    ///////////////////////////////////////////////////////
    /////////////// Get messages from DB
    /////////////////////////////////////////////
    private ValueEventListener myMessagesSaved = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                MessageData messageData = postSnapshot.getValue(MessageData.class);
                addMessageFromDB(messageData);

            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private boolean isMyMessage(String userId) {
        return userId.equals(User.getCurrentUser().getIdGoogle());
    }
    ///////////////////////////////////////////////////////
    /////////////// Get participants from DB
    /////////////////////////////////////////////
    ValueEventListener getParticipantsFromDB = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            for (DataSnapshot p : dataSnapshot.getChildren()) {
                UserData userData =p.getValue(UserData.class);
                User user=userData.toUser();
                Chat.this.addParticipants(user);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };


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

    public Double getNumOfMessage() {
        return numOfMessage;
    }

    public void setNumOfMessage(Double numOfMessage) {
        this.numOfMessage = numOfMessage;
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


    /*@BindingAdapter("android:src")
    public static void setImageResource(ImageView imageView, int resource) {
        imageView.setImageResource(resource);
    }*/

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
    public interface IChatListener {
        /**
         * new message x in chat chat
         *
         * @param chat chat
         * @param x    message
         */
        void onNewMessage(Chat chat, Message x);


    }

}
