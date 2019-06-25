package com.example.mrrobot.concurrent.ui.destination;

import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
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

public class DestinationFragment extends DialogFragment {
    String TAG = "autocompleteDestination";
    private DestinationViewModel mViewModel;
    DestinationListener destinationListener;
    AutocompleteSupportFragment autocompleteFragment;

    public DestinationFragment(){
        // Required empty public constructor
    }

    public static DestinationFragment newInstance() {
        return new DestinationFragment();
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width,height);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.destination_fragment, container, false);
        initializeAutocompleteSupportFragment();
        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog= super.onCreateDialog(savedInstanceState);
        dialog.setCancelable(true);
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.TOP);
        return dialog;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // view models
        //mViewModel = ViewModelProviders.of(this).get(DestinationViewModel.class);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        // Initialize the AutocompleteSupportFragment



    }
    private void initializeAutocompleteSupportFragment() {
        if(autocompleteFragment==null) {
            autocompleteFragment = (AutocompleteSupportFragment)
                    getFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        }
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
    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (this.autocompleteFragment != null)
            getFragmentManager().beginTransaction().remove(this.autocompleteFragment).commit();

    }


    // INTERFACE
    public interface DestinationListener{

        void onPlaceSelected(Place place);
    }

}
