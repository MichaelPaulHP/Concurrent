package com.example.mrrobot.concurrent;

import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mrrobot.concurrent.ui.destination.DestinationFragment;
import com.example.mrrobot.concurrent.ui.location.LocationViewModel;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapView;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements PermissionsListener , DestinationFragment.DestinationListener {

    final String apiKey ="AIzaSyCE6yWse7ECNMN5q7XRxuQ8ihyU8QuqrdY";
    final String apiKeyMapBox="pk.eyJ1IjoibXJtaWNoYWVsYm90IiwiYSI6ImNqZHpiamNnNzBwMXYycXA5cXh2M2xnZjcifQ.iqfPeoVbpWQcLG8bvf9qzw";
    FragmentManager fragmentManager;
    LocationViewModel locationViewModel;

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // initialize MapBox
        Mapbox.getInstance(this, apiKeyMapBox);
        // set content
        setContentView(R.layout.activity_main);
        // request GPS
        LocationViewModel.requestLocationPermissions(getApplicationContext(),this,this);
        // associate the activity with a ViewModel
        this.locationViewModel= ViewModelProviders.of(this).get(LocationViewModel.class);
        //https://img-comment-fun.9cache.com/media/aqL0mzR/alznLYRZ_700w_0.jpg

        MapView mapView = findViewById(R.id.mapView);

        this.locationViewModel.setMapView(mapView ,savedInstanceState);

        // Initialize Places.
        Places.initialize(getApplicationContext(), apiKey);
        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);

        initUI();
    }

    private void initUI(){
        this.fragmentManager=getSupportFragmentManager();
        initToolbar();
        DestinationFragment destinationFragment =new DestinationFragment();
        destinationFragment.setDestinationListener(this);
        addMainFragment(destinationFragment, true, R.id.container);

        this.textView= findViewById(R.id.outputOfPlace);



    }


    private void initToolbar() {
        Toolbar mToolbar = findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }
    protected void addMainFragment(Fragment fragment, boolean addToBackStack, int containerId) {
        invalidateOptionsMenu();
        String backStackName = fragment.getClass().getName();
        boolean fragmentPopped = fragmentManager.popBackStackImmediate(backStackName, 0);
        if (!fragmentPopped) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(containerId, fragment, backStackName)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            if (addToBackStack)
                transaction.addToBackStack(backStackName);
            transaction.commit();
        }
    }


    // Permion Listener


    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

    }

    @Override
    public void onPermissionResult(boolean granted) {

    }
    // IDestinationFragment

    @Override
    public void onPlaceSelected(Place place) {
        String str =place.getName()+" "+place.getLatLng();
        this.textView.setText(str);
    }

    //////////////////////
    // LIFE CYCLE
    //////
    @Override
    public void onStart() {
        super.onStart();
        this.locationViewModel.mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        this.locationViewModel.mapView.onResume();
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
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        this.locationViewModel.mapView.onSaveInstanceState(outState);
    }

}
