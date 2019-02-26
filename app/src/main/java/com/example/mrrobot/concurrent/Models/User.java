package com.example.mrrobot.concurrent.Models;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String userName;

    private String id;
    private List<Chat> chats=new ArrayList<>();

    public User() {

    }

    public User(String userName, String id) {
        this.userName = userName;
        this.id = id;
    }


    /**
     * NOT SAVE in data base, solo add to list
     * @param chat
     */
    public void joinToChat(Chat chat){
        this.chats.add(chat);
    }
    /**
     * this user send message to chat
     * @param messageText: text of message
     * @param chat: destination chat
     */
//    public void sendMessage(String messageText,Chat chat){
//        Message message = new Message(messageText,this,Message.ME_MESSAGE);
//        chat.addMessage(message);
//    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
