package com.example.mrrobot.concurrent.ui.destination;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.mrrobot.concurrent.Config.MapBox;
import com.example.mrrobot.concurrent.R;
import com.example.mrrobot.concurrent.Services.SocketIO;
import com.example.mrrobot.concurrent.Utils.DestinationSymbol;
import com.example.mrrobot.concurrent.Utils.IMessenger;
import com.example.mrrobot.concurrent.Utils.SymbolPrinter;
import com.example.mrrobot.concurrent.entityes.DestinationEntity;
import com.example.mrrobot.concurrent.models.Destination;
import com.example.mrrobot.concurrent.models.DestinationData;
import com.example.mrrobot.concurrent.models.User;
import com.example.mrrobot.concurrent.ui.home.DestinationAdapter;
import com.google.android.libraries.places.api.model.Place;
import com.mapbox.api.geocoding.v5.GeocodingCriteria;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.core.exceptions.ServicesException;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.OnSymbolDragListener;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.utils.BitmapUtils;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class DestinationViewModel extends AndroidViewModel
        implements OnMapReadyCallback, DestinationAdapter.IEventListener, OnSymbolDragListener {

    private MapView mapView;
    private MapboxMap mapboxMap;
    private SymbolManager symbolManager;

    private Socket socket;
    private List<Destination> resultsDestination = new ArrayList<>();
    public MutableLiveData<Boolean> hasNewDestination = new MutableLiveData<>();

    private SymbolPrinter symbolPrinter;

    private Destination myDestinationTemp;
    private Location origin;
    private Location destination;

    private Symbol originSymbol;
    private Symbol destinationSymbol;


    public MutableLiveData<Destination> destinationFound = new MutableLiveData<>();
    private DestinationSymbol destinationFoundSymbol;

    private static final String MAKI_ICON_MARKER = "marker-stroked-15";
    private static final String ICON_PLACE = "ic-place";

    private IMessenger messenger;
    private String TAG = "DestinationViewModel";

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
            Bitmap bitmap = BitmapFactory.decodeResource(
                    resources, R.drawable.ic_place);
            Bitmap bitmap1 = BitmapUtils.getBitmapFromDrawable(resources.getDrawable(R.drawable.ic_location_on_black_24dp));
            style.addImage(ICON_PLACE, bitmap1, true);
        }
    }

    private void createMyDestinationTemp() {
        this.origin = getMyLocation();
        if (this.origin != null) {

            this.myDestinationTemp = new Destination(null, User.getCurrentUser().getId());
            this.myDestinationTemp.initMutableLiveData();
            this.myDestinationTemp.setOrigin(this.origin);
        }
    }

    private void printAndGoMyOrigin() {

        printOrigin();
        goLocation(this.origin);
    }

    private Location getMyLocation() {

        Location location = User.getCurrentUser().myLocation.getValue();
        if (location == null) {
            location = this.mapboxMap.getLocationComponent().getLastKnownLocation();
        }
        return location;
    }


    private void printOrigin() {
        int color = Color.BLACK;
        this.originSymbol= this.symbolPrinter.printSymbol(
                this.originSymbol,
                new LatLng(this.origin),
                color,
                ICON_PLACE);
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
            printDestination();
            goLocation(this.destination);

        } else {
            updateOrigin(latitude, longitude);
            printOrigin();
            goLocation(this.origin);
        }

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

    private void printDestination() {

        int color = this.myDestinationTemp.getColor();
        this.symbolPrinter.printSymbol(
                this.destinationSymbol,
                new LatLng(this.destination),
                color,
                ICON_PLACE
        );
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
            this.resultsDestination.clear();
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
        destinationFound = Destination.findDestinationInListById(this.resultsDestination, destination);
        if (destinationFound == null) {
            this.resultsDestination.add(destination);
            this.hasNewDestination.postValue(true);
        }


        //this.destinationAdapter.notifyNewDestinationInserted();
    }


    private void createDestinationsFoundSymbol() {
        this.destinationFoundSymbol =
                new DestinationSymbol(
                        this.symbolPrinter, this.destinationFound.getValue(), ICON_PLACE);
    }

    /**
     * Called when a Destination layout has been clicked of list
     *
     * @param position    in list
     * @param destination is a Destination
     */
    @Override
    public void onDestinationClick(int position, Destination destination) {
        if (destinationFoundSymbol == null)
            createDestinationsFoundSymbol();
        this.destinationFoundSymbol.setDestination(destination);
        this.destinationFoundSymbol.print();
        this.destinationFound.postValue(destination);
    }


    public void OnSubmit() throws JSONException {
        Destination destinationSelected = this.myDestinationTemp;// setDestinationEntity
        if (destinationSelected != null) {
            User current = User.getCurrentUser();
            String idDestination = destinationSelected.getId();
            // is new ?
            if (idDestination == null) {
                // create a new Destination
                //Destination.emitNewDestination(destinationSelected);
                DestinationData.newDestination(destinationSelected);

            } else {
                if (current.isMyDestination(idDestination)) {
                    messenger.OnWarning("You are in this Destination");
                } else {
                    // this user join to destination
                    //Destination.emitJoinToDestination(destinationSelected.getId());
                    DestinationData.addParticipant(destinationSelected.getId(), current.getId());
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


    private void searchPlace(LatLng latLng, final MutableLiveData<Destination> destinationLiveData) {
        try {
            MapboxGeocoding client = MapboxGeocoding.builder()
                    .accessToken(MapBox.token)
                    .query(Point.fromLngLat(latLng.getLongitude(), latLng.getLatitude()))
                    .geocodingTypes(GeocodingCriteria.TYPE_ADDRESS)
                    .build();
            client.enqueueCall(new Callback<GeocodingResponse>() {
                @Override
                public void onResponse(Call<GeocodingResponse> call, Response<GeocodingResponse> response) {
                    List<CarmenFeature> results = response.body().features();
                    Log.d(TAG, "onResponse " + results.size());
                    if (results.size() > 0) {
                        CarmenFeature feature = results.get(0);
                        String name = feature.placeName();
                        Log.d(TAG, "onResponse " + name);
                        Destination destination = destinationLiveData.getValue();
                        destination.setName(name);
                        destinationLiveData.postValue(destination);
                    } else {

                    }
                }

                @Override
                public void onFailure(Call<GeocodingResponse> call, Throwable t) {

                    Log.d(TAG, "onFailure " + t.getMessage());
                }
            });


        } catch (ServicesException servicesException) {
            Log.e(TAG, "Error geocoding: %s" + servicesException.toString());

            servicesException.printStackTrace();
        }
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
            if (this.originSymbol.getId() == annotation.getId()) {

                distance = latLng.distanceTo(this.myDestinationTemp.getOriginLatLng());
                this.myDestinationTemp.setOrigin(latLng);
                setName(true);

            }
            if (this.destinationSymbol.getId() == annotation.getId()) {
                distance = latLng.distanceTo(this.myDestinationTemp.getDestinationLatLng());
                this.myDestinationTemp.setDestination(latLng);
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

    private void setName(boolean toOrigin) {
        if (toOrigin) {
            String address = getAddressFromLocation(myDestinationTemp.getOrigin());
            myDestinationTemp.setOriginAddress(address);
        } else {
            String address = getAddressFromLocation(myDestinationTemp.getDestination());
            myDestinationTemp.setDestinationAddress(address);
        }
    }


    private void setName(MutableLiveData<Destination> destinationMlv, String name) {
        Destination destination = destinationMlv.getValue();
        destination.setName(name);
        destinationMlv.postValue(destination);
    }

    public void setMessenger(IMessenger messenger) {
        this.messenger = messenger;
    }

}
