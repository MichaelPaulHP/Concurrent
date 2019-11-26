package com.example.mrrobot.concurrent.ui.destination;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.mrrobot.concurrent.R;
import com.example.mrrobot.concurrent.Utils.IMessenger;
import com.example.mrrobot.concurrent.databinding.ActivityDestinationBinding;
import com.example.mrrobot.concurrent.models.Destination;
import com.example.mrrobot.concurrent.models.User;
import com.example.mrrobot.concurrent.models.UserEmitter;
import com.example.mrrobot.concurrent.ui.home.DestinationAdapter;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.mapbox.mapboxsdk.maps.MapView;

import org.json.JSONException;

import java.util.Arrays;
import java.util.List;

import timber.log.Timber;

public class DestinationActivity extends AppCompatActivity implements View.OnClickListener, View.OnDragListener, IMessenger {


    String TAG = "DestinationActivity";
    int AUTOCOMPLETE_REQUEST_CODE = 66;


    private DestinationViewModel destinationViewModel;
    private ActivityDestinationBinding binding;

    private RecyclerView recyclerViewDestinationsFound;
    public DestinationAdapter destinationAdapter;
    private BottomSheetBehavior sheetBehavior;
    private MapView mapView;
    private ProgressBar progressBar;


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

        this.destinationViewModel.setMessenger(this);
        this.destinationViewModel.initMapView(mapView, savedInstanceState);

        initUI();
        UserEmitter.startListenerOnNewDestination();
    }

    private void initUI() {
        initBottomSheet();
        this.binding.layoutSearch.bringToFront();

        this.binding.btnMyOrigin.setOnClickListener(this);
        this.binding.btnMyDestination.setOnClickListener(this);
        this.binding.btnFindMyDestination.setOnClickListener(this);
        this.binding.btnFindMyOrigin.setOnClickListener(this);
        this.progressBar =findViewById(R.id.formProgressBar);

        this.binding.btnSubmitDestination.setOnClickListener(this);
        this.recyclerViewDestinationsFound = this.binding.rvDestinationsFound;
        initRecyclerViewOfDestinations();

        subscribeDestinationFound();
        subscribeMyDestinationTemp();
        subscribeHasResults();
        subscribeHasNewDestination();
    }

    private void initBottomSheet() {
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


    private void subscribeDestinationFound() {
        final Observer<Destination> observer = new Observer<Destination>() {
            @Override
            public void onChanged(@Nullable final Destination aDestination) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //DestinationActivity.this.destinationAdapter.notifyNewDestinationInserted();
                        toggleBottomSheet();
                        showLayoutToJoin();
                        showDestinationToJoin(aDestination, "JOIN");
                    }
                });

            }
        };
        this.destinationViewModel.destinationFound.observe(this, observer);
    }
    private void subscribeHasResults() {
        final Observer<Boolean> observer = new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable final Boolean hasResults) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if(hasResults){
                            DestinationActivity.this.destinationAdapter.notifyNewDestinationInserted();
                            // show animation
                        }

                    }
                });

            }
        };
        this.destinationViewModel.hasNewDestination.observe(this, observer);
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

    private void subscribeMyDestinationTemp() {
        final Observer<Destination> observer = new Observer<Destination>() {
            @Override
            public void onChanged(@Nullable final Destination aDestination) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                        if (hasOrigin(aDestination)) {

                            String textO=aDestination.getOriginAddress();
                            DestinationActivity.this.binding.btnMyOrigin.setText(textO);
                        }

                        if (hasDestination(aDestination) && aDestination.getName()!=null) {

                            String text=aDestination.getDestinationAddress();
                            DestinationActivity.this.binding.btnMyDestination.setText(text);

                            if (hasOrigin(aDestination)) {

                                String textO=aDestination.getOriginAddress();
                                DestinationActivity.this.binding.btnMyOrigin.setText(textO);
                                showLayoutToJoin();
                                showDestinationToJoin(aDestination, "Create");

                            }
                        }


                    }
                });

            }
        };
        destinationViewModel.getMyDestinationObservable().observe(this, observer);

        //Destination.destinationSelected.observe(this, booleanObserver);
    }
    private void subscribeHasNewDestination() {
        final Observer<Destination> observer = new Observer<Destination>() {
            @Override
            public void onChanged(@Nullable final Destination aDestination) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showProgressBar(false);showMessage("Complete");
                    }
                });
            }
        };
        User.getCurrentUser().hasNewDestination.observe(this,observer);
    }

    private void showDestinationToJoin(Destination destination, String text) {
        this.binding.setDestinationSelected(destination);
        this.binding.btnSubmitDestination.setText(text);
    }

    private boolean hasOrigin(Destination destination) {
        return destination.getOrigin() != null;
    }

    private boolean hasDestination(Destination destination) {
        return destination.getDestination() != null;
    }

    private void initRecyclerViewOfDestinations() {

        this.recyclerViewDestinationsFound.setLayoutManager(
                new LinearLayoutManager(getApplicationContext(),
                        LinearLayoutManager.HORIZONTAL, false));

        this.destinationAdapter = new DestinationAdapter();
        this.recyclerViewDestinationsFound.setAdapter(this.destinationAdapter);
        this.destinationAdapter.setDestinations(this.destinationViewModel.getResultsDestination());
        this.destinationAdapter.setEventListener(this.destinationViewModel);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE || (requestCode == AUTOCOMPLETE_REQUEST_CODE + 1)) {
            if (resultCode == RESULT_OK) {

                Place place = Autocomplete.getPlaceFromIntent(data);
                Timber.d("ee");

                Boolean isDestination = requestCode == AUTOCOMPLETE_REQUEST_CODE + 1;
                this.destinationViewModel.onPlaceSelected(place, isDestination);

                //DestinationFragment.this.destinationListener.onPlaceSelected(place);

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);

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
        try {
            showProgressBar(true);
            Destination destination = this.binding.getDestinationSelected();
            this.destinationViewModel.OnSubmit(destination);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void showLayoutToJoin() {
        this.binding.layoutToDestination.setVisibility(View.VISIBLE);
    }
    private void showProgressBar(Boolean show) {
        this.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        if( this.binding.layoutToDestination.getVisibility()==View.VISIBLE)
            findViewById(R.id.btnSubmitDestination).setVisibility(show ? View.GONE : View.VISIBLE);
    }
    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btnFindMyOrigin:
                showSearchPlace(AUTOCOMPLETE_REQUEST_CODE);
                break;
            case R.id.btnFindMyDestination:
                showSearchPlace(AUTOCOMPLETE_REQUEST_CODE + 1);
                break;
            case R.id.btnMyOrigin:
                destinationViewModel.goMyOrigin();
                break;
            case R.id.btnMyDestination:
                destinationViewModel.goMyDestination();
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
    public void onError(String message) {
        showProgressBar(false);
        showMessage(message);
    }

    @Override
    public void OnWarning(String message) {
        showProgressBar(false);
        showMessage(message);
    }

    @Override
    public void onSuccess(String message) {
        showProgressBar(false);
        showMessage(message);
    }

    private void showMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        int id = v.getId();
        if (id == R.id.btnBottomSheet) {
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
