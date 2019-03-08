package com.example.mrrobot.concurrent.models;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Message implements IMessage
{



    private User user;
    private Date createAt;
    // firebase
    public String text;
    public String userName;
    public String userIdGoogle;
    public Long createAtLong;

    public Message() {

    }
    public Message (String text, User user) {
        this.text = text;
        this.user = user;
        this.userName=user.getName();
        this.userIdGoogle =user.getIdGoogle();
        this.createAt = Calendar.getInstance().getTime();
        this.createAtLong=createAt.getTime();
    }
    /**
     * save a message in database
     *
     * @param message with text,user,create add
     */
    public static Task<Void> saveMessage(String chatKey, Message message) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference dbReferenceMessages;
        dbReferenceMessages = database.getReference("/RoomsChat/Messages");
        return dbReferenceMessages.child(chatKey).push().setValue(message);
    }
    public static void getMessages(final String chatKey, List<Message> messagesContainer){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference dbReferenceMessages;
        dbReferenceMessages = database.getReference("/RoomsChat/Messages");
        dbReferenceMessages.child(chatKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //messagesContainer.putAll((Map) dataSnapshot.getValue());
                    dbReferenceMessages.child(chatKey).removeEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }





    public void setUser(User user) {
        this.user = user;
    }

    public void setId(java.lang.String id) {
        this.userIdGoogle = id;
    }

    public void setCreateAt(Long l){
        this.createAtLong=l;
        this.createAt= Calendar.getInstance().getTime();
        this.createAt.setTime(l);
    }

    /**
     * Returns message identifier
     *
     * @return the message id
     */
    @Override
    public String getId() {
        return this.userIdGoogle;
    }

    /**
     * Returns message text
     *
     * @return the message text
     */
    @Override
    public String getText() {
        return this.text;
    }

    /**
     * Returns message author. See the {@link IUser} for more details
     *
     * @return the message author
     */
    @Override
    public IUser getUser() {
        return this.user;
    }

    /**
     * Returns message creation date
     *
     * @return the message creation date
     */
    @Override
    public Date getCreatedAt() {
        return this.createAt;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        //result.put("id", this.id);
        result.put("text", this.text);
        result.put("userIdGoogle", this.userIdGoogle);
        result.put("userName", this.userName);
        result.put("createdAtLong", this.createAtLong);


        return result;
    }


}
