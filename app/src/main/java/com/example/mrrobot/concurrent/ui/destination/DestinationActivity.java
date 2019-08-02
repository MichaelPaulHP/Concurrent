package com.example.mrrobot.concurrent.ui.destination;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableList;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.example.mrrobot.concurrent.R;
import com.example.mrrobot.concurrent.databinding.ActivityDestinationBinding;
import com.example.mrrobot.concurrent.models.Destination;
import com.example.mrrobot.concurrent.models.User;
import com.example.mrrobot.concurrent.ui.home.DestinationAdapter;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.Arrays;
import java.util.List;

public class DestinationActivity extends AppCompatActivity implements View.OnClickListener {


    String TAG = "autocompleteDestination";
    int AUTOCOMPLETE_REQUEST_CODE = 66;


    private DestinationViewModel destinationViewModel;
    private ActivityDestinationBinding binding;

    private RecyclerView recyclerViewDestinationsFound;
    public DestinationAdapter destinationAdapter;



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
        initUI();

    }

    private void initUI() {
        this.binding.btnSearchGooglePlace.setOnClickListener(this);
        this.binding.btnSubmitDestination.setOnClickListener(this);
        this.recyclerViewDestinationsFound = this.binding.rvDestinationsFound;
        initRecyclerViewOfDestinations();

        subscribeHasNewDestination();
        subscribeDestinationSelected();
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
    private void subscribeDestinationSelected() {
        final Observer<Destination> booleanObserver = new Observer<Destination>() {
            @Override
            public void onChanged(@Nullable final Destination aLong) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(aLong==null){
                            DestinationActivity.this.binding.btnSubmitDestination.setText("Cancelar");
                        }
                        else{
                            DestinationActivity.this.binding.btnSubmitDestination.setText("Unirse");
                            DestinationActivity.this.binding.setDestinationSelected(aLong);
                        }


                    }
                });

            }
        };

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
                .build(getApplicationContext());

        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);

    }

    private void onSubmit() {

        this.destinationViewModel.OnSubmit();
        // close activity
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btnSearchGooglePlace:
                showSearchPlace();
                break;
            case R.id.btnSubmitDestination:
                onSubmit();
                break;

        }
    }
}
