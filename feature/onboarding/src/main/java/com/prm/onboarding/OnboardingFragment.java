package com.prm.onboarding;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class OnboardingFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "OnboardingFragment";

    private OnboardingViewModel mViewModel;

    // UI Components
    private ImageView ivLogo;
    private TextView tvTitle;
    private Button btnSignUpFree;
    private Button btnContinueGoogle;
    private Button btnContinueFacebook;
    private Button btnContinueApple;
    private TextView tvLogIn;

    public static OnboardingFragment newInstance() {
        return new OnboardingFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_onboarding, container, false);
        initViews(view);
        setupClickListeners();
        setupAnimations();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(OnboardingViewModel.class);
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
        showToast("Sign up free clicked");
        // TODO: Navigate to sign up screen
    }

    private void handleContinueWithGoogle() {
        Log.d(TAG, "Continue with Google clicked");
        showToast("Continue with Google clicked");
        // TODO: Implement Google Sign In
    }

    private void handleContinueWithFacebook() {
        Log.d(TAG, "Continue with Facebook clicked");
        showToast("Continue with Facebook clicked");
        // TODO: Implement Facebook Sign In
    }

    private void handleContinueWithApple() {
        Log.d(TAG, "Continue with Apple clicked");
        showToast("Continue with Apple clicked");
        // TODO: Implement Apple Sign In
    }

    private void handleLogIn() {
        Log.d(TAG, "Log in clicked");
        showToast("Log in clicked");
        // TODO: Navigate to login screen
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