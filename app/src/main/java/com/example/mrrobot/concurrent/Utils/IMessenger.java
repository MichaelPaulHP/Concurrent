package com.example.mrrobot.concurrent.Utils;

public interface IMessenger {

    void onError(String message);
    void OnWarning(String message);
    void onSuccess(String message);

}
