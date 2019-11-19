package com.example.mrrobot.concurrent.Services.ConcurrentService;

import com.example.mrrobot.concurrent.Config.SocketIO;

public class ConcurrentApiConstants {

    public static final  String BASE_URL= SocketIO.URL;
    public static final String USER=BASE_URL+"/user";

    public static final String REGISTER_USER=USER+"/register";

}
