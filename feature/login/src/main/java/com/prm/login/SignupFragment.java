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
import com.prm.domain.model.User;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

@AndroidEntryPoint
public class SignupFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "SignupFragment";

    @Inject
    Navigator navigator;

    @Inject
    CreateUserUseCase createUserUseCase; // Inject CreateUserUseCase

    private GoogleSignInHelper googleSignInHelper;

    // UI Components
    private ImageView ivBack;
    private TextInputLayout tilEmail, tilPassword, tilConfirmPassword;
    private TextInputEditText etEmail, etPassword, etConfirmPassword;
    private Button btnSignUp, btnContinueGoogle;
    private TextView tvLogin;

    private final CompositeDisposable disposables = new CompositeDisposable();

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

                            // Create an AppUser object and save to Firestore
                            User appUser = new User(
                                    user.getUid(),
                                    user.getDisplayName() != null ? user.getDisplayName() : "",
                                    user.getEmail() != null ? user.getEmail() : "",
                                    user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : ""
                            );

                            disposables.add(createUserUseCase.execute(appUser)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(() -> {
                                        Log.d(TAG, "User data created/updated in Firestore successfully");
                                        showToast("Account created successfully!");
                                        navigator.clearAndNavigate(com.prm.common.R.string.route_home);
                                    }, throwable -> {
                                        Log.e(TAG, "Failed to create/update user data in Firestore: " + throwable.getMessage(), throwable);
                                        showToast("Sign up failed: Failed to save user data.");
                                        // Still navigate home even if Firestore save fails for now, or handle differently
                                        navigator.clearAndNavigate(com.prm.common.R.string.route_home);
                                    })
                            );

                        } else {
                            Log.e(TAG, "Firebase user is null after successful creation");
                            showToast("Account created, but user data could not be saved.");
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

    private void setupGoogleSignIn() {
        if (getContext() != null) {
            googleSignInHelper = new GoogleSignInHelper(getContext(), createUserUseCase); // Pass createUserUseCase
            googleSignInHelper.setListener(new GoogleSignInHelper.GoogleSignInListener() {
                @Override
                public void onSignInSuccess(FirebaseUser user) {
                    Log.d(TAG, "Google sign in successful: " + user.getUid());
                    Log.d(TAG, "User display name: " + user.getDisplayName());
                    Log.d(TAG, "User email: " + user.getEmail());
                    showToast("Welcome " + (user.getDisplayName() != null ? user.getDisplayName() : user.getEmail()) + "!");
                    Log.d(TAG, "About to call navigateToHome()");
                    navigator.clearAndNavigate(com.prm.common.R.string.route_home);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        disposables.clear();
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
