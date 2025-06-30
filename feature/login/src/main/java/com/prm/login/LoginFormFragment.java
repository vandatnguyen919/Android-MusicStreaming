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
public class LoginFormFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "LoginFormFragment";

    @Inject
    Navigator navigator;

    // UI Components
    private ImageView ivBack;
    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText etEmail, etPassword;
    private Button btnLogin, btnContinueGoogle;
    private TextView tvForgotPassword, tvSignUp;

    public static LoginFormFragment newInstance() {
        return new LoginFormFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login_form, container, false);
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
        etEmail = view.findViewById(R.id.et_email);
        etPassword = view.findViewById(R.id.et_password);
        btnLogin = view.findViewById(R.id.btn_login);
        btnContinueGoogle = view.findViewById(R.id.btn_continue_google);
        tvForgotPassword = view.findViewById(R.id.tv_forgot_password);
        tvSignUp = view.findViewById(R.id.tv_sign_up);
    }

    private void setupClickListeners() {
        ivBack.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        btnContinueGoogle.setOnClickListener(this);
        tvForgotPassword.setOnClickListener(this);
        tvSignUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_back) {
            handleBack();
        } else if (id == R.id.btn_login) {
            handleLogin();
        } else if (id == R.id.btn_continue_google) {
            handleContinueWithGoogle();
        } else if (id == R.id.tv_forgot_password) {
            handleForgotPassword();
        } else if (id == R.id.tv_sign_up) {
            handleGoToSignUp();
        }
    }

    private void handleBack() {
        if (getParentFragmentManager().getBackStackEntryCount() > 0) {
            getParentFragmentManager().popBackStack();
        } else {
            requireActivity().finish();
        }
    }

    private void handleLogin() {
        if (validateForm()) {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            
            showToast("Signing in...");
            signInWithEmailPassword(email, password);
        }
    }

    private void handleContinueWithGoogle() {
        showToast("Google sign-in coming soon!");
        // TODO: Implement Google Sign-In
    }

    private void handleForgotPassword() {
        showToast("Forgot password feature coming soon!");
        // TODO: Implement forgot password
    }

    private void handleGoToSignUp() {
        // Navigate to signup form
        SignupFragment signupFragment = SignupFragment.newInstance();
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, signupFragment)
                .addToBackStack(null)
                .commit();
    }

    private boolean validateForm() {
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
                            Log.d(TAG, "Is anonymous: " + user.isAnonymous());
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
