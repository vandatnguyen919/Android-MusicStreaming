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
import com.prm.domain.usecase.user.CreateUserUseCase;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LoginFormFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "LoginFormFragment";

    @Inject
    Navigator navigator;

    private GoogleSignInHelper googleSignInHelper;

    // UI Components
    private ImageView ivBack;
    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText etEmail, etPassword;
    private Button btnLogin, btnContinueGoogle;
    private TextView tvForgotPassword, tvSignUp;

    @Inject
    CreateUserUseCase createUserUseCase;

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
        setupGoogleSignIn();
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
        Log.d(TAG, "Continue with Google clicked");
        showToast("Signing in with Google...");
        if (googleSignInHelper != null) {
            googleSignInHelper.signIn(getActivity());
        }
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
            googleSignInHelper = new GoogleSignInHelper(getContext(), createUserUseCase);
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
