package com.prm.login;

import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.prm.common.Navigator;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LoginFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "LoginFragment";

    @Inject
    Navigator navigator;

    private LoginViewModel mViewModel;
    // UI Components
    private ImageView ivLogo;
    private TextView tvTitle;
    private Button btnSignUpFree;
    private Button btnContinueGoogle;
    private Button btnContinueFacebook;
    private Button btnContinueApple;
    private TextView tvLogIn;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        initViews(view);
        setupClickListeners();
        setupAnimations();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        // TODO: Use the ViewModel
    }
    
    private void initViews(View view) {
        ivLogo = view.findViewById(R.id.iv_logo);
        tvTitle = view.findViewById(R.id.tv_title);
        btnSignUpFree = view.findViewById(R.id.btn_sign_up_free);
        btnContinueGoogle = view.findViewById(R.id.btn_continue_google);
        btnContinueFacebook = view.findViewById(R.id.btn_continue_facebook);
        btnContinueApple = view.findViewById(R.id.btn_continue_apple);
        tvLogIn = view.findViewById(R.id.tv_log_in);
    }

    private void setupClickListeners() {
        btnSignUpFree.setOnClickListener(this);
        btnContinueGoogle.setOnClickListener(this);
        btnContinueFacebook.setOnClickListener(this);
        btnContinueApple.setOnClickListener(this);
        tvLogIn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.btn_sign_up_free) {
            handleSignUpFree();
        } else if (id == R.id.btn_continue_google) {
            handleContinueWithGoogle();
        } else if (id == R.id.btn_continue_facebook) {
            handleContinueWithFacebook();
        } else if (id == R.id.btn_continue_apple) {
            handleContinueWithApple();
        } else if (id == R.id.tv_log_in) {
            handleLogIn();
        }
    }

    private void handleSignUpFree() {
        Log.d(TAG, "Sign up free clicked");
        showSignUpDialog();
    }

    private void handleContinueWithGoogle() {
        Log.d(TAG, "Continue with Google clicked");
        showToast("Signing in...");
        signInAnonymously();
    }

    private void handleContinueWithFacebook() {
        Log.d(TAG, "Continue with Facebook clicked");
        showToast("Signing in...");
        signInAnonymously();
    }

    private void handleContinueWithApple() {
        Log.d(TAG, "Continue with Apple clicked");
        showToast("Signing in...");
        signInAnonymously();
    }

    private void handleLogIn() {
        Log.d(TAG, "Log in clicked");
        showLoginDialog();
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
                            navigateToHome();
                        }
                    } else {
                        Log.e(TAG, "Anonymous sign in failed", task.getException());
                        String errorMsg = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                        showToast("Login failed: " + errorMsg);
                    }
                });
    }

    private void navigateToHome() {
        try {
            // Check if fragment is still attached to avoid crashes
            if (isAdded() && getContext() != null) {
                navigator.navigateToHome(getContext());
                // Use a small delay before finishing activity to ensure navigation completes
                new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                    if (getActivity() != null && !getActivity().isFinishing()) {
                        getActivity().finish();
                    }
                }, 100);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error navigating to home", e);
            // Fallback: just finish the current activity
            if (getActivity() != null && !getActivity().isFinishing()) {
                getActivity().finish();
            }
        }
    }

    private void setupAnimations() {
        if (getContext() != null) {
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
            btnContinueFacebook.startAnimation(slideUp);

            slideUp = AnimationUtils.loadAnimation(getContext(), R.anim.slide_up);
            slideUp.setStartOffset(600);
            btnContinueApple.startAnimation(slideUp);

            slideUp = AnimationUtils.loadAnimation(getContext(), R.anim.slide_up);
            slideUp.setStartOffset(700);
            tvLogIn.startAnimation(slideUp);
        }
    }

    private void showLoginDialog() {
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
            positiveButton.setOnClickListener(v -> {
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
                            navigateToHome();
                        }
                    } else {
                        Log.e(TAG, "Email/Password sign in failed", task.getException());
                        String errorMsg = task.getException() != null ?
                            task.getException().getMessage() : "Unknown error";
                        showToast("Login failed: " + errorMsg);
                    }
                });
    }

    private void showSignUpDialog() {
        if (getContext() == null) return;

        // Create dialog layout
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_signup, null);

        // Get references to dialog views
        TextInputLayout tilEmail = dialogView.findViewById(R.id.til_email);
        TextInputLayout tilPassword = dialogView.findViewById(R.id.til_password);
        TextInputLayout tilConfirmPassword = dialogView.findViewById(R.id.til_confirm_password);
        TextInputEditText etEmail = dialogView.findViewById(R.id.et_email);
        TextInputEditText etPassword = dialogView.findViewById(R.id.et_password);
        TextInputEditText etConfirmPassword = dialogView.findViewById(R.id.et_confirm_password);

        // Create and show dialog
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("Create Free Account")
                .setView(dialogView)
                .setPositiveButton("Create Account", null)
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(v -> {
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
                            navigateToHome();
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
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

}