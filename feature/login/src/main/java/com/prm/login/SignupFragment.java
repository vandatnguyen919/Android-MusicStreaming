package com.prm.login;

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
        showToast("Google sign-in coming soon!");
        // TODO: Implement Google Sign-In
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

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
