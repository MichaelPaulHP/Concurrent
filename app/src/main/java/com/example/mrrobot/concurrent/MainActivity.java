package com.example.mrrobot.concurrent;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.mrrobot.concurrent.Firebase.Auth;
import com.example.mrrobot.concurrent.Services.SocketIO;
import com.example.mrrobot.concurrent.lib.SmartFragmentStatePagerAdapter;
import com.example.mrrobot.concurrent.models.Destination;
import com.example.mrrobot.concurrent.models.Localization;
import com.example.mrrobot.concurrent.models.User;
import com.example.mrrobot.concurrent.ui.chat.DialogsActivity;
import com.example.mrrobot.concurrent.ui.destination.DestinationActivity;
import com.example.mrrobot.concurrent.ui.destination.DestinationFragment;
import com.example.mrrobot.concurrent.ui.destination.DestinationViewModel;
import com.example.mrrobot.concurrent.ui.home.DestinationAdapter;
import com.example.mrrobot.concurrent.ui.home.HomeViewModel;
import com.example.mrrobot.concurrent.ui.location.LocationViewModel;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapView;

import java.util.List;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity
        implements PermissionsListener,
        View.OnClickListener {


    final String apiKey = "AIzaSyCE6yWse7ECNMN5q7XRxuQ8ihyU8QuqrdY";
    final String apiKeyMapBox = "pk.eyJ1IjoibXJtaWNoYWVsYm90IiwiYSI6ImNqZHpiamNnNzBwMXYycXA5cXh2M2xnZjcifQ.iqfPeoVbpWQcLG8bvf9qzw";

    LocationViewModel locationViewModel;
    HomeViewModel homeViewModel;


    private RecyclerView recyclerViewListDestinations;
    DestinationAdapter destinationAdapter;

    private ProgressBar progressBar;

    //////////////////////////////////////////////
    //////////////////////////// METHODS
    /////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // initialize MapBox
        Mapbox.getInstance(this, apiKeyMapBox);
        // set content
        setContentView(R.layout.activity_main);
        // request GPS
        LocationViewModel.requestLocationPermissions(getApplicationContext(), this, this);
        // associate the activity with a ViewModel
        this.locationViewModel = ViewModelProviders.of(this).get(LocationViewModel.class);
        this.homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);

        //

        // MAP VIEW
        MapView mapView = findViewById(R.id.mapView);
        this.locationViewModel.setMapView(mapView, savedInstanceState);

        // Initialize Places.
        Places.initialize(getApplicationContext(), apiKey);
        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);

        initUI();
    }

    private void initUI() {

        findViewById(R.id.optionsTop).bringToFront();
        findViewById(R.id.optionsBot).bringToFront();
        this.progressBar = (ProgressBar) findViewById(R.id.mainProgressBar);

        findViewById(R.id.btnTest).setOnClickListener(this);

        findViewById(R.id.btnChats).setOnClickListener(this);
        findViewById(R.id.btnLogOut).setOnClickListener(this);
        findViewById(R.id.btnFormLocation).setOnClickListener(this);
        initRecyclerViewOfDestinations();


        subscribeToHasNewDestination();
        subscribeConnectTask();
    }



    private void subscribeToHasNewDestination() {

        final Observer<Destination> newDestinationObserver = new Observer<Destination>() {
            @Override
            public void onChanged(@Nullable final Destination destination) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MainActivity.this.destinationAdapter.notifyNewDestinationInserted();
                        //destination.setDestinationListener(locationViewModel);
                    }
                });

            }
        };
        User.getCurrentUser().hasNewDestination.observe(this, newDestinationObserver);

    }

    private void subscribeConnectTask() {

        final Observer<Boolean> observer = new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable final Boolean isConnected) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showProgressBar(!isConnected);
                    }
                });

            }
        };

        SocketIO.isConnected.observe(this, observer);

    }


    private void initRecyclerViewOfDestinations() {
        this.recyclerViewListDestinations = findViewById(R.id.recyclerViewListDestinations);
        this.recyclerViewListDestinations.
                setLayoutManager(
                        new LinearLayoutManager(
                                getApplicationContext(),
                                LinearLayoutManager.HORIZONTAL,
                                false));
        destinationAdapter = new DestinationAdapter();
        destinationAdapter.setEventListener(this.locationViewModel);
        destinationAdapter.setDestinations(User.getCurrentUser().getMyDestinations());
        this.recyclerViewListDestinations.setAdapter(destinationAdapter);


    }

    private void showProgressBar(Boolean show) {

        this.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btnChats:
                startActivity(new Intent(getApplicationContext(), DialogsActivity.class));
                break;
            case R.id.btnFormLocation:
                startActivity(new Intent(getApplicationContext(), DestinationActivity.class));
                //showDialogTheme();
                break;
            case R.id.btnLogOut:
                Auth.getInstance().signOut();
                break;
            case R.id.btnTest:
                test();
                break;
        }
    }

    private void test() {
        //User.getCurrentUser().requestMyDestinations();
    }

    public void showDialogTheme() {
        /*DialogFragment themeDialogFragment = new DestinationFragment();
        themeDialogFragment.show(getFragmentManager(), "DialogFragmentFragment");*/

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("QWEQW");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        //this.destinationFragment.show(ft,"QWEQW");
    }

    ////////////////////////
    // GPS Permission Listener


    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

    }

    @Override
    public void onPermissionResult(boolean granted) {

    }


    //////////////////////
    // LIFE CYCLE
    //////


    @Override
    protected void onRestart() {
        super.onRestart();
        Timber.d("onRestart");
    }

    @Override
    public void onStart() {
        super.onStart();
        this.locationViewModel.mapView.onStart();
        this.destinationAdapter.notifyNewDestinationInserted();
        Timber.d("onStart");

    }

    @Override
    public void onResume() {
        super.onResume();
        this.locationViewModel.mapView.onResume();
        Timber.d("onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        this.locationViewModel.mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        this.locationViewModel.onStop();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        this.locationViewModel.mapView.onDestroy();

        SocketIO.getSocket().disconnect();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        this.locationViewModel.mapView.onSaveInstanceState(outState);
    }

    //////////
    // End LIFE CYCLE
    ///////////////////////////////////////////////////


}
