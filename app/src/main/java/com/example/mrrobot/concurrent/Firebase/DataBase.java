package com.example.mrrobot.concurrent.Firebase;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DataBase {
    private static final DataBase ourInstance = new DataBase();
    private final String TAG="DataBaseE";


    public FirebaseDatabase database;

    public static DataBase getInstance() {
        return ourInstance;
    }

    private DataBase() {

    }


    public void add(String reference,String data,OnSuccessListener onSuccessListener,OnFailureListener onFailureListener){
        DatabaseReference myRef = database.getReference(reference);
        myRef.setValue(data)
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);

    }

    public void read(String reference,String data) {
        DatabaseReference myRef = database.getReference(reference);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String value = dataSnapshot.getValue(String.class);
                Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", databaseError.toException());

            }
        });
    }
}
