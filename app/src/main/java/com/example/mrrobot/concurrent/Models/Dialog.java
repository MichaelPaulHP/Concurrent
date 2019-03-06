package com.example.mrrobot.concurrent.Models;

import com.stfalcon.chatkit.commons.models.IDialog;
import com.stfalcon.chatkit.commons.models.IUser;

import java.util.ArrayList;
import java.util.List;

/**
 *  this is a chat room
 */
public class Dialog implements IDialog<Message> {

    private String id;
    private String dialogPhoto;
    private String dialogName;
    private ArrayList<User> users;
    private Message lastMessage;

    private int unreadCount;

    public Dialog() {

    }

    public Dialog(String id, String dialogPhoto, String dialogName, ArrayList<User> users, Message lastMessage, int unreadCount) {
        this.id = id;
        this.dialogPhoto = dialogPhoto;
        this.dialogName = dialogName;
        this.users = users;
        this.lastMessage = lastMessage;
        this.unreadCount = unreadCount;
    }

    @Override
    public String getId() {
        return this.getId();
    }

    @Override
    public String getDialogPhoto() {
        return this.dialogPhoto;
    }

    @Override
    public String getDialogName() {
        return this.dialogName;
    }

    @Override
    public List<? extends IUser> getUsers() {
        return this.users;
    }

    @Override
    public Message getLastMessage() {
        return null;
    }

    @Override
    public void setLastMessage(Message message) {
        this.lastMessage = lastMessage;
    }

    @Override
    public int getUnreadCount() {
        return 0;
    }
}
