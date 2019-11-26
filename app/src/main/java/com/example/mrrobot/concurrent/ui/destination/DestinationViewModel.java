package com.example.mrrobot.concurrent.ui.destination;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.example.mrrobot.concurrent.Config.MapBox;
import com.example.mrrobot.concurrent.R;
import com.example.mrrobot.concurrent.Services.SocketIO;
import com.example.mrrobot.concurrent.Utils.DestinationSymbol;
import com.example.mrrobot.concurrent.Utils.IMessenger;
import com.example.mrrobot.concurrent.Utils.SymbolPrinter;
import com.example.mrrobot.concurrent.Utils.Utils;
import com.example.mrrobot.concurrent.entityes.DestinationEntity;
import com.example.mrrobot.concurrent.models.Destination;
import com.example.mrrobot.concurrent.models.DestinationData;
import com.example.mrrobot.concurrent.models.User;
import com.example.mrrobot.concurrent.ui.home.DestinationAdapter;
import com.google.android.libraries.places.api.model.Place;

import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.OnSymbolDragListener;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.utils.BitmapUtils;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import timber.log.Timber;

public class DestinationViewModel extends AndroidViewModel
        implements OnMapReadyCallback,DestinationAdapter.IEventListener,  OnSymbolDragListener {

    private MapView mapView;
    private MapboxMap mapboxMap;



    private Socket socket;
    private List<Destination> resultsDestination = new ArrayList<>();
    public MutableLiveData<Boolean> hasNewDestination = new MutableLiveData<>();

    private SymbolPrinter symbolPrinter;

    private Destination myDestinationTemp;
    private DestinationSymbol myDestinationTempSymbol;

    private Location origin;
    private Location destination;


    public MutableLiveData<Destination> destinationFound = new MutableLiveData<>();
    private DestinationSymbol destinationFoundSymbol;


    private static final String ICON_PLACE = "ic-place";
    private static final String ICON_PLACE_DESTINATION = "ic-place-destination";

    private IMessenger messenger;


    //////////////////////////////
    ///////////METHODS
    /////////


    public DestinationViewModel(Application application) {
        super(application);

        this.socket = SocketIO.getSocket();
        this.socket.on("destinationsFound", onDestinationFound);
        createMyDestinationTemp();
    }

    public void initMapView(MapView mapView, Bundle savedInstanceState) {

        this.mapView = mapView;

        this.mapView.onCreate(savedInstanceState);
        this.mapView.getMapAsync(this);

    }

    // -16.429366, -71.545707
    @Override
    public void onMapReady(final MapboxMap mapboxMap) {

        this.mapboxMap = mapboxMap;

        // SET STYLE
        this.mapboxMap.setStyle(MapBox.style, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {

                configSymbolManager(style);
                addIcon(style);


                initMyDestinationSymbol();
                printAndGoMyOrigin();

            }
        });
    }

    private void configSymbolManager(Style style) {
        this.symbolPrinter = new SymbolPrinter(mapView, mapboxMap);
        this.symbolPrinter.addDragListener(this);
    }

    private void addIcon(Style style) {
        Resources resources = getApplication().getApplicationContext().getResources();
        if (resources != null) {


            Bitmap bitmap1 = BitmapUtils
                    .getBitmapFromDrawable(
                            resources.getDrawable(R.drawable.ic_location_on_black_24dp));
            Bitmap bitmap2 = BitmapUtils
                    .getBitmapFromDrawable(
                            resources.getDrawable(R.drawable.ic_markerii_15));

            style.addImage(ICON_PLACE_DESTINATION, bitmap2, true);
            style.addImage(ICON_PLACE, bitmap1, true);
        }
    }

    private void createMyDestinationTemp() {

        this.origin = getMyLocation();
        if (this.origin != null) {

            this.myDestinationTemp = new Destination(User.getCurrentUser().getGoogleId());
            this.myDestinationTemp.initMutableLiveData();
            this.myDestinationTemp.setOrigin(this.origin);

        }
    }

    private void initMyDestinationSymbol() {
        this.myDestinationTempSymbol =
                new DestinationSymbol
                (
                        this.symbolPrinter,
                        this.myDestinationTemp
                );
    }

    private void printAndGoMyOrigin() {


        printMyDestination();
        //printOrigin();
        goLocation(this.origin);
        setName(true);
    }

    private Location getMyLocation() {

        Location location = User.getCurrentUser().myLocation.getValue();
        if (location == null) {
            location = this.mapboxMap.getLocationComponent().getLastKnownLocation();
        }
        return location;
    }

    private void printMyDestination() {
        this.myDestinationTempSymbol.setDestination(this.myDestinationTemp);
        this.myDestinationTempSymbol.print();
    }


    public void onPlaceSelected(Place place, boolean isDestination) {
        Double latitude = place.getLatLng().latitude;
        Double longitude = place.getLatLng().longitude;
        //LatLng latLngPLace = place.getLatLng();
        String name = place.getName();


        if (isDestination) {
            if (this.destination == null)
                this.destination = new Location("GooglePlace");
            updateDestination(latitude, longitude);
            //printDestination();
            goLocation(this.destination);
            setName(false);

        } else {
            updateOrigin(latitude, longitude);
            //printOrigin();
            goLocation(this.origin);
            setName(true);
        }

        printMyDestination();

        try {
            findDestinations();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void updateDestination(Double latitude, Double longitude) {
        this.destination.setLatitude(latitude);
        this.destination.setLongitude(longitude);
        this.myDestinationTemp.setDestination(this.destination);
    }

    private void updateOrigin(Double latitude, Double longitude) {
        this.origin.setLatitude(latitude);
        this.origin.setLongitude(longitude);
        this.myDestinationTemp.setOrigin(this.origin);
    }


    public void goLocation(Location location) {

        Double zoom = this.mapboxMap.getCameraPosition().zoom;
        //this.mapboxMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                new CameraPosition.Builder()
                        .target(new LatLng(location))
                        .zoom(zoom)
                        .build()), 1000);
    }

    public void goMyOrigin() {
        try {
            Location location = this.myDestinationTemp.getOrigin();
            goLocation(location);
        } catch (NullPointerException e) {
            messenger.onError("Origin no found");
        }

    }

    public void goMyDestination() {
        try {
            Location location = this.myDestinationTemp.getDestination();
            goLocation(location);
        } catch (NullPointerException e) {
            messenger.onError("Destination no found");
        }
    }

    private void findDestinations() throws JSONException {

        if (origin != null && destination != null) {
            //Destination.emitFindDestinations(origin,destination);
            DestinationData.findDestinations(this.myDestinationTemp);
            // this.resultsDestination.clear();
            this.hasNewDestination.postValue(false);
        }

    }


    private Emitter.Listener onDestinationFound = new Emitter.Listener() {

        @Override
        public void call(Object... args) {

            try {

                List<Destination> destinations = DestinationEntity.readDestinations(args);

                for (Destination destination : destinations) {
                    //destination.setDestinationListener(DestinationViewModel.this);
                    addToListOfResults(destination);
                }
            } catch (NullPointerException e) {
                messenger.onError("destinations Not found ");
            }
        }
    };


    private void addToListOfResults(Destination destination) {
        Destination destinationFound;

        //destinationFound = Destination.findDestinationInListById(this.resultsDestination, destination);
        destinationFound = Utils.findInList(this.resultsDestination, destination);
        if (destinationFound == null) {
            this.resultsDestination.add(destination);
            this.hasNewDestination.postValue(true);
        }


        //this.destinationAdapter.notifyNewDestinationInserted();
    }


    private void createDestinationsFoundSymbol() {
        this.destinationFoundSymbol =
                new DestinationSymbol(
                        this.symbolPrinter,
                        this.destinationFound.getValue()
                );
    }




    public void OnSubmit(Destination destination) throws JSONException {

        Destination destinationSelected =destination;
        if (destinationSelected != null) {
            User current = User.getCurrentUser();
            String idDestination = destinationSelected.getId();
            // is new ?
            if (destinationSelected.equals(this.myDestinationTemp)) {
                // create a new Destination
                DestinationData.newDestination(destinationSelected);

            } else {
                if (current.isMyDestination(idDestination)) {
                    messenger.OnWarning("You are in this Destination");
                } else {
                    // this user join to destination
                    DestinationData.addParticipant(destinationSelected.getId(), current.getGoogleId());
                }
            }
        }
    }


    public List<Destination> getResultsDestination() {
        return resultsDestination;
    }

    public MutableLiveData<Destination> getMyDestinationObservable() {
        return this.myDestinationTemp.getObservable();
    }



    /*private void getAddressFromLatLng(final LatLng latLng, final MutableLiveData<Destination> destinationLiveData) {
        final Destination destination=destinationLiveData.getValue();
        final Double latitude = latLng.getLatitude();
        final Double longitude = latLng.getLongitude();

        Thread thread = new Thread() {
            @Override
            public void run() {
                Geocoder geocoder = new Geocoder(getApplication().getApplicationContext(), Locale.getDefault());
                String result = null;
                try {
                    List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
                    if (addressList != null && addressList.size() > 0) {
                        Address address = addressList.get(0);
                        String name = address.getAddressLine(0);// .getAddressLine(0);
                        String[] names = name.split(",");
                        name = names[0] + names[1];
                        DestinationViewModel.this.setName(destinationLiveData, name);

                    } else {
                        Log.e(TAG, "empty or null");
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Unable connect to Geocoder", e);
                }

            }
        };
        thread.start();
    }*/
    private String getAddressFromLocation(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        Geocoder geocoder = new Geocoder(getApplication().getApplicationContext(), Locale.getDefault());
        String result = null;
        try {
            List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                String name = address.getAddressLine(0);// .getAddressLine(0);
                String[] names = name.split(",");
                result = names[0] + names[1];

            } else {
                throw new Exception("Address not found");
            }
        } catch (IOException e) {
            Timber.e("Unable connect to Geocoder");
            this.messenger.onError("Unable connect to Geocoder");
        } catch (Exception e) {
            this.messenger.onError("Address not found");
        }
        return result;
    }

    ///////////////////////////////////////////////////////////
    /////////////////////////////LISTENERS symbolManager
    /////////////////////////////

    /**
     * Called when an annotation dragging has started.
     *
     * @param annotation the annotation
     */
    @Override
    public void onAnnotationDragStarted(Symbol annotation) {

    }

    /**
     * Called when an annotation dragging is in progress.
     *
     * @param annotation the annotation
     */
    @Override
    public void onAnnotationDrag(Symbol annotation) {

    }

    /**
     * Called when an annotation dragging has finished.
     *
     * @param annotation the annotation
     */
    @Override
    public void onAnnotationDragFinished(Symbol annotation) {
        LatLng latLng = annotation.getLatLng();
        double distance = 0;
        try {

            if (myDestinationTempSymbol.getOriginId() == annotation.getId()) {

                distance = latLng.distanceTo(this.myDestinationTemp.getOriginLatLng());
                updateOrigin(latLng.getLatitude(), latLng.getLongitude());
                setName(true);

            }
            if (myDestinationTempSymbol.getDestinationId() == annotation.getId()) {
                distance = latLng.distanceTo(this.myDestinationTemp.getDestinationLatLng());
                updateDestination(latLng.getLatitude(), latLng.getLongitude());
                setName(false);
            }

            if (distance >= 100.0) {

                findDestinations();
            }

        } catch (NullPointerException e) {

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public double calculeDistance() {
        LatLng origin = this.myDestinationTemp.getOriginLatLng();
        LatLng destination = this.myDestinationTemp.getDestinationLatLng();
        return origin.distanceTo(destination);

    }

    private void setName(boolean toOrigin) {
        if (toOrigin) {
            String address = getAddressFromLocation(myDestinationTemp.getOrigin());
            myDestinationTemp.setOriginAddress(address);
        } else {
            String address = getAddressFromLocation(myDestinationTemp.getDestination());
            myDestinationTemp.setDestinationAddress(address);
        }
    }


    public void setMessenger(IMessenger messenger) {
        this.messenger = messenger;
    }


    /**
     * Called when a Destination layout has been clicked
     *
     * @param position in list
     * @param view     The view that was clicked.
     */
    @Override
    public void onDestinationClick(int position, View view) {
        Destination destination=this.resultsDestination.get(position);
        if (destinationFoundSymbol == null)
            createDestinationsFoundSymbol();
        this.destinationFoundSymbol.hide();
        this.destinationFoundSymbol.setDestination(destination);
        this.destinationFoundSymbol.print();
        this.destinationFound.postValue(destination);
        this.goLocation(destination.getOrigin());
    }
}
