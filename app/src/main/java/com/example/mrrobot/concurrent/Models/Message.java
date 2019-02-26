package com.example.mrrobot.concurrent.Models;

import com.example.mrrobot.concurrent.Firebase.DataBase;
import com.google.firebase.database.ServerValue;

import java.util.Map;

public class Message {
    public static final int  LOG_MESSAGE= 0;
    public static final int  ME_MESSAGE =1;
    public static final int  YOUR_MESSAGE =2;



    public String text;
    public String userName;
    public Long  time;
    public int type;

    public Message() {

    }

    public Message(String text, String userName, Long  time, int type) {
        this.text = text;
        this.userName = userName;
        this.time = time;
        this.type = type;
    }

    public Message(String text, String user, int typeMessage) {
        this.text = text;
        this.userName = user;
        this.time =new Long(585) ;
        this.type = typeMessage;
        ;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long  time) {
        this.time = time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Message{" +
                "text='" + text + '\'' +
                ", userName='" + userName + '\'' +
                ", type=" + type +
                '}';
    }
}
