package com.example.mrrobot.concurrent.ui.home;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.ViewModel;
import android.util.Log;
import android.widget.Toast;

import com.example.mrrobot.concurrent.Services.SocketIO;
import com.example.mrrobot.concurrent.models.Destination;
import com.example.mrrobot.concurrent.models.Localization;
import com.example.mrrobot.concurrent.ui.destination.DestinationFragment;
import com.example.mrrobot.concurrent.ui.location.LocationViewModel;
import com.google.android.libraries.places.api.model.Place;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class HomeViewModel extends AndroidViewModel
        implements
        DestinationFragment.DestinationListener,
        Destination.IListenerDestination {

    public DestinationAdapter destinationAdapter;
    List<Destination> destinations = new ArrayList<>();
    Socket socket;
    private Destination destinationCurrent;

    /////////////////// METHODS

    public HomeViewModel(Application application) {
        super(application);

        initDestinationAdapter();
        this.socket=SocketIO.getSocket();
        this.socket.on("joinToDestination",onJoinToDestination);
    }

    private void initDestinationAdapter() {
        this.destinationAdapter = new DestinationAdapter();
        this.destinationAdapter.setDestinations(this.destinations);
    }

    private void addDestination(Destination destination) {
        this.destinations.add(destination);
        this.destinationAdapter.notifyNewDestinationInserted();
        this.destinationCurrent=destination; // DESTINATION CURRENT
        MapboxMap mapboxMap = LocationViewModel.getMapBox();
        Style style = mapboxMap.getStyle();
        if(style!=null){
            LocationViewModel.initSymbolLayer(style,destination.getId(),destination.getId(),getApplication().getBaseContext());
        }
    }

    Emitter.Listener onJoinToDestination = new Emitter.Listener() {

        @Override
        public void call(Object... args) {
            Destination destination;

            try {
                JSONObject data = (JSONObject) args[0];
                if(data==null) {
                    return;
                }
                String name = data.getString("name");
                String id = data.getString("idDestination");
                int color=Integer.parseInt(data.getString("color"));
                int numUsers = Integer.parseInt(data.getString("numUsers"));
                double latitude = Double.parseDouble( data.getString("latitude"));
                double longitude =Double.parseDouble( data.getString("longitude"));
                destination = new Destination();
                destination.setColor(color);
                destination.setName(name);
                destination.setId(id);
                destination.setNumUsers(numUsers);
                destination.setLocalization(new Localization("",latitude,longitude));
                destination.setListenerDestination(HomeViewModel.this);
                addDestination(destination);

            } catch (JSONException e) {
                return;
            }
        }
    };


    /**
     * on Click in Destination
     *
     * @param destination
     */

    @Override
    public void onClick(Destination destination) {
        // show Position of users
        destinationCurrent=destination;
    }

    @Override
    public void onDestinationSelected(Destination destination) {
        // add to list
        SocketIO.emitJoinToDestination(destination);

    }
}
