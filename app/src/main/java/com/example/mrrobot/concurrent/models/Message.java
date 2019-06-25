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

public class Message implements IMessage,Cloneable
{

    private User user;

    private Date createAt;
    private String text;
    private String id;

    public Message() {
    }

    public Message(User user) {
        this.user = user;
        this.createAt = Calendar.getInstance().getTime();
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public void setText(String text) {
        this.text = text;
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

    @Override
    public Message clone() throws CloneNotSupportedException {
        return (Message) super.clone();
    }
}
