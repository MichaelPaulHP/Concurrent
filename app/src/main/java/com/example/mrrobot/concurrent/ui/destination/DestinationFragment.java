package com.example.mrrobot.concurrent.ui.destination;

import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mrrobot.concurrent.R;
import com.example.mrrobot.concurrent.Services.SocketIO;
import com.example.mrrobot.concurrent.databinding.DestinationBinding;
import com.example.mrrobot.concurrent.databinding.DestinationFragmentBinding;
import com.example.mrrobot.concurrent.models.Destination;
import com.example.mrrobot.concurrent.ui.location.LocationViewModel;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.mapbox.mapboxsdk.maps.MapView;

import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class DestinationFragment extends DialogFragment implements View.OnClickListener {
    String TAG = "autocompleteDestination";
    int AUTOCOMPLETE_REQUEST_CODE = 66;

    private DestinationViewModel destinationViewModel;
    DestinationListener destinationListener;
    RecyclerView recyclerViewDestinationsFound;

    private DestinationFragmentBinding binding;

    public DestinationFragment() {
        // Required empty public constructor
    }

    public static DestinationFragment newInstance() {
        return new DestinationFragment();
    }

    @Override
    public void onStart() {
        super.onStart();
        /*Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }*/
        Log.i(TAG, "onStart");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
        //Log.i(TAG, "onCreate");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // DATA BINDING
        binding =
                DataBindingUtil.inflate(inflater,R.layout.destination_fragment, container, false);

        // TO VIEW MODEL
        destinationViewModel = ViewModelProviders.of(this).get(DestinationViewModel.class);

        binding.setDestinationVM(destinationViewModel);
        binding.btnSearchPlace.setOnClickListener(this);
        binding.btnSubmitDestination.setOnClickListener(this);
        this.recyclerViewDestinationsFound = binding.recyclerViewDestinationsFound;


        return binding.getRoot();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setCancelable(true);
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.TOP);

        return dialog;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // view models
        //destinationViewModel = ViewModelProviders.of(this).get(DestinationViewModel.class);

        initRecyclerViewOfDestinations();
    }

    private void initRecyclerViewOfDestinations() {

        //this.recyclerViewDestinationsFound = findViewById(R.id.recyclerViewListDestinations);
        this.recyclerViewDestinationsFound.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        this.recyclerViewDestinationsFound.setAdapter(this.destinationViewModel.destinationAdapter);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
                this.destinationViewModel.onPlaceSelected(place);
                //DestinationFragment.this.destinationListener.onPlaceSelected(place);


            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    private void showSearchPlace() {
        List<Place.Field> fields = Arrays.asList(Place.Field.LAT_LNG, Place.Field.NAME);
        // Start the autocomplete intent.

        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY, fields)
                .setCountry("PE")
                .build(getContext());

        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);

    }

    /*private void initializeAutocompleteSupportFragment() {
        if(autocompleteFragment==null) {
            autocompleteFragment= (AutocompleteSupportFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
            //autocompleteFragment = (AutocompleteSupportFragment)getActivity().
            //        getFragmentManager().findFragmentById(R.id.autocomplete_fragment);
            //AutocompleteSupportFragment autocompleteFragment1  = (AutocompleteSupportFragment)getActivity().getFragmentManager().findFragmentById(R.id.autocomplete_fragment);
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

    }*/
    private void onSubmit() {
        Destination destination= destinationViewModel.getDestinationSelected();
        if( destination!=null) {
            this.destinationListener.onDestinationSelected(destination);
            dismiss();
        }
    }

    public void setDestinationListener(DestinationListener iDestinationFragment) {
        this.destinationListener = iDestinationFragment;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btnSearchPlace:
                showSearchPlace();
                break;
            case R.id.btnSubmitDestination:
                onSubmit();
                break;

        }
    }
    // LIFE CYCLE

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        Log.i(TAG, "onDestroyView");
        /*if (this.autocompleteFragment != null)
            getFragmentManager().beginTransaction().remove(this.autocompleteFragment).commit();*/

    }

    @Override
    public void onDestroy() {
        super.onDestroy();


        Log.i(TAG, "onDestroy");
    }

    // INTERFACE
    public interface DestinationListener {

        void onDestinationSelected(Destination destination);
    }

}
