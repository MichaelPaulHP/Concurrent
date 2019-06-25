package com.example.mrrobot.concurrent.models;

import java.util.HashMap;
import java.util.Map;

public class MessagePrototypeFactory {

    private static final Map<String, Message> prototypes = new HashMap<>();

    static {
        prototypes.put("myMessage", new Message(User.getCurrentUser()));
        prototypes.put("otherMessage", new Message(null));
    }

    public static Message getPrototype(String type) {
        try {
            return  prototypes.get(type).clone();
        } catch (NullPointerException ex) {
            System.out.println("Prototype:  " + type + ", doesn't exist");
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
