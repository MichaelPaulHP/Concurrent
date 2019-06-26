package com.example.mrrobot.concurrent.ui.location;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.example.mrrobot.concurrent.Config.MapBox;
import com.example.mrrobot.concurrent.MainActivity;
import com.example.mrrobot.concurrent.R;
import com.example.mrrobot.concurrent.Services.SocketIO;
import com.example.mrrobot.concurrent.models.Localization;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static android.os.Looper.getMainLooper;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconSize;

public class LocationViewModel extends AndroidViewModel implements
        OnMapReadyCallback,
        LocationEngineCallback<LocationEngineResult> {

    public MapView mapView;
    static private MapboxMap mapboxMap;
    private LocationEngine locationEngine;
    Socket socketIO;
    private LatLng currentPosition = new LatLng(-16.4273742, -71.5431796);


    //////////////////////////////
    ///////////METHODS
    /////////

    public LocationViewModel(Application application) {
        super(application);
        this.socketIO = SocketIO.getSocket();

    }
    public static MapboxMap getMapBox(){
        return mapboxMap;
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

                //createPoint(style);

            }
        });
    }

    /*private void createPoint(Style style) {

        Resources resources = getApplication().getApplicationContext().getResources();
        if (resources == null) {
            Toast.makeText(getApplication().getApplicationContext(), "null Resources", Toast.LENGTH_LONG).show();
        } else {
            //Bitmap bitmap = BitmapFactory.decodeResource(this.context.getResources(), R.drawable.ic_location_on_black_24dp);
            //style.addImage("location_icon", bitmap);
            style.addImage("marker-icon-id",
                    BitmapFactory.decodeResource(
                            resources, R.drawable.mapbox_marker_icon_default));
            geoJsonSource = new GeoJsonSource("source-id",
                    Feature.fromGeometry(Point.fromLngLat(-71.5370, -16.3989
                    )));

            style.addSource(geoJsonSource);

            style.addLayer(new SymbolLayer("layer-id", "source-id")
                    .withProperties(
                            PropertyFactory.iconImage("location_icon"),
                            PropertyFactory.iconIgnorePlacement(true),
                            PropertyFactory.iconAllowOverlap(true)
                    ));

        }
    }*/
    public static void initSymbolLayer(@NonNull Style style,String layer,String source,Context context) {
        Resources resources = context.getResources();
        if(resources!=null) {
            style.addImage("point",
                    BitmapFactory.decodeResource(
                            resources, R.drawable.ic_location_on_black_24dp));

            style.addSource(new GeoJsonSource(source));

            style.addLayer(new SymbolLayer(layer, source).withProperties(
                    iconImage("point"),
                    iconIgnorePlacement(true),
                    iconAllowOverlap(true),
                    iconSize(.5f)
            ));
        }
    }

    Emitter.Listener onLocalizationChange = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            LatLng latLng;
            String sourceUserID;
            try {
                JSONObject data = (JSONObject) args[0];
                sourceUserID = data.getString("userID");
                String latitude = data.getString("latitude ");
                String longitude = data.getString("longitude ");
                latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));

            } catch (JSONException e) {
                return;
            }
            updateMarkerPosition(latLng,sourceUserID);
        }
    };

    private void updateMarkerPosition(LatLng position,String source) {
        // This method is were we update the marker position once we have new coordinates. First we
        // check if this is the first time we are executing this handler, the best way to do this is
        // check if marker is null;
        if (this.mapboxMap.getStyle()!= null && position!=null && source!=null) {
            GeoJsonSource  spaceStationSource = this.mapboxMap.getStyle().getSourceAs(source);

            if (spaceStationSource != null) {
                spaceStationSource.setGeoJson(FeatureCollection.fromFeature(
                        Feature.fromGeometry(Point.fromLngLat(position.getLongitude(), position.getLatitude()))
                ));
            }
        }

        // Lastly, animate the camera to the new position so the user
        // wont have to search for the marker and then return.
        //map.animateCamera(CameraUpdateFactory.newLatLng(position));
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
        Localization localization = new Localization("",lastLocation.getLatitude(),lastLocation.getLongitude());
        SocketIO.emitMyLocalizationChange(localization);
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

