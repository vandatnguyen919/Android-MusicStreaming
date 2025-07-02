package com.prm.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;


public class GoogleSignInHelper {
    private static final String TAG = "GoogleSignInHelper";
    private static final int RC_SIGN_IN = 9001;
    
    private final Context context;
    private final GoogleSignInClient googleSignInClient;
    private final FirebaseAuth firebaseAuth;
    private GoogleSignInListener listener;
    
    public interface GoogleSignInListener {
        void onSignInSuccess(FirebaseUser user);
        void onSignInFailure(String error);
        void onSignInCancelled();
    }
    
    public GoogleSignInHelper(Context context) {
        this.context = context;
        this.firebaseAuth = FirebaseAuth.getInstance();

        Log.d(TAG, "Initializing GoogleSignInHelper");

        // Get web client ID
        String webClientId = getWebClientId(context);
        Log.d(TAG, "Web Client ID: " + (webClientId != null ? "configured" : "null"));
        Log.d(TAG, "Web Client ID value: " + webClientId);

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(webClientId)
                .requestEmail()
                .build();

        this.googleSignInClient = GoogleSignIn.getClient(context, gso);
        Log.d(TAG, "GoogleSignInClient created successfully");
    }
    
    public void setListener(GoogleSignInListener listener) {
        this.listener = listener;
    }
    

    public void signInWithLauncher(ActivityResultLauncher<Intent> launcher) {
        Log.d(TAG, "Starting Google Sign-In flow with launcher");
        try {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            Log.d(TAG, "Got sign-in intent, launching with ActivityResultLauncher");
            launcher.launch(signInIntent);
        } catch (Exception e) {
            Log.e(TAG, "Error starting Google Sign-In", e);
            if (listener != null) {
                listener.onSignInFailure("Failed to start Google Sign-In: " + e.getMessage());
            }
        }
    }


    @Deprecated
    public void signIn(Activity activity) {
        Log.d(TAG, "Starting Google Sign-In flow (deprecated method)");
        try {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            Log.d(TAG, "Got sign-in intent, starting activity for result");
            activity.startActivityForResult(signInIntent, RC_SIGN_IN);
        } catch (Exception e) {
            Log.e(TAG, "Error starting Google Sign-In", e);
            if (listener != null) {
                listener.onSignInFailure("Failed to start Google Sign-In: " + e.getMessage());
            }
        }
    }
    

    public void handleSignInResult(Intent data) {
        Log.d(TAG, "Handling Google Sign-In result");
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            // Google Sign In was successful, authenticate with Firebase
            GoogleSignInAccount account = task.getResult(ApiException.class);
            Log.d(TAG, "Google sign in successful: " + account.getId());
            Log.d(TAG, "Account email: " + account.getEmail());
            Log.d(TAG, "Account display name: " + account.getDisplayName());
            String idToken = account.getIdToken();
            Log.d(TAG, "ID Token: " + (idToken != null ? "present" : "null"));
            firebaseAuthWithGoogle(idToken);
        } catch (ApiException e) {
            // Google Sign In failed
            Log.w(TAG, "Google sign in failed with status code: " + e.getStatusCode(), e);
            if (listener != null) {
                if (e.getStatusCode() == 12501) {
                    // User cancelled the sign-in
                    Log.d(TAG, "User cancelled Google Sign-In");
                    listener.onSignInCancelled();
                } else {
                    Log.e(TAG, "Google Sign-In error: " + e.getStatusCode() + " - " + e.getMessage());
                    listener.onSignInFailure("Google sign in failed: " + e.getMessage() + " (Code: " + e.getStatusCode() + ")");
                }
            }
        }
    }
    

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success
                            Log.d(TAG, "Firebase authentication successful");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            if (listener != null && user != null) {
                                listener.onSignInSuccess(user);
                            }
                        } else {
                            // Sign in failed
                            Log.w(TAG, "Firebase authentication failed", task.getException());
                            if (listener != null) {
                                String errorMsg = task.getException() != null ? 
                                    task.getException().getMessage() : "Authentication failed";
                                listener.onSignInFailure(errorMsg);
                            }
                        }
                    }
                });
    }
    

    public void signOut() {
        // Firebase sign out
        firebaseAuth.signOut();
        
        // Google sign out
        googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(TAG, "Google sign out completed");
            }
        });
    }
    

    public void revokeAccess() {
        // Firebase sign out
        firebaseAuth.signOut();
        
        // Google revoke access
        googleSignInClient.revokeAccess().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(TAG, "Google access revoked");
            }
        });
    }
    

    public boolean isSignedIn() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
        return account != null && firebaseAuth.getCurrentUser() != null;
    }
    

    public static int getRequestCode() {
        return RC_SIGN_IN;
    }


    private String getWebClientId(Context context) {
        try {
            Log.d(TAG, "Attempting to get web client ID");

            // Try to get from app resources first
            int resourceId = context.getResources().getIdentifier(
                "default_web_client_id", "string", context.getPackageName());
            Log.d(TAG, "Resource ID for default_web_client_id: " + resourceId);

            if (resourceId != 0) {
                String webClientId = context.getString(resourceId);
                Log.d(TAG, "Found web client ID from package: " + context.getPackageName());
                if (webClientId != null && !webClientId.equals("YOUR_WEB_CLIENT_ID_HERE")) {
                    return webClientId;
                }
                Log.w(TAG, "Web client ID is placeholder value");
            }

            // Fallback: try to get from main app package
            try {
                int appResourceId = context.getResources().getIdentifier(
                    "default_web_client_id", "string", "com.prm.musicstreaming");
                Log.d(TAG, "App resource ID for default_web_client_id: " + appResourceId);

                if (appResourceId != 0) {
                    String appWebClientId = context.getString(appResourceId);
                    Log.d(TAG, "Found web client ID from app package");
                    if (appWebClientId != null && !appWebClientId.equals("YOUR_WEB_CLIENT_ID_HERE")) {
                        return appWebClientId;
                    }
                }
            } catch (Exception e) {
                Log.w(TAG, "Failed to get web client ID from app package", e);
            }

            Log.w(TAG, "No valid web client ID found, using placeholder");
            // Return a placeholder - this will cause Google Sign-In to fail with a clear error
            return "YOUR_WEB_CLIENT_ID_HERE";
        } catch (Exception e) {
            Log.e(TAG, "Failed to get web client ID", e);
            return "YOUR_WEB_CLIENT_ID_HERE";
        }
    }
}
