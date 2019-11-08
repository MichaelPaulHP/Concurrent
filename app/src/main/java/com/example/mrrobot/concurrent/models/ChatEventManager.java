package com.example.mrrobot.concurrent.models;

import java.util.ArrayList;
import java.util.List;

public class ChatEventManager {
    // Todo: use this
    private List<Chat.IChatListener> listeners;

    public ChatEventManager() {

    }

    public void subscribe(Chat.IChatListener listener) {
        if (this.listeners == null) {
            this.listeners = new ArrayList<>();
        }
        this.listeners.add(listener);
    }

    public void unsubscribe(Chat.IChatListener listener) {
        this.listeners.remove(listener);
    }

    public void notifyNewMessage(Chat chat, Message message) {
        try {
            for (Chat.IChatListener x : listeners) {
                x.onNewMessage(chat, message);
            }
        } catch (NullPointerException e) {

        }

    }

    public void notifyNewParticipantAdded(Chat chat, User user) {
        for (Chat.IChatListener x : listeners) {
            x.onNewParticipant(chat, user);
        }
    }

    /*private List<T> listeners;

    public ChatEventManager() {
        this.listeners = new ArrayList<>();
    }

    public void subscribe(T listener) {
        this.listeners.add(listener);
    }

    public void unsubscribe(T listener) {
        this.listeners.remove(listener);
    }

    public void notifyChange(){
        for (T x: listeners) {
            x.notifyChange();
        }
    }*/
}
