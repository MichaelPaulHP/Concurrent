package com.example.mrrobot.concurrent.ui.location;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.example.mrrobot.concurrent.Config.MapBox;
import com.example.mrrobot.concurrent.R;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;

import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.util.List;

import static android.os.Looper.getMainLooper;

public class LocationViewModel extends ViewModel implements
        OnMapReadyCallback,
        LocationEngineCallback<LocationEngineResult> {

    public MapView mapView;
    private Context context;
    private MapboxMap mapboxMap;
    private PermissionsManager permissionsManager;
    private Location location;
    private MutableLiveData<Location> locationLiveData = new MutableLiveData<>();
    private LocationEngine locationEngine;
    private GeoJsonSource geoJsonSource;

    private LatLng currentPosition = new LatLng(-16.4273742, -71.5431796);

    public LocationViewModel() {


    }

    public static boolean requestLocationPermissions(Context context,Activity activity,PermissionsListener PermissionsListener){
        if (PermissionsManager.areLocationPermissionsGranted(context)) {
            // Permission sensitive logic called here, such as activating the Maps SDK's LocationComponent to show the device's location
            return true;

        } else {
            PermissionsManager permissionsManager = new PermissionsManager(PermissionsListener);
            permissionsManager.requestLocationPermissions(activity);
        }
        return false;

    }


    public void setMapView(MapView mapView, Bundle savedInstanceState) {

        this.mapView = mapView;
        this.context = this.mapView.getContext();
        this.mapView.onCreate(savedInstanceState);
        this.mapView.getMapAsync(this);
    }

    // this.mapView.getMapAsync(this);
    @Override
    public void onMapReady(MapboxMap mapboxMap) {

        this.mapboxMap = mapboxMap;
        // SET STYLE
        //String themeCurrent = MapBox.style;
        this.mapboxMap.setStyle(Style.LIGHT, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                enableLocationComponent();
                enableLocationEngine();

                //createPoint(style);

            }
        });
    }

    @SuppressLint("MissingPermission")
    private void enableLocationComponent() {
        // Get an instance of the component
        LocationComponent locationComponent = mapboxMap.getLocationComponent();


        // Activate
        Style style = mapboxMap.getStyle();
        locationComponent.activateLocationComponent(context, style);

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
        this.locationEngine = LocationEngineProvider.getBestLocationEngine(this.context);

        LocationEngineRequest request = new LocationEngineRequest.Builder(min)
                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                .setDisplacement(distance)
                .setFastestInterval(min)
                .setMaxWaitTime(min * 2)
                .build();

        locationEngine.requestLocationUpdates(request, this, getMainLooper());


    }

    private void createPoint(Style style){

        Bitmap bitmap= BitmapFactory.decodeResource(this.context.getResources(),R.drawable.ic_location_on_black_24dp);
        style.addImage("location_icon",bitmap);

        geoJsonSource = new GeoJsonSource("source-id",
                Feature.fromGeometry(Point.fromLngLat(currentPosition.getLongitude(),
                        currentPosition.getLatitude())));
        style.addSource(geoJsonSource);

        style.addLayer(new SymbolLayer("layer-id", "source-id")
                .withProperties(
                        PropertyFactory.iconImage("location_icon"),
                        PropertyFactory.iconIgnorePlacement(true),
                        PropertyFactory.iconAllowOverlap(true)
                ));


    }

    // locationEngine.requestLocationUpdates
    @Override
    public void onSuccess(LocationEngineResult result) {
        // Localization logic here

        Location lastLocation = result.getLastLocation();
        this.locationLiveData.postValue(lastLocation);

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
}

