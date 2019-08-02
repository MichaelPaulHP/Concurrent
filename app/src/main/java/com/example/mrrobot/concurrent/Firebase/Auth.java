package com.example.mrrobot.concurrent.Firebase;

import android.net.Uri;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Auth {
    private static Auth instance;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private Auth() {
        mAuth = FirebaseAuth.getInstance();

        this.currentUser = mAuth.getCurrentUser();

    }

    public static Auth getInstance() {
        if (instance == null) {
            instance = new Auth();
        }
        return instance;
    }
    public void signOut() {
        // Firebase sign out
        mAuth.signOut();
    }

    public String getUserName(){

        if(  currentUser.getDisplayName()==null || currentUser.getDisplayName().isEmpty()){
            return currentUser.getEmail();
        }
        return currentUser.getDisplayName();
    }
    public Uri getPhotoUrl(){
        return currentUser.getPhotoUrl();
    }
    public String getId(){
        return currentUser.getUid();
    }
}
