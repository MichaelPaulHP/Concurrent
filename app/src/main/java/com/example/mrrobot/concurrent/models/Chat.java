package com.example.mrrobot.concurrent.models;

import android.databinding.BindingAdapter;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;

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
    public String name;
    public Long createdAtLong;
    public String createdBy;
    public Double numOfParticipants;
    public List<Message> messages = new ArrayList<>();

    private Date createdAt;
    private String key;
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
        this.createdAtLong = this.createdAt.getTime();
    }

    public Chat(Chat chat) {
        this.name = chat.name;
        this.createdAtLong = chat.createdAtLong;
        this.createdBy = chat.createdBy;
        this.numOfParticipants = chat.numOfParticipants;
        this.key=chat.key;
    }

    /**
     * save a Chat in DB, add a user to participant and User is
     *
     * @param name      chat's name
     * @param user      user saved in DB
     * @param chatSaved chat for add data
     * @return Task<Void>
     */
    public static void saveChatAndAddUser(String name, final User user, final Chat chatSaved) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        // REFERENCES
        //  CHAT
        final DatabaseReference dbReferenceChats = database.getReference("/RoomsChat/Chats");
        final String idChat = dbReferenceChats.push().getKey();

        chatSaved.name = name;
        chatSaved.setKey(idChat);
        chatSaved.createdBy = user.getIdGoogle();
        chatSaved.setCreatedAt(Chat.getNowDate());
        chatSaved.addParticipants(user); // user is participant
        // save chat
        dbReferenceChats.child(idChat).setValue(chatSaved.toMap())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // update numChats in user and add chat
                        DatabaseReference dbReferenceChats = database.getReference();
                        Map<String, Object> toUpdate = new HashMap<>();
                        user.addChat(chatSaved); // add tolist and increment

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

                        toUpdate.put("RoomsChat/Users/"+user.getIdGoogle()+"/", user.toMap());

                        dbReferenceChats.updateChildren(toUpdate).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.i("CHAT","onSuccess add chat to user");
                                user.listeners.onJoinToChat(chatSaved);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.i("CHAT","onFailure add chat to user"+e.getMessage());
                                user.myChats.remove(chatSaved);
                            }
                        });
                    }
                });
    }

    public static void addParticipant(final Chat chat, final User user) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dbReferenceChat;
        dbReferenceChat = database.getReference("/RoomsChat");

        Map<String, Object> toUpdate = new HashMap<>();
        // increment numParticipant
        chat.addParticipants(user);
        toUpdate.put("Chats/" + chat.key, chat.toMap());

        // increment numChats
        user.addChat(chat);
        toUpdate.put("Users/" + user.getIdGoogle(), user.toMap());
        dbReferenceChat.updateChildren(toUpdate);
    }
    public static void saveMessage(Chat chat,Message m){
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

    }



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
                Message message=dataSnapshot.getValue(Message.class);
                message.setId(dataSnapshot.getKey());
                chatListener.onNewMessage(Chat.this,message);
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
     * add a user in list,
     *
     * @param user
     */
    public void addParticipants(User user) {
        this.participants.add(user);
        if(this.numOfParticipants==null){
            this.numOfParticipants=0.0;
        }
        this.numOfParticipants = this.numOfParticipants + 1;
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
        result.put("key", this.key);
        result.put("name", this.name);
        result.put("createdAtLong", this.createdAtLong);
        result.put("createdBy", this.createdBy);
        result.put("numOfParticipants", this.numOfParticipants);
        result.put("participants", this.listOfUserToSave());
        result.put("messages", this.messages);
        return result;
    }
    @Exclude
    public Map<String, Object> toMapOmitLists() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("key", this.key);
        result.put("name", this.name);
        result.put("createdAtLong", this.createdAtLong);
        result.put("createdBy", this.createdBy);
        result.put("numOfParticipants", this.numOfParticipants);
        return result;
    }

    private List<User> listOfUserToSave() {
        List<User> userList = new ArrayList<>();
        for (User user : this.participants) {
            User temp = new User(user);
            userList.add(temp);
        }
        return userList;
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
