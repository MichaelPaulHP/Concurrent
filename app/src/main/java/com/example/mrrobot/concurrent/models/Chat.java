package com.example.mrrobot.concurrent.models;

import android.databinding.BindingAdapter;
import android.databinding.ObservableField;
import android.widget.ImageView;

import com.example.mrrobot.concurrent.R;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Chat {
    public String name;
    public String id;
    public String idMessages;
    public String idUsers;
    public Map<String, String> time;
    public Message lastMessage;

    private List<User> users = new ArrayList<>();
    private List<Message> messages = new ArrayList<>();

    public ObservableField<String> persons= new ObservableField<>("1/3");
    public int icon= R.drawable.mapbox_logo_icon;
    public Chat() {

    }
    public Chat(String name, String id, String idMessages, String idUsers) {
        this.name = name;
        this.id = id;
        this.idMessages = idMessages;
        this.idUsers = idUsers;
        this.time=ServerValue.TIMESTAMP;
    }

    /**
     * SAVE a chat with id ,name, a user, a message
     * and  user NOT  join to  Chat create
     * chats{ 23423423424:{Chat}},
     * user {23423423424{username:id}},
     * messages{23423423424{33333333{Message}}}
     *
     * @return Chat with witch to database
     */
    public static Chat save(String name, final User user) {
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

        // new CHAT
        final Message lastMessage = new Message("created by" + user.getUserName(), user.getUserName(), Message.LOG_MESSAGE);
        Chat chat = new Chat(name, idChat,idMessages,idUsers);


        // save chat
        dbReferenceChats.child(idChat).setValue(chat);

        // save user
        dbReferenceUsers.child(idUsers).push().setValue(user);

        // save messages log
        dbReferenceMessages.child(idMessages).push().setValue(lastMessage);

        return chat;
    }

    /**
     * save a message in database
     *
     * @param message
     */
    public Task<Void> saveMessage(final Message message) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference dbReferenceMessages;
        dbReferenceMessages = database.getReference("/RoomsChat/Messages");
        return dbReferenceMessages.child(this.idMessages).push().setValue(message);
    }

    /**
     * save in DB
     *
     * @param user
     */
    public Task<Void> saveUser(User user) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference dbReferenceMessages;
        dbReferenceMessages = database.getReference("/RoomsChat/Users");

        return dbReferenceMessages.child(this.idUsers).push().setValue(user);

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
        dbReferenceMessages.child(this.idUsers).addChildEventListener(childEventListener);
    }

    /**
     * save a user in database, add message Log and add to list
     *
     * @param user
     */
    public void addUserToList(User user) {
        this.users.add(user);
        // add message type LOG
    }
    public void addMessageToList(Message message){
        this.messages.add(message);
    }


    public static Chat findChat(List<Chat> list, Chat x) {
        for (Chat chat : list) {
            if (chat.equals(x)) {
                return chat;
            }
        }
        return null;
    }

    public Message getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
    }

    public List<Message> getMessages() {
        return messages;
    }

    @Override
    public boolean equals(Object obj) {
        Chat a = (Chat) obj;
        return this.id.equals(a.id);
    }

    @BindingAdapter("android:src")
    public static void setImageResource(ImageView imageView, int resource){
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
