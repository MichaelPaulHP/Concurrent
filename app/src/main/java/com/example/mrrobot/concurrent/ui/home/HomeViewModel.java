package com.example.mrrobot.concurrent.ui.home;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;

import com.example.mrrobot.concurrent.models.Destination;
import com.example.mrrobot.concurrent.models.DestinationData;
import com.example.mrrobot.concurrent.models.Participant;
import com.example.mrrobot.concurrent.models.User;
import com.example.mrrobot.concurrent.models.UserEmitter;

public class HomeViewModel extends AndroidViewModel {


    private User user;


    /////////////////// METHODS
    public HomeViewModel(Application application) {
        super(application);

        user = User.getCurrentUser();
        UserEmitter.requestMyDestinations();
        //requestMyDestinations();
        UserEmitter.startListenerJoinMyDestinations();
        UserEmitter.startListenerOnChangeLocation();
    }


}
