package com.example.mrrobot.concurrent.Firebase.DB;

import android.support.annotation.NonNull;

import com.example.mrrobot.concurrent.models.Message;
import com.example.mrrobot.concurrent.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageData {

    public  long createAtLong;
    public String text;
    public String userId;
    public String userName;

    public MessageData() {
    }

    public MessageData(Message message) {
        this.createAtLong = message.getCreatedAt().getTime();
        this.text = message.getText();
        User user= (User) message.getUser();
        this.userId = user.getIdGoogle();
        this.userName = message.getUser().getName();
    }

    public static Task<Void> saveMessage(String chatKey,Double x,Message message) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference dbReferenceMessages;

        dbReferenceMessages = database.getReference("/RoomsChat");

        String num=x.intValue()+"";
        MessageData messageData = new MessageData(message);

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/Chats/" + chatKey+ "/numOfMessage", x+1);
        childUpdates.put("/Messages/" + chatKey+"/"+num , messageData);

        return dbReferenceMessages.updateChildren(childUpdates);
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

}
