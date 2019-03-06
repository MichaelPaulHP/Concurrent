package com.example.mrrobot.concurrent.models;

import com.stfalcon.chatkit.commons.models.IUser;

import java.util.ArrayList;
import java.util.List;

public class User implements IUser {
    private String userName;

    private String id;
    private List<Chat> chats=new ArrayList<>();
    private String avatarUrl;
    public User() {

    }

    public User(String userName, String id,String avatar) {
        this.userName = userName;
        this.id = id;
        this.avatarUrl=avatar;
    }


    /**
     * NOT SAVE in data base, solo add to list
     * @param chat
     */
    public void joinToChat(Chat chat){
        this.chats.add(chat);
    }


    /**
     * Returns the user's name
     *
     * @return the user's name
     */
    @Override
    public String getName() {
        return this.userName;
    }

    /**
     * Returns the user's avatar image url
     *
     * @return the user's avatar image url
     */
    @Override
    public String getAvatar() {
        return this.avatarUrl;
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
