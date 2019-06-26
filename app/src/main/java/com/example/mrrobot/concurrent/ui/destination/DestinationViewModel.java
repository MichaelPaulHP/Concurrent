package com.example.mrrobot.concurrent.ui.destination;

import android.arch.lifecycle.ViewModel;

import com.example.mrrobot.concurrent.Services.SocketIO;
import com.example.mrrobot.concurrent.models.Destination;
import com.example.mrrobot.concurrent.models.Localization;
import com.example.mrrobot.concurrent.ui.home.DestinationAdapter;
import com.google.android.libraries.places.api.model.Place;
import com.mapbox.mapboxsdk.geometry.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class DestinationViewModel extends ViewModel
    implements  Destination.IListenerDestination{


    private Socket socket;
    private List<Destination> resultsDestination=new ArrayList<>();
    public DestinationAdapter destinationAdapter;

    private Destination destinationSelected;
    private boolean isNewDestination=true;
    public DestinationViewModel() {
        initDestinationAdapter();
        this.socket= SocketIO.getSocket();
        this.socket.on("destinationFound",onDestinationFound);
    }

    public void onPlaceSelected(Place place){

        Localization localization = new Localization("",place.getLatLng().latitude,place.getLatLng().longitude);
        //SocketIO.emitFindDestinations(localization);
        this.destinationSelected=createDestination(place);

    }
    private Destination  createDestination(Place place){

        String name = place.getName();
        Double longitude = place.getLatLng().longitude;
        double latitude = place.getLatLng().latitude;
        Localization localization = new Localization(name, latitude, longitude);
        Destination destination = new Destination();
        destination.setName(name);
        destination.setLocalization(localization);
        destination.setNumUsers(2);
        destination.setListenerDestination(this);
        return destination;
    }
    private Emitter.Listener onDestinationFound = new Emitter.Listener() {

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
                int numUsers = Integer.parseInt(data.getString("numUsers"));
                double latitude = Double.parseDouble( data.getString("latitude "));
                double longitude =Double.parseDouble( data.getString("longitude "));
                destination = new Destination();
                destination.setName(name);
                destination.setId(id);
                destination.setNumUsers(numUsers);
                destination.setLocalization(new Localization("",latitude,longitude));
                destination.setListenerDestination(DestinationViewModel.this);
                addToListOfResults(destination);

            } catch (JSONException e) {
                return;
            }
            // show in list
        }
    };




    private void initDestinationAdapter() {
        this.destinationAdapter = new DestinationAdapter();
        this.destinationAdapter.setDestinations(this.resultsDestination);

    }

    private void addToListOfResults(Destination destination){
        this.resultsDestination.add(destination);
        this.destinationAdapter.notifyNewDestinationInserted();
    }

    /**
     * on Click in Destination
     *
     * @param destination
     */
    @Override
    public void onClick(Destination destination) {
        // join and close.
        this.destinationSelected=destination;
        isNewDestination=false;
    }

    public Destination getDestinationSelected() {
        if (isNewDestination){
            //SocketIO.emitNewDestination(destinationSelected);
            // response with id of Destination.
        }
        else{
            //SocketIO.emitJoinToDestination(destinationSelected);
        }
        return destinationSelected;
    }
}
