package com.example.mrrobot.concurrent.ui.destination;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableField;
import android.support.annotation.MainThread;
import android.widget.Toast;

import com.example.mrrobot.concurrent.Services.SocketIO;
import com.example.mrrobot.concurrent.models.Destination;
import com.example.mrrobot.concurrent.models.Localization;
import com.example.mrrobot.concurrent.models.User;
import com.example.mrrobot.concurrent.ui.home.DestinationAdapter;
import com.google.android.libraries.places.api.model.Place;
import com.mapbox.mapboxsdk.geometry.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class DestinationViewModel extends AndroidViewModel
        implements Destination.IDestinationListener {


    private Socket socket;
    private List<Destination> resultsDestination = new ArrayList<>();
    public MutableLiveData<Boolean> hasNewDestination = new MutableLiveData<>();

    private Destination newDestination;

    public ObservableField<Destination> destinationObservableField = new ObservableField<>();

    public DestinationViewModel(Application application) {
        super(application);

        this.socket = SocketIO.getSocket();
        this.socket.on("destinationsFound", onDestinationFound);
    }

    public void onPlaceSelected(Place place) {

        Localization localization = new Localization(place.getName(), place.getLatLng().latitude, place.getLatLng().longitude);
        Destination.emitFindDestinations(localization);// response SERVE onDestinationFound
        this.newDestination = createTempDestination(place);
        // show in map and add to selected
        Destination.destinationSelected.postValue(this.newDestination);
    }

    private Destination createTempDestination(Place place) {

        String name = place.getName();
        Double longitude = place.getLatLng().longitude;
        double latitude = place.getLatLng().latitude;
        Localization localization = new Localization(name, latitude, longitude);
        Destination destination = new Destination();
        destination.setName(name);
        destination.setLocalization(localization);
        destination.setNumUsers(0);
        destination.setDestinationListener(this);
        return destination;
    }


    private Emitter.Listener onDestinationFound = new Emitter.Listener() {

        @Override
        public void call(Object... args) {

            try {
                List<Destination> destinations = Destination.destinationsToList(args);
                for (Destination destination : destinations) {
                    destination.setDestinationListener(DestinationViewModel.this);
                    addToListOfResults(destination);
                }
            } catch (NullPointerException e) {

            }
        }
    };


    private void addToListOfResults(Destination destination) {
        this.resultsDestination.add(destination);
        this.hasNewDestination.postValue(true);
        //this.destinationAdapter.notifyNewDestinationInserted();

    }

    /**
     * on Click in Destination
     *
     * @param destination
     */
    @Override
    public void onClick(Destination destination) {

        Destination.destinationSelected.postValue(destination);
        destinationObservableField.set(destination);

    }

    public void OnSubmit() {
        Destination destinationSelected = Destination.destinationSelected.getValue();
        if (destinationSelected != null) {
            User current =User.getCurrentUser();
            String idDestination=destinationSelected.getId();
            // is new ?
            if(idDestination==null){
                // create a new Destination
                current.createDestination(destinationSelected);
            }
            else {
                String userId = current.getIdGoogle();
                if(current.isMyDestination(idDestination)){
                    Toast.makeText(getApplication().getApplicationContext(),"Ya esta Dentro",Toast.LENGTH_LONG).show();
                }
                else{
                    Destination.emitJoinToDestination(destinationSelected.getId(), userId);
                }

            }
        }
    }
    public void deleteSelection(){

        Destination.destinationSelected.postValue(null);
    }


    public List<Destination> getResultsDestination() {
        return resultsDestination;
    }
}
