package com.prm.login;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;
import androidx.fragment.app.Fragment;

import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.prm.common.Navigator;
import com.prm.domain.usecase.user.CreateUserUseCase;

import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LoginFragment extends Fragment {

    private static final String TAG = "LoginFragment";
    private static final String WEB_CLIENT_ID = "585106703577-aopj0mck5l6h4cgh2d4omu36mj8irnu8.apps.googleusercontent.com";

    @Inject
    Navigator navigator;

    @Inject
    CreateUserUseCase createUserUseCase;

    private CredentialManager credentialManager;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        credentialManager = CredentialManager.create(context);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // UI Components
        ImageView ivLogo = view.findViewById(R.id.iv_logo);
        TextView tvTitle = view.findViewById(R.id.tv_title);
        Button btnSignUpFree = view.findViewById(R.id.btn_sign_up_free);
        Button btnContinueGoogle = view.findViewById(R.id.btn_continue_google);
        TextView tvLogIn = view.findViewById(R.id.tv_log_in);

        btnSignUpFree.setOnClickListener(this::showSignUpDialog);
        btnContinueGoogle.setOnClickListener(this::onGoogleSignInClick);
        tvLogIn.setOnClickListener(this::showLoginDialog);

        // Fade in animation for logo and title
        Animation fadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        ivLogo.startAnimation(fadeIn);
        tvTitle.startAnimation(fadeIn);

        // Slide up animation for buttons with delay
        Animation slideUp = AnimationUtils.loadAnimation(getContext(), R.anim.slide_up);
        slideUp.setStartOffset(300);
        btnSignUpFree.startAnimation(slideUp);

        slideUp = AnimationUtils.loadAnimation(getContext(), R.anim.slide_up);
        slideUp.setStartOffset(400);
        btnContinueGoogle.startAnimation(slideUp);

        slideUp = AnimationUtils.loadAnimation(getContext(), R.anim.slide_up);
        slideUp.setStartOffset(500);
        tvLogIn.startAnimation(slideUp);

        return view;
    }

    private void onGoogleSignInClick(View v) {

        // Create Google ID option
        GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(WEB_CLIENT_ID)
                .build();

        // Create credential request
        GetCredentialRequest request = new GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build();

        // Make the credential request
        credentialManager.getCredentialAsync(
                requireContext(),
                request,
                null,
                Executors.newSingleThreadExecutor(),
                new CredentialManagerCallback<>() {
                    @Override
                    public void onResult(GetCredentialResponse result) {
                        requireActivity().runOnUiThread(() -> handleGoogleSignInResult(result));
                    }

                    @Override
                    public void onError(@NonNull GetCredentialException e) {
                        requireActivity().runOnUiThread(() -> handleGoogleSignInError(e));
                    }
                }
        );
    }

    private void handleGoogleSignInResult(GetCredentialResponse result) {
        // Get the credential from the result
        GoogleIdTokenCredential googleIdTokenCredential = GoogleIdTokenCredential
                .createFrom(result.getCredential().getData());

        // Extract the ID token
        String idToken = googleIdTokenCredential.getIdToken();

        // Authenticate with Firebase using the ID token
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Firebase authentication successful");
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            // Log user information
                            Log.d(TAG, "Google Sign-In successful");
                            Log.d(TAG, "Email: " + googleIdTokenCredential.getId());
                            Log.d(TAG, "Google sign in successful: " + user.getUid());
                            Log.d(TAG, "User display name: " + user.getDisplayName());
                            Log.d(TAG, "User email: " + user.getEmail());

                            String welcomeMessage = "Welcome " +
                                    (user.getDisplayName() != null ? user.getDisplayName() : user.getEmail()) + "!";
                            showToast(welcomeMessage);

                            // Navigate to home
                            navigator.clearAndNavigate(com.prm.common.R.string.route_home);
                        }
                    } else {
                        Log.e(TAG, "Firebase authentication failed", task.getException());
                        String errorMsg = task.getException() != null ?
                                task.getException().getMessage() : "Authentication failed";
                        showToast("Sign in failed: " + errorMsg);
                    }
                });

    }

    private void handleGoogleSignInError(GetCredentialException e) {
        Log.e(TAG, "Google Sign-In failed", e);

        String errorMessage = "Google sign in failed";
        if (e.getMessage() != null) {
            errorMessage += ": " + e.getMessage();
        }
        showToast(errorMessage);
    }

    private void signInAnonymously() {
        FirebaseAuth.getInstance().signInAnonymously()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Anonymous sign in successful");
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            Log.d(TAG, "User ID: " + user.getUid());
                            Log.d(TAG, "Is anonymous: " + user.isAnonymous());
                            showToast("Login successful!");
                            navigator.clearAndNavigate(com.prm.common.R.string.route_home);
                        }
                    } else {
                        Log.e(TAG, "Anonymous sign in failed", task.getException());
                        String errorMsg = task.getException() != null ?
                                task.getException().getMessage() : "Unknown error";
                        showToast("Login failed: " + errorMsg);
                    }
                });
    }

    private void showLoginDialog(View v) {
        if (getContext() == null) return;

        // Create dialog layout
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_login, null);

        // Get references to dialog views
        TextInputLayout tilEmail = dialogView.findViewById(R.id.til_email);
        TextInputLayout tilPassword = dialogView.findViewById(R.id.til_password);
        TextInputEditText etEmail = dialogView.findViewById(R.id.et_email);
        TextInputEditText etPassword = dialogView.findViewById(R.id.et_password);

        // Create and show dialog
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("Log in to Music")
                .setView(dialogView)
                .setPositiveButton("Log in", null)
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(view-> {
                // Validate form
                if (validateLoginForm(tilEmail, tilPassword, etEmail, etPassword)) {
                    String email = etEmail.getText().toString().trim();
                    String password = etPassword.getText().toString().trim();

                    showToast("Signing in...");
                    signInWithEmailPassword(email, password);
                    dialog.dismiss();
                }
            });
        });

        dialog.show();
    }

    private boolean validateLoginForm(TextInputLayout tilEmail, TextInputLayout tilPassword,
                                      TextInputEditText etEmail, TextInputEditText etPassword) {
        boolean isValid = true;

        // Clear previous errors
        tilEmail.setError(null);
        tilPassword.setError(null);

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validate email
        if (TextUtils.isEmpty(email)) {
            tilEmail.setError("Email is required");
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Please enter a valid email");
            isValid = false;
        }

        // Validate password
        if (TextUtils.isEmpty(password)) {
            tilPassword.setError("Password is required");
            isValid = false;
        }

        return isValid;
    }

    private void signInWithEmailPassword(String email, String password) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Email/Password sign in successful");
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            Log.d(TAG, "User ID: " + user.getUid());
                            Log.d(TAG, "User email: " + user.getEmail());
                            showToast("Login successful!");
                            navigator.clearAndNavigate(com.prm.common.R.string.route_home);
                        }
                    } else {
                        Log.e(TAG, "Email/Password sign in failed", task.getException());
                        String errorMsg = task.getException() != null ?
                                task.getException().getMessage() : "Unknown error";
                        showToast("Login failed: " + errorMsg);
                    }
                });
    }

    private void showSignUpDialog(View v) {
        if (getContext() == null) return;

        // Create dialog layout
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_signup, null);

        // Get references to dialog views
        TextInputLayout tilEmail = dialogView.findViewById(R.id.til_email);
        TextInputLayout tilPassword = dialogView.findViewById(R.id.til_password);
        TextInputLayout tilConfirmPassword = dialogView.findViewById(R.id.til_confirm_password);
        TextInputEditText etEmail = dialogView.findViewById(R.id.et_email);
        TextInputEditText etPassword = dialogView.findViewById(R.id.et_password);
        TextInputEditText etConfirmPassword = dialogView.findViewById(R.id.et_confirm_password);

        // Create and show dialog
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle("Create Free Account")
                .setView(dialogView)
                .setPositiveButton("Create Account", null)
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(view -> {
                // Validate form
                if (validateSignUpForm(tilEmail, tilPassword, tilConfirmPassword,
                        etEmail, etPassword, etConfirmPassword)) {
                    String email = etEmail.getText().toString().trim();
                    String password = etPassword.getText().toString().trim();

                    showToast("Creating account...");
                    createAccount(email, password);
                    dialog.dismiss();
                }
            });
        });

        dialog.show();
    }

    private boolean validateSignUpForm(TextInputLayout tilEmail, TextInputLayout tilPassword,
                                       TextInputLayout tilConfirmPassword, TextInputEditText etEmail,
                                       TextInputEditText etPassword, TextInputEditText etConfirmPassword) {
        boolean isValid = true;

        // Clear previous errors
        tilEmail.setError(null);
        tilPassword.setError(null);
        tilConfirmPassword.setError(null);

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Validate email
        if (TextUtils.isEmpty(email)) {
            tilEmail.setError("Email is required");
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Please enter a valid email");
            isValid = false;
        }

        // Validate password
        if (TextUtils.isEmpty(password)) {
            tilPassword.setError("Password is required");
            isValid = false;
        } else if (password.length() < 6) {
            tilPassword.setError("Password must be at least 6 characters");
            isValid = false;
        }

        // Validate confirm password
        if (TextUtils.isEmpty(confirmPassword)) {
            tilConfirmPassword.setError("Please confirm your password");
            isValid = false;
        } else if (!password.equals(confirmPassword)) {
            tilConfirmPassword.setError("Passwords do not match");
            isValid = false;
        }

        return isValid;
    }

    private void createAccount(String email, String password) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Account created successfully");
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            Log.d(TAG, "User ID: " + user.getUid());
                            Log.d(TAG, "User email: " + user.getEmail());
                            showToast("Account created successfully!");
                            navigator.clearAndNavigate(com.prm.common.R.string.route_home);
                        }
                    } else {
                        Log.e(TAG, "Account creation failed", task.getException());
                        String errorMsg = task.getException() != null ?
                                task.getException().getMessage() : "Unknown error";
                        showToast("Sign up failed: " + errorMsg);
                    }
                });
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
}