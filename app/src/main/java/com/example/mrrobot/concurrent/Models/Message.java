package com.example.mrrobot.concurrent.Models;

import android.support.annotation.Nullable;

import com.example.mrrobot.concurrent.Firebase.DataBase;
import com.google.firebase.database.ServerValue;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;
import com.stfalcon.chatkit.commons.models.MessageContentType;

import java.util.Date;
import java.util.Map;

public class Message implements IMessage,
        MessageContentType.Image, /*this is for default image messages implementation*/
        MessageContentType /*and this one is for custom content type (in this case - voice message)*/

{

    public static final int  LOG_MESSAGE= 0;
    public static final int  ME_MESSAGE =1;
    public static final int  YOUR_MESSAGE =2;
    private  Date createdAt;


    public String text;
    public String userName;
    public Long  time;
    public int type;
    private User user;
    private String id;
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
    public Message(String messageId, User user, String text, Date createdAt) {
        this.id= messageId;
        this.text = text;
        this.user = user;
        this.createdAt = createdAt;
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

    /**
     * Returns message text
     *
     * @return the message text
     */
    @Override
    public String getText() {
        return this.text;
    }

    @Nullable
    @Override
    public String getImageUrl() {
        return null;
    }

    /**
     * Returns message identifier
     *
     * @return the message id
     */
    @Override
    public String getId() {
        return this.id;
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
        return this.createdAt;
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
