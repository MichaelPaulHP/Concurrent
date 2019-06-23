package com.example.mrrobot.concurrent.Firebase.DB;

import android.support.annotation.NonNull;

import com.example.mrrobot.concurrent.models.Message;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class MessageData {

    public  long createAtLong;
    public String text;
    public String userId;
    public String userName;

    public MessageData(Message message) {
        this.createAtLong = message.getCreatedAt().getTime();
        this.text = message.getText();
        this.userId = message.getUser().getId();
        this.userName = message.getUser().getName();
    }

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

}
