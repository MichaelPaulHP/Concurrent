package com.example.mrrobot.concurrent.ui.destination;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mrrobot.concurrent.R;
import com.example.mrrobot.concurrent.ui.location.LocationViewModel;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.mapbox.mapboxsdk.maps.MapView;

import java.util.Arrays;

public class DestinationFragment extends Fragment {
    String TAG = "autocompleteDestination";
    private DestinationViewModel mViewModel;
    DestinationListener destinationListener;


    public static DestinationFragment newInstance() {
        return new DestinationFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.destination_fragment, container, false);
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // view models
        mViewModel = ViewModelProviders.of(this).get(DestinationViewModel.class);


    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        // Initialize the AutocompleteSupportFragment
        initializeAutocompleteSupportFragment();
    }
    private void initializeAutocompleteSupportFragment() {
        //mapFragment =  getChildFragmentManager().findFragmentById(R.id.map2);
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);


        //autocompleteFragment.setTypeFilter(TypeFilter.ADDRESS);
        autocompleteFragment.setCountry("PE");

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.LAT_LNG, Place.Field.NAME));
        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getLatLng());
                DestinationFragment.this.destinationListener.onPlaceSelected(place);
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }

        });


    }

    public void setDestinationListener(DestinationListener iDestinationFragment) {
        this.destinationListener = iDestinationFragment;
    }

    // LIFE CYCLE



    // INTERFACE
    public interface DestinationListener{

        void onPlaceSelected(Place place);
    }

}
