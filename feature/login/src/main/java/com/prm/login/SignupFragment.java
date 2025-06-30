package com.prm.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.prm.common.Navigator;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SignupFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "SignupFragment";

    @Inject
    Navigator navigator;

    private GoogleSignInHelper googleSignInHelper;

    // UI Components
    private ImageView ivBack;
    private TextInputLayout tilEmail, tilPassword, tilConfirmPassword;
    private TextInputEditText etEmail, etPassword, etConfirmPassword;
    private Button btnSignUp, btnContinueGoogle;
    private TextView tvLogin;

    public static SignupFragment newInstance() {
        return new SignupFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_signup, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupClickListeners();
        setupGoogleSignIn();
    }

    private void initViews(View view) {
        ivBack = view.findViewById(R.id.iv_back);
        tilEmail = view.findViewById(R.id.til_email);
        tilPassword = view.findViewById(R.id.til_password);
        tilConfirmPassword = view.findViewById(R.id.til_confirm_password);
        etEmail = view.findViewById(R.id.et_email);
        etPassword = view.findViewById(R.id.et_password);
        etConfirmPassword = view.findViewById(R.id.et_confirm_password);
        btnSignUp = view.findViewById(R.id.btn_sign_up);
        btnContinueGoogle = view.findViewById(R.id.btn_continue_google);
        tvLogin = view.findViewById(R.id.tv_login);
    }

    private void setupClickListeners() {
        ivBack.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);
        btnContinueGoogle.setOnClickListener(this);
        tvLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_back) {
            handleBack();
        } else if (id == R.id.btn_sign_up) {
            handleSignUp();
        } else if (id == R.id.btn_continue_google) {
            handleContinueWithGoogle();
        } else if (id == R.id.tv_login) {
            handleGoToLogin();
        }
    }

    private void handleBack() {
        if (getParentFragmentManager().getBackStackEntryCount() > 0) {
            getParentFragmentManager().popBackStack();
        } else {
            requireActivity().finish();
        }
    }

    private void handleSignUp() {
        if (validateForm()) {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            showToast("Creating account...");
            try {
                createAccount(email, password);
            } catch (Exception e) {
                Log.e(TAG, "Error during sign up process", e);
                showToast("Sign up failed: " + e.getMessage());
            }
        }
    }

    private void handleContinueWithGoogle() {
        Log.d(TAG, "Continue with Google clicked");
        showToast("Signing in with Google...");
        if (googleSignInHelper != null) {
            googleSignInHelper.signIn(getActivity());
        }
    }

    private void handleGoToLogin() {
        // Navigate to login form
        LoginFormFragment loginFormFragment = LoginFormFragment.newInstance();
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, loginFormFragment)
                .addToBackStack(null)
                .commit();
    }

    private boolean validateForm() {
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

    private void navigateToHome() {
        try {
            Log.d(TAG, "navigateToHome() called");
            // Check if fragment is still attached to avoid crashes
            if (isAdded() && getContext() != null) {
                Log.d(TAG, "Fragment is attached and context is not null");
                Log.d(TAG, "Navigator instance: " + (navigator != null ? "not null" : "null"));

                boolean navigationSuccess = false;

                // Try using injected navigator first
                if (navigator != null) {
                    try {
                        Log.d(TAG, "Calling navigator.navigateToHome()");
                        navigator.navigateToHome(getContext());
                        Log.d(TAG, "navigator.navigateToHome() completed");
                        navigationSuccess = true;
                    } catch (Exception e) {
                        Log.e(TAG, "Navigator failed", e);
                    }
                }

                // Fallback: direct navigation
                if (!navigationSuccess) {
                    Log.d(TAG, "Using fallback navigation");
                    try {
                        Class<?> mainActivityClass = Class.forName("com.prm.musicstreaming.MainActivity");
                        android.content.Intent intent = new android.content.Intent(getContext(), mainActivityClass);
                        intent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK | android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        Log.d(TAG, "Fallback navigation successful");
                        navigationSuccess = true;
                    } catch (Exception e) {
                        Log.e(TAG, "Fallback navigation failed", e);
                    }
                }

                if (navigationSuccess) {
                    // Use a small delay before finishing activity to ensure navigation completes
                    new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                        Log.d(TAG, "Finishing AuthActivity");
                        if (getActivity() != null && !getActivity().isFinishing()) {
                            getActivity().finish();
                        }
                    }, 500);
                }
            } else {
                Log.e(TAG, "Fragment not attached or context is null");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error navigating to home", e);
            // Last resort: just finish the current activity
            if (getActivity() != null && !getActivity().isFinishing()) {
                getActivity().finish();
            }
        }
    }

    private void setupGoogleSignIn() {
        if (getContext() != null) {
            googleSignInHelper = new GoogleSignInHelper(getContext());
            googleSignInHelper.setListener(new GoogleSignInHelper.GoogleSignInListener() {
                @Override
                public void onSignInSuccess(FirebaseUser user) {
                    Log.d(TAG, "Google sign in successful: " + user.getUid());
                    Log.d(TAG, "User display name: " + user.getDisplayName());
                    Log.d(TAG, "User email: " + user.getEmail());
                    showToast("Welcome " + (user.getDisplayName() != null ? user.getDisplayName() : user.getEmail()) + "!");
                    Log.d(TAG, "About to call navigateToHome()");
                    navigateToHome();
                }

                @Override
                public void onSignInFailure(String error) {
                    Log.e(TAG, "Google sign in failed: " + error);
                    showToast("Google sign in failed: " + error);
                }

                @Override
                public void onSignInCancelled() {
                    Log.d(TAG, "Google sign in cancelled");
                    showToast("Sign in cancelled");
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GoogleSignInHelper.getRequestCode()) {
            if (googleSignInHelper != null) {
                googleSignInHelper.handleSignInResult(data);
            }
        }
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
