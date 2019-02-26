package com.example.mrrobot.concurrent.ui.chat;

import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.mrrobot.concurrent.Models.Chat;
import com.example.mrrobot.concurrent.Models.Message;
import com.example.mrrobot.concurrent.Models.User;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

public class ChatViewModel extends ViewModel {

    List<Chat> chats = new ArrayList<>();
    User user = new User("CURRENT USER","33333");
    MessageRecyclerViewAdapter messageAdapter;
    ChatRoomListener chatRoomListener;

    public ChatViewModel() {

    }


    public void createChat(){
        Chat chat = Chat.save("first chat",this.user);
        chat.addUserToList(this.user);

        this.chats.add(chat);
        // why am I had a list of messages
        // on create chat update UI
        this.chatRoomListener.onNewChat(chat);

        chat.addMessagesListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                // { key = -LZbgTSxYE_Wc87bXZSp, value = {text=hola, time=1551149164415, type=1, userName=CURRENT USER} }
                Message message = dataSnapshot.getValue(Message.class);

                Log.i("CHAT","onChildAdded"+ message.toString());
                messageAdapter.addMessage(message);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.i("CHAT","onChildChanged"+ dataSnapshot.toString());
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
    public void TEST(){
        Chat prueba= this.chats.get(0);
        prueba.saveMessage(new Message("hola",this.user.getUserName(),Message.ME_MESSAGE));
        // prueba.addMessageToList();
        prueba.saveMessage(new Message("Mundo HOLA",this.user.getUserName(),Message.ME_MESSAGE));
        User qwe = new User("MY NAME","MY ID");
        prueba.saveUser(qwe);
        prueba.addUserToList(qwe);
        prueba.saveMessage(new Message("hola q hace",qwe.getUserName(),Message.YOUR_MESSAGE));
        prueba.saveMessage(new Message("dime",this.user.getUserName(),Message.ME_MESSAGE));
    }

    public void setMessageAdapter(MessageRecyclerViewAdapter messageAdapter) {
        this.messageAdapter = messageAdapter;
    }

    public List<Message> getMessageOfChat(int chat){
        return this.chats.get(chat).getMessages();
    }
    public boolean isEmptyChats(){
        return this.chats.isEmpty();
    }

    public void setChatRoomListener(ChatRoomListener chatRoomListener) {
        this.chatRoomListener = chatRoomListener;
    }

    public interface ChatRoomListener{
        void onNewChat(Chat chat);
    }


}
