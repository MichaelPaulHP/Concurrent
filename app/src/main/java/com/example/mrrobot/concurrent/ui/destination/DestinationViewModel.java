package com.example.mrrobot.concurrent.ui.destination;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.content.res.Resources;
import android.databinding.ObservableField;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.mrrobot.concurrent.Config.MapBox;
import com.example.mrrobot.concurrent.R;
import com.example.mrrobot.concurrent.Services.SocketIO;
import com.example.mrrobot.concurrent.models.Destination;
import com.example.mrrobot.concurrent.models.User;
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
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.mapbox.mapboxsdk.utils.BitmapUtils;
import com.mapbox.mapboxsdk.utils.ColorUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DestinationViewModel extends AndroidViewModel
        implements OnMapReadyCallback,Destination.IDestinationListener, OnSymbolDragListener {

    private MapView mapView;
    private MapboxMap mapboxMap;
    private SymbolManager symbolManager;

    private Socket socket;
    private List<Destination> resultsDestination = new ArrayList<>();
    public MutableLiveData<Boolean> hasNewDestination = new MutableLiveData<>();



    public ObservableField<Destination> destinationObservableField = new ObservableField<>();

    public MutableLiveData<Destination> myDestination= new MutableLiveData<>();
    public MutableLiveData<Destination> myOrigin= new MutableLiveData<>();
    private Symbol originSymbol;
    private Symbol destinationSymbol;


    private static final String MAKI_ICON_MARKER = "marker-stroked-15";
    private static final String ICON_PLACE = "ic-place";

    private String TAG="DestinationViewModel";
    /*---------------------------------------------------------------------*/
    public DestinationViewModel(Application application) {
        super(application);

        this.socket = SocketIO.getSocket();
        this.socket.on("destinationsFound", onDestinationFound);
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
                printAndGoMyLocation();


            }
        });
    }
    private void configSymbolManager(Style style){
        DestinationViewModel.this.symbolManager = new SymbolManager(mapView, mapboxMap, style);
        symbolManager.setIconAllowOverlap(true);
        symbolManager.setTextAllowOverlap(true);
        //symbolManager.addClickListener
        //symbolManager.addLongClickListener
        //symbolManager.addDragListener
        symbolManager.addDragListener(this);

    }
    private void addIcon(Style style){
        Resources resources = getApplication().getApplicationContext().getResources();
        if(resources!=null) {
            Bitmap bitmap = BitmapFactory.decodeResource(
                    resources, R.drawable.ic_place);
            Bitmap bitmap1= BitmapUtils.getBitmapFromDrawable(resources.getDrawable(R.drawable.ic_location_on_black_24dp));
            style.addImage(ICON_PLACE, bitmap1,true);
        }
    }

    private void printAndGoMyLocation(){

        Location location = User.getCurrentUser().myLocation.getValue();
        if(location!=null) {
            Double lat = location.getLatitude();
            Double lon = location.getLongitude();
            LatLng latLng = new LatLng(lat, lon);
            Destination destination = createTempDestination(" origin",latLng);
            this.myOrigin.postValue(destination);
            int color = Color.BLACK;
            this.originSymbol = createSymbol(latLng,color);
            goLatLng(latLng);
            getAddressFromLatLng(latLng,myOrigin);
        }
    }

    public void onPlaceSelected(Place place,boolean isMyDestination) {
        Double latitude=place.getLatLng().latitude;
        Double longitude=place.getLatLng().longitude;
        String name= place.getName();
        LatLng latLng= new LatLng(latitude,longitude);
        Destination destination = createTempDestination(name,latLng);
        if(isMyDestination){
            int color=destination.getColor();
            if(this.destinationSymbol==null){

                this.destinationSymbol=createSymbol(latLng,color);
            }
            else{
                this.destinationSymbol.setLatLng(latLng);
                this.destinationSymbol.setIconColor(color);
                this.symbolManager.update(this.destinationSymbol);
            }
            Destination.emitFindDestinations(destination);// response SERVE onDestinationFound
            this.myDestination.postValue(destination);
        }
        else{
            //createSymbol(latLng,"Origin");
            this.originSymbol.setLatLng(latLng);
            this.symbolManager.update(this.originSymbol);
            this.myOrigin.postValue(destination);
        }
        goLatLng(latLng);
    }
    public  void goLatLng(LatLng latLng) {

        Double zoom=this.mapboxMap.getCameraPosition().zoom;
        //this.mapboxMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                new CameraPosition.Builder()
                        .target(latLng)
                        .zoom(zoom)
                        .build()), 1000);
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
    private Symbol createSymbol(LatLng latLng,int iconColor){
        Symbol symbol;
        symbol = symbolManager.create(new SymbolOptions()
                .withLatLng(latLng)
                .withIconColor(ColorUtils.colorToRgbaString(iconColor))
                .withIconImage(ICON_PLACE)
                .withIconSize(2.0f)
                .withDraggable(true));
        return symbol;
    }

    private Destination createTempDestination(String name,LatLng latLng ) {

        Destination destination = new Destination();
        destination.setName(name);
        destination.setLocalization(latLng);
        destination.setNumUsers(0);
        destination.setDestinationListener(this);
        return destination;
    }

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

    private void searchPlace(LatLng latLng, final MutableLiveData<Destination> destinationLiveData) {
        try {
            MapboxGeocoding client = MapboxGeocoding.builder()
                    .accessToken(MapBox.token)
                    .query(Point.fromLngLat(latLng.getLongitude(),latLng.getLatitude()))
                    .geocodingTypes(GeocodingCriteria.TYPE_ADDRESS)
                    .build();
            client.enqueueCall(new Callback<GeocodingResponse>() {
                @Override
                public void onResponse(Call<GeocodingResponse> call, Response<GeocodingResponse> response) {
                    List<CarmenFeature> results = response.body().features();
                    Log.d(TAG,"onResponse "+results.size());
                    if (results.size() > 0) {
                        CarmenFeature feature = results.get(0);
                        String name= feature.placeName();
                        Log.d(TAG,"onResponse "+name);
                        Destination destination = destinationLiveData.getValue();
                        destination.setName(name);
                        destinationLiveData.postValue(destination);
                    } else {

                    }
                }

                @Override
                public void onFailure(Call<GeocodingResponse> call, Throwable t) {

                    Log.d(TAG,"onFailure "+t.getMessage());
                }
            });


        } catch (ServicesException servicesException) {
            Log.e(TAG,"Error geocoding: %s"+ servicesException.toString());

            servicesException.printStackTrace();
        }
    }

    private  void getAddressFromLatLng(final LatLng latLng, final MutableLiveData<Destination> destinationLiveData) {
        final Double latitude=latLng.getLatitude();
        final Double longitude=latLng.getLongitude();

        Thread thread = new Thread() {
            @Override
            public void run() {
                Geocoder geocoder = new Geocoder(getApplication().getApplicationContext(), Locale.getDefault());
                String result = null;
                try {
                    List <Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
                    if (addressList != null && addressList.size() > 0) {
                        Address address = addressList.get(0);
                        String name=address.getAddressLine(0);
                        Destination destination =destinationLiveData.getValue();
                        destination.setLocalization(latLng);
                        destination.setName(name);
                        destinationLiveData.postValue(destination);

                    }
                    else{
                        Log.e(TAG, "empty or null");
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Unable connect to Geocoder", e);
                }

            }
        };
        thread.start();
    }
    /*
    LISTENERS symbolManager
     */

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

        try {
            if(this.originSymbol.getId()==annotation.getId()){
                getAddressFromLatLng(latLng,this.myOrigin);
                return;
            }
            if(this.destinationSymbol.getId()==annotation.getId()){
                getAddressFromLatLng(latLng,this.myDestination);
            }
        }catch (NullPointerException e){

        }




    }
}
