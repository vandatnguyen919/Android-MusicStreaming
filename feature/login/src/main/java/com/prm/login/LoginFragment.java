package com.prm.login;

import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.os.Bundle;

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
        btnSignUpFree.setOnClickListener(v -> navigator.navigate(com.prm.common.R.string.route_home));
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
        showToast("Sign up free clicked");
        // Simulate successful login for demonstration
        simulateSuccessfulLogin();
    }

    private void handleContinueWithGoogle() {
        Log.d(TAG, "Continue with Google clicked");
        showToast("Continue with Google clicked");
        // Simulate successful login for demonstration
        simulateSuccessfulLogin();
    }

    private void handleContinueWithFacebook() {
        Log.d(TAG, "Continue with Facebook clicked");
        showToast("Continue with Facebook clicked");
        // Simulate successful login for demonstration
        simulateSuccessfulLogin();
    }

    private void handleContinueWithApple() {
        Log.d(TAG, "Continue with Apple clicked");
        showToast("Continue with Apple clicked");
        // Simulate successful login for demonstration
        simulateSuccessfulLogin();
    }

    private void handleLogIn() {
        Log.d(TAG, "Log in clicked");
        showToast("Log in clicked");
        // Simulate successful login for demonstration
        simulateSuccessfulLogin();
    }

    private void simulateSuccessfulLogin() {
        // Notify the activity that login was successful
        navigator.navigate(com.prm.common.R.string.route_home);
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

    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

}