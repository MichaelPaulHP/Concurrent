package com.example.mrrobot.concurrent;

import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mrrobot.concurrent.lib.SmartFragmentStatePagerAdapter;
import com.example.mrrobot.concurrent.ui.chat.ChatFragment;
import com.example.mrrobot.concurrent.ui.destination.DestinationFragment;
import com.example.mrrobot.concurrent.ui.home.HomeFragment;
import com.example.mrrobot.concurrent.ui.location.LocationViewModel;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapView;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements PermissionsListener ,
        ViewPager.OnPageChangeListener,
        DestinationFragment.DestinationListener {



    final String apiKey ="AIzaSyCE6yWse7ECNMN5q7XRxuQ8ihyU8QuqrdY";
    final String apiKeyMapBox="pk.eyJ1IjoibXJtaWNoYWVsYm90IiwiYSI6ImNqZHpiamNnNzBwMXYycXA5cXh2M2xnZjcifQ.iqfPeoVbpWQcLG8bvf9qzw";

    LocationViewModel locationViewModel;

    TextView textView;

    ViewPager viewPager;
    MyPagerAdapter adapterViewPager;
    Toolbar toolbar;

    DestinationFragment destinationFragment=DestinationFragment.newInstance();
    HomeFragment homeFragment=HomeFragment.newInstance();
    ChatFragment chatFragment=ChatFragment.newInstance();
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
        LocationViewModel.requestLocationPermissions(getApplicationContext(),this,this);
        // associate the activity with a ViewModel
        this.locationViewModel= ViewModelProviders.of(this).get(LocationViewModel.class);
        //

        // MAP VIEW
        MapView mapView = findViewById(R.id.mapView);
        this.locationViewModel.setMapView(mapView ,savedInstanceState);

        // Initialize Places.
        Places.initialize(getApplicationContext(), apiKey);
        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);

        initUI();
    }

    private void initUI(){
        initToolbar();
        // view Pager
        this.viewPager = (ViewPager) findViewById(R.id.viewPager);
        this.adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        this.viewPager.setAdapter(this.adapterViewPager);
        this.viewPager.addOnPageChangeListener(this);
        this.viewPager.setCurrentItem(1);




        this.textView= findViewById(R.id.outputOfPlace);

        this.destinationFragment.setDestinationListener(this);


    }


    private void initToolbar() {
        this.toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        //toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        /*toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });*/

    }

    ////////////////////////
    // GPS Permission Listener


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
    // VIEW PAGER LISTENERS

    // This method will be invoked when a new page becomes selected.
    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }
    // This method will be invoked when the current page is scrolled
    @Override
    public void onPageSelected(int i) {

        CharSequence title= this.adapterViewPager.getPageTitle(i);
        this.toolbar.setTitle(title);
        /*if(i==0 ){
            this.destinationFragment=(DestinationFragment) this.adapterViewPager.getItem(0);
            this.destinationFragment.setDestinationListener(this);
        }*/

    }
    // Called when the scroll state changes:
    // SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING, SCROLL_STATE_SETTLING
    @Override
    public void onPageScrollStateChanged(int i) {

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
    //////////
    // End LIFE CYCLE
    ///////////////////////////////////////////////////

    public class MyPagerAdapter extends SmartFragmentStatePagerAdapter {
        private  int NUM_ITEMS = 3;

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: // Fragment # 0 - This will show FirstFragment
                    //return DestinationFragment.newInstance();
                    return destinationFragment;
                case 1: // Fragment # 0 - This will show FirstFragment different title
                    return homeFragment;
                    //return HomeFragment.newInstance();
                case 2: // Fragment # 1 - This will show SecondFragment
                    //return ChatFragment.newInstance();
                    return chatFragment;
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }

        // Force a refresh of the page when a different fragment is displayed
//        @Override
//        public int getItemPosition(Object object) {
//            // this method will be called for every fragment in the ViewPager
//            if (object instanceof SomePermanantCachedFragment) {
//                return POSITION_UNCHANGED; // don't force a reload
//            } else {
//                // POSITION_NONE means something like: this fragment is no longer valid
//                // triggering the ViewPager to re-build the instance of this fragment.
//                return POSITION_NONE;
//            }
//        }

    }

}
