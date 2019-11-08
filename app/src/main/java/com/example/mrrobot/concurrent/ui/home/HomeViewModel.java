package com.example.mrrobot.concurrent.ui.home;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;

import com.example.mrrobot.concurrent.models.Destination;
import com.example.mrrobot.concurrent.models.User;

public class HomeViewModel extends AndroidViewModel
        implements
        Destination.IListener {



    private User user;


    /////////////////// METHODS
    public HomeViewModel(Application application) {
        super(application);

        user=User.getCurrentUser();
        //user.requestMyDestinations();
        //requestMyDestinations();
        user.startOnJoinToDestination();

    }
    /*public void requestMyDestinations() {
        Socket socket = SocketIO.getSocket();

        socket.emit("getMyDestinations", Utils.toJsonObject("userID",getIdGoogle()));

        socket.on("getMyDestinations", onGetMyDestinations);//getMyDestinations
        boolean c=socket.connected();
        boolean lister= socket.hasListeners("getMyDestinations");
    }

    Emitter.Listener onGetMyDestinations = new Emitter.Listener() {

        @Override
        public void call(Object... args) {

            try {
                //last item of args is a ACK
                for (int i =0;i<args.length;i++){

                    JSONObject data = (JSONObject) args[i];
                    Destination destination = Destination.get(data);
                    user.addDestination(destination);
                }
                //destination.setDestinationListener(HomeViewModel.this);
            } catch (Exception e) {
                Log.e("USER",e.toString());
            }
        }
    };
    */







    /**
     * on Click in Destination
     *
     * @param destination
     */

    @Override
    public void onClick(Destination destination) {
        // show Position of users

    }
}
