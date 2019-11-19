package com.example.mrrobot.concurrent.ui.location;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.example.mrrobot.concurrent.Config.MapBox;
import com.example.mrrobot.concurrent.R;
import com.example.mrrobot.concurrent.Services.SocketIO;
import com.example.mrrobot.concurrent.Utils.DestinationSymbol;
import com.example.mrrobot.concurrent.Utils.SymbolPrinter;
import com.example.mrrobot.concurrent.models.Destination;
import com.example.mrrobot.concurrent.models.Localization;
import com.example.mrrobot.concurrent.models.Participant;
import com.example.mrrobot.concurrent.models.User;
import com.example.mrrobot.concurrent.ui.home.DestinationAdapter;
import com.google.gson.JsonObject;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;

import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.style.layers.Layer;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.style.sources.Source;

import static com.mapbox.mapboxsdk.style.layers.Property.NONE;
import static com.mapbox.mapboxsdk.style.layers.Property.VISIBLE;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.visibility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static android.os.Looper.getMainLooper;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconSize;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconTextFit;

public class LocationViewModel extends AndroidViewModel implements
        OnMapReadyCallback,
        LocationEngineCallback<LocationEngineResult>,
        Destination.IListener, DestinationAdapter.IEventListener {

    public MapView mapView;
    private MapboxMap mapboxMap;
    private Style style;

    private LocationEngine locationEngine;



    private MutableLiveData<Destination> currentDestination = new MutableLiveData<>();

    private HashMap<String, DestinationSymbol> symbolHashMap = new HashMap<>();
    private SymbolPrinter symbolPrinter;

    //////////////////////////////
    ///////////METHODS
    /////////

    public LocationViewModel(Application application) {
        super(application);

    }


    public void setMapView(MapView mapView, Bundle savedInstanceState) {

        this.mapView = mapView;

        this.mapView.onCreate(savedInstanceState);
        this.mapView.getMapAsync(this);
    }

    // this.mapView.getMapAsync(this);
    @Override
    public void onMapReady(MapboxMap mapboxMap) {

        this.mapboxMap = mapboxMap;
        // SET STYLE
        //String themeCurrent = MapBox.style;
        this.mapboxMap.setStyle(MapBox.style, new Style.OnStyleLoaded() {


            @Override
            public void onStyleLoaded(@NonNull Style style) {
                enableLocationComponent();
                enableLocationEngine();
                LocationViewModel.this.style = style;
                configSymbolManager(style);
                //createASymbolLayer(style,"layerUno","casa",getApplication().getApplicationContext());
                //createPoint(style);

            }
        });
    }


    private void configSymbolManager(Style style) {
        this.symbolPrinter = new SymbolPrinter(mapView, mapboxMap);
        //this.symbolPrinter.addDragListener(this);
    }

    private void printDestination(Destination destination) {

        if (existInSymbolHash(destination)) {

            DestinationSymbol destinationSymbol = this.symbolHashMap.get(destination.getId());
            destinationSymbol.print();
        } else {

            DestinationSymbol destinationSymbol = new
                    DestinationSymbol(this.symbolPrinter, destination);
            this.symbolHashMap.put(destination.getId(), destinationSymbol);
            destinationSymbol.print();
        }

    }


    private boolean existInSymbolHash(Destination aDestination) {
        DestinationSymbol destinationSymbol = this.symbolHashMap.get(aDestination.getId());
        return destinationSymbol != null;
    }

    private void printParticipants(Destination destination) {

        if (existLayer(destination.getId())) {
            updateSource(destination);
            setVisibleToLayer(destination.getId());
        } else {
            createASymbolLayer(destination);
        }
    }

    private boolean existLayer(String idLayer) {
        return this.style.getLayer(idLayer) != null;
    }


    private void setVisibleToLayer(String id) {
        Layer layer = this.mapboxMap.getStyle().getLayer(id);
        layer.setProperties(visibility(VISIBLE));
    }

    // -16.429366, -71.545707
    private void createASymbolLayer(Destination destination) {
        String id = destination.getId();

        Source source = createSourceOfDestination(destination);
        this.style.addSource(source);


        SymbolLayer symbolLayer = new SymbolLayer(id, id);
        symbolLayer.withProperties(
                iconColor(destination.getColor()),
                iconImage("marker_15")
        );
        style.addLayer(symbolLayer);
    }

    private Source createSourceOfDestination(Destination destination) {
        FeatureCollection fc = getFeatureCollectionOfDestination(destination);
        Source source = new GeoJsonSource(destination.getId(), fc);
        return source;
    }

    private FeatureCollection getFeatureCollectionOfDestination(Destination destination) {

        List<Feature> features = destination.getFeatures();
        FeatureCollection fc = FeatureCollection.fromFeatures(features);
        return fc;
    }

    public void goToDestination(Destination destination) {

        LatLng latLng = destination.getDestinationLatLng();
        this.mapboxMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
    }


    private void updateSource(Destination destination) {
        String sourceId = destination.getId();
        GeoJsonSource spaceStationSource = this.style.getSourceAs(sourceId);

        if (spaceStationSource != null) {
            FeatureCollection featureCollection = getFeatureCollectionOfDestination(destination);
            spaceStationSource.setGeoJson(featureCollection);
            /*spaceStationSource.setGeoJson(FeatureCollection.fromFeature(
                    Feature.fromGeometry(Point.fromLngLat(position.getLongitude(), position.getLatitude()))
            ));*/
        }
    }
    private void setCurrentDestination(Destination destination){
        this.currentDestination.postValue(destination);
    }
    private void hideDestinationPrevious() {
        Destination previous = this.currentDestination.getValue();
        if (previous != null) {
            String destinationId = previous.getId();
            Layer layer = this.mapboxMap.getStyle().getLayer(destinationId);
            layer.setProperties(visibility(NONE));
        }
    }

    /**
     * Called when a Destination layout has been clicked
     *
     * @param position in list
     * @param view     The view that was clicked.
     */
    @Override
    public void onDestinationClick(int position, View view) {
        User user = User.getCurrentUser();
        Destination destination = user.getMyDestinations().get(position);
        printDestination(destination);
        printParticipants(destination);
        hideDestinationPrevious();
        setCurrentDestination(destination);
    }

    private void onParticipantChangePosition(Destination destination, Participant participant){
        Destination currentDestination=this.currentDestination.getValue();
        if(currentDestination!=null && currentDestination.equals(destination))
            updateSource(destination);
    }

    @Override
    public void emitParticipantChange(Destination destination, Participant participant) {
        onParticipantChangePosition(destination,participant);
    }

    @SuppressLint("MissingPermission")
    private void enableLocationComponent() {
        // Get an instance of the component
        LocationComponent locationComponent = mapboxMap.getLocationComponent();

        // Activate
        Style style = mapboxMap.getStyle();
        locationComponent.activateLocationComponent(getApplication().getApplicationContext(), style);

        // Enable to make component visible
        locationComponent.setLocationComponentEnabled(true);

        // Set the component's camera mode
        locationComponent.setCameraMode(CameraMode.TRACKING);

        // Set the component's render mode
        locationComponent.setRenderMode(RenderMode.COMPASS);
    }

    @SuppressLint("MissingPermission")
    private void enableLocationEngine() {

        int min = 1000 * 60 * 1;// 2 min
        int distance = 5; //metros
        this.locationEngine = LocationEngineProvider.getBestLocationEngine(this.getApplication().getApplicationContext());

        LocationEngineRequest request = new LocationEngineRequest.Builder(min)
                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                .setDisplacement(distance)
                .setFastestInterval(min)
                .setMaxWaitTime(min * 2)
                .build();

        locationEngine.requestLocationUpdates(request, this, getMainLooper());

    }


    // locationEngine.requestLocationUpdates
    @Override
    public void onSuccess(LocationEngineResult result) {
        // Localization logic here

        Location lastLocation = result.getLastLocation();
        if(lastLocation!=null)
            User.getCurrentUser().setLocation(lastLocation);
    }

    // locationEngine.requestLocationUpdates
    @Override
    public void onFailure(@NonNull Exception exception) {

    }


    //LIFECYCLE

    public void onStop() {


        if (locationEngine != null) {
            locationEngine.removeLocationUpdates(this);
        }

        mapView.onStop();
    }

    public static boolean requestLocationPermissions(Context context, Activity activity, PermissionsListener PermissionsListener) {
        if (PermissionsManager.areLocationPermissionsGranted(context)) {
            // Permission sensitive logic called here, such as activating the Maps SDK's LocationComponent to show the device's location
            return true;

        } else {
            PermissionsManager permissionsManager = new PermissionsManager(PermissionsListener);
            permissionsManager.requestLocationPermissions(activity);
        }
        return false;

    }
}

