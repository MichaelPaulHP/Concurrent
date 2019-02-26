package com.example.mrrobot.concurrent.Firebase;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.Map;

import javax.annotation.Nullable;

public class Firestore {
    private final String TAG ="Firestore";
    FirebaseFirestore db;
    private static final Firestore ourInstance = new Firestore();

    public static Firestore getInstance() {
        return ourInstance;
    }

    private Firestore() {
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Add a new document with a generated ID
     * Map<String, Object> user = new HashMap<>();
     * user.put("first", "Ada");
     * user.put("last", "Lovelace");
     * user.put("born", 1815);
     */
    public void addData(String collectionPath , Map data){

        db.collection(collectionPath)
                .add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });



    }



    /**
     *
     */
    public void readData(String collectionPath){
        db.collection("collectionPath")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }
}
