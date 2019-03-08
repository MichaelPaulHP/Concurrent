package com.example.mrrobot.concurrent.models;

import android.databinding.BindingAdapter;
import android.databinding.ObservableField;
import android.widget.ImageView;

import com.example.mrrobot.concurrent.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Chat {

    public String name;
    public String idMessages;
    public String idParticipants;
    public Long createdAtLong;
    public String createdBy;
    public Integer numOfParticipants;

    private Date createdAt;

    private String key;
    private List<User> participants = new ArrayList<>();
    private List<Message> messages = new ArrayList<>();

    public ObservableField<String> persons = new ObservableField<>("0");
    public int icon = R.drawable.ic_location_on_black_24dp;

    public Chat() {

    }

    public Chat(String name, String createdBy) {
        this.name = name;
        this.createdBy = createdBy;
        this.numOfParticipants = 0;
        this.createdAt = Calendar.getInstance().getTime();
        this.createdAtLong = this.createdAt.getTime();
    }

    /**
     * SAVE a chat with key ,name, a user, a message
     * and  user NOT  join to  Chat create
     * chats{ 23423423424:{Chat}},
     * user {23423423424{username:key}},
     * messages{23423423424{33333333{Message}}}
     *
     * @return Chat with witch to database
     */
    public static Task<Void> save(String name, final User user, Chat chatSaved) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        // REFERENCES
        //  CHAT
        DatabaseReference dbReferenceChats;
        dbReferenceChats = database.getReference("/RoomsChat/Chats");
        final String idChat = dbReferenceChats.push().getKey();

        //  USERS
        final DatabaseReference dbReferenceUsers;
        dbReferenceUsers = database.getReference("/RoomsChat/Users");
        final String idUsers = dbReferenceUsers.push().getKey();
        //  MESSAGES
        final DatabaseReference dbReferenceMessages;
        dbReferenceMessages = database.getReference("/RoomsChat/Messages");
        final String idMessages = dbReferenceUsers.push().getKey();


        //final Message lastMessage = new Message("I created this room", user, Chat.getNowDate());
        // new CHAT
        //chatSaved = new Chat(name, idChat,idMessages,idParticipants,Chat.getNowDate());
        chatSaved.name = name;
        chatSaved.setKey(idChat);
        chatSaved.idMessages = idMessages;
        chatSaved.idParticipants = idUsers;
        chatSaved.createdBy=user.getId();
        chatSaved.setCreatedAt(Chat.getNowDate());


        // save user, this user is participant
        dbReferenceUsers.child(idUsers).push().setValue(user.toMap());
        // save messages log
        //String idLastMessage=dbReferenceMessages.child(idMessages).push().getIdGoogle();
        //dbReferenceMessages.child(idMessages).push().setValue(lastMessage.toMap());

        //lastMessage.setIdGoogle(idLastMessage);
        // save chat
        return dbReferenceChats.child(idChat).setValue(chatSaved.toMap());

    }





    /**
     * set listener for child Message
     *
     * @param childEventListener
     */
    public void addMessagesListener(ChildEventListener childEventListener) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference dbReferenceMessages;
        dbReferenceMessages = database.getReference("/RoomsChat/Messages");
        dbReferenceMessages.child(this.idMessages).addChildEventListener(childEventListener);

    }

    public void addUsersListener(ChildEventListener childEventListener) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference dbReferenceMessages;
        dbReferenceMessages = database.getReference("/RoomsChat/Users");
        dbReferenceMessages.child(this.idParticipants).addChildEventListener(childEventListener);
    }

    public static Date getNowDate() {
        return Calendar.getInstance().getTime();
    }

    /**
     * add a user in list,
     *
     * @param user
     */
    public void addParticipants(User user) {
        this.participants.add(user);
        // add message type LOG
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
    /////////////////////////
    /////////////// GETTERS


    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
        this.createdAtLong = createdAt.getTime();
    }
    public void setCreatedAt(Long createdAt) {

        this.createdAt = Calendar.getInstance().getTime();
        this.createdAt.setTime(createdAt);
        this.createdAtLong = createdAt;
    }
    public List<Message> getMessages() {
        return messages;
    }


    @Override
    public boolean equals(Object obj) {
        Chat a = (Chat) obj;
        return this.key.equals(a.key);
    }


    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        //result.put("key", this.key);
        result.put("name", name);
        result.put("idMessages", this.idMessages);
        result.put("idParticipants", this.idParticipants);
        result.put("createdAtLong", this.createdAtLong);
        result.put("createdBy", this.createdBy);
        result.put("numOfParticipants", this.numOfParticipants);
        return result;
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

}
