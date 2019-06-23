package com.example.mrrobot.concurrent.models.Builders;

import com.example.mrrobot.concurrent.models.Message;

public class MessageBuilder {
    private Message message;

    public MessageBuilder defaultAtributes(){
        return this;
    }
    public Message builder(){
        return this.message;
    }
}
