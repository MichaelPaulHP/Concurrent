package com.example.mrrobot.concurrent.ui.destination;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewStubProxy;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewStub;

import com.example.mrrobot.concurrent.R;
import com.example.mrrobot.concurrent.databinding.ActivityDestinationBinding;
import com.example.mrrobot.concurrent.databinding.DestinationBinding;
import com.example.mrrobot.concurrent.models.Destination;
import com.example.mrrobot.concurrent.ui.home.DestinationAdapter;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;

import java.util.Arrays;
import java.util.List;

public class DestinationActivity extends AppCompatActivity implements View.OnClickListener, View.OnDragListener {


    String TAG = "DestinationActivity";
    int AUTOCOMPLETE_REQUEST_CODE = 66;


    private DestinationViewModel destinationViewModel;
    private ActivityDestinationBinding binding;

    private RecyclerView recyclerViewDestinationsFound;
    public DestinationAdapter destinationAdapter;
    private BottomSheetBehavior sheetBehavior;
    private MapView mapView;


    ///////////////////////////////////////////////////////
    /////////////// METHODS
    /////////////////////////////////////////////


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_destination);
        // TO VIEW MODEL
        destinationViewModel = ViewModelProviders.of(this).get(DestinationViewModel.class);
        binding.setDestinationVM(destinationViewModel);
        // MAP VIEW
        this.mapView = findViewById(R.id.mapViewDestination);
        this.destinationViewModel.initMapView(mapView, savedInstanceState);

        initUI();
    }

    private void initUI() {
        initBottomSheet();
        this.binding.layoutSearch.bringToFront();

        this.binding.btnMyOrigin.setOnClickListener(this);
        this.binding.btnMyDestination.setOnClickListener(this);
        this.binding.btnFindMyDestination.setOnClickListener(this);
        this.binding.btnFindMyOrigin.setOnClickListener(this);

        this.binding.btnSubmitDestination.setOnClickListener(this);
        this.recyclerViewDestinationsFound = this.binding.rvDestinationsFound;
        initRecyclerViewOfDestinations();

        subscribeHasNewDestination();
        subscribeDestinationSelected();
        subscribeMyOrigin();

    }
    private void initBottomSheet(){
        sheetBehavior = BottomSheetBehavior.from(this.binding.bottomSheet);
        this.binding.btnBottomSheet.setOnClickListener(this);
        this.binding.btnBottomSheet.setOnDragListener(this);
    }
    public void toggleBottomSheet() {
        if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        } else {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            //btnBottomSheet.setText("Expand sheet");
        }
    }


    private void subscribeHasNewDestination() {
        final Observer<Boolean> booleanObserver = new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable final Boolean aLong) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DestinationActivity.this.destinationAdapter.notifyNewDestinationInserted();

                    }
                });

            }
        };

        this.destinationViewModel.hasNewDestination.observe(this, booleanObserver);
    }

    /*private void intViewStub(){

        ViewStubProxy viewStub= DestinationActivity.this.binding.viewStubDestinationSelected;
        viewStub.setOnInflateListener(new ViewStub.OnInflateListener() {
            @Override
            public void onInflate(ViewStub stub, View inflated) {
                DestinationBinding destinationBinding= DataBindingUtil.bind(inflated);
                Destination destination= DestinationActivity.this.destinationViewModel.myDestination.getValue();
                destinationBinding.setDestination(destination);
            }

        });
    }*/
    private void subscribeDestinationSelected() {
        final Observer<Destination> booleanObserver = new Observer<Destination>() {
            @Override
            public void onChanged(@Nullable final Destination aDestination) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DestinationActivity.this.binding.btnMyDestination.setText(aDestination.getName());
                        DestinationActivity.this.binding.setDestinationSelected(aDestination);
                        //DestinationActivity.this.binding.viewStubDestinationSelected.getViewStub().inflate();

                        //viewStub.getViewStub().inflate();
                    }
                });

            }
        };
        destinationViewModel.myDestination.observe(this,booleanObserver);
        //Destination.destinationSelected.observe(this, booleanObserver);
    }
    private void subscribeMyOrigin() {
        final Observer<Destination> booleanObserver = new Observer<Destination>() {
            @Override
            public void onChanged(@Nullable final Destination aDestination) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DestinationActivity.this.binding.btnMyOrigin.setText(aDestination.getName());
                    }
                });

            }
        };
        destinationViewModel.myOrigin.observe(this,booleanObserver);
        //Destination.destinationSelected.observe(this, booleanObserver);
    }

    private void initRecyclerViewOfDestinations() {

        this.recyclerViewDestinationsFound.setLayoutManager(
                new LinearLayoutManager(getApplicationContext(),
                        LinearLayoutManager.HORIZONTAL, false));

        this.destinationAdapter = new DestinationAdapter();
        this.recyclerViewDestinationsFound.setAdapter(this.destinationAdapter);
        this.destinationAdapter.setDestinations(this.destinationViewModel.getResultsDestination());

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE || (requestCode==AUTOCOMPLETE_REQUEST_CODE+1) ) {
            if (resultCode == RESULT_OK) {

                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());

                Boolean isMyDestination=requestCode==AUTOCOMPLETE_REQUEST_CODE+1;
                this.destinationViewModel.onPlaceSelected(place,isMyDestination);

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

    private void showSearchPlace(int REQUEST_CODE) {
        List<Place.Field> fields = Arrays.asList(Place.Field.LAT_LNG, Place.Field.NAME);
        // Start the autocomplete intent.

        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY, fields)
                .setCountry("PE")
                .build(getApplicationContext());

        startActivityForResult(intent, REQUEST_CODE);
    }

    private void onSubmit() {

        this.destinationViewModel.OnSubmit();
        // close activity
    }
    private void goTo(MutableLiveData<Destination> destinationMutableLiveData){
        Destination destination= destinationMutableLiveData.getValue();
        if(destination!=null) {
            LatLng latLng = destination.getLocalization();
            this.destinationViewModel.goLatLng(latLng);
        }
    }
    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btnFindMyOrigin:
                showSearchPlace(AUTOCOMPLETE_REQUEST_CODE);
                break;
            case R.id.btnFindMyDestination:
                showSearchPlace(AUTOCOMPLETE_REQUEST_CODE+1);
                break;
            case R.id.btnMyOrigin:
                goTo(this.destinationViewModel.myOrigin);
                break;
            case R.id.btnMyDestination:
                goTo(this.destinationViewModel.myDestination);
                break;
            case R.id.btnSubmitDestination:
                onSubmit();
                break;
            case R.id.btnBottomSheet:
                toggleBottomSheet();
                break;
        }
    }
    @Override
    public boolean onDrag(View v, DragEvent event) {
        int id= v.getId();
        if(id==R.id.btnBottomSheet) {
            toggleBottomSheet();
        }
        return false;
    }
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }



}
