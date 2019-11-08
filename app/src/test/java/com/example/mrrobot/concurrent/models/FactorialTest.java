package com.example.mrrobot.concurrent.models;

import com.example.mrrobot.concurrent.Services.SocketIO;
import com.google.gson.Gson;

import org.junit.Test;

import io.socket.client.Socket;

import static org.junit.Assert.*;

public class FactorialTest {

    @Test
    public void factorial() {


        Socket socket= SocketIO.getSocket();

        /*while(!socket.connected()){
            System.out.println("conectando..");
        }*/

    }
}