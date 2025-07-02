package com.prm.musicstreaming;

import android.app.Application;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class MainApplication extends Application {

    private static final String TAG = "MainApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize Firebase
        FirebaseApp.initializeApp(this);

        // Check current authentication status
        FirebaseAuth auth = FirebaseAuth.getInstance();
        Log.d(TAG, "Checking current user...");

        FirebaseAuth.AuthStateListener authStateListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                Log.d(TAG, "User authenticated: " + user.getUid());
                Log.d(TAG, "User email: " + user.getEmail());
                Log.d(TAG, "Is anonymous: " + user.isAnonymous());
            } else {
                Log.d(TAG, "No authenticated user");
            }
        };

        auth.addAuthStateListener(authStateListener);

        // Check if user is already signed in
        if (auth.getCurrentUser() != null) {
            Log.d(TAG, "User already authenticated: " + auth.getCurrentUser().getUid());
        } else {
            Log.d(TAG, "No current user - user needs to login");
        }
    }
}
