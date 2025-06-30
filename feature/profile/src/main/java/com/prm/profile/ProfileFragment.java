package com.prm.profile;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.prm.common.Navigator;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ProfileFragment extends Fragment {

    private ProfileViewModel mViewModel;
    private Button btnAddSong;
    private Button btnAuthenticate;
    private TextView tvAuthStatus;

    @Inject
    Navigator navigator;

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize views
        btnAddSong = view.findViewById(R.id.btn_add_song);
        btnAuthenticate = view.findViewById(R.id.btn_authenticate);
        tvAuthStatus = view.findViewById(R.id.tv_auth_status);

        // Set click listeners
        btnAddSong.setOnClickListener(v -> {
            // Check authentication before showing dialog
            if (isUserAuthenticated()) {
                showAddSongDialog();
            } else {
                showAuthenticationRequiredDialog();
            }
        });

        btnAuthenticate.setOnClickListener(v -> {
            authenticateUser();
        });

        // Update authentication status
        updateAuthenticationStatus();

        // Set up authentication state listener
        setupAuthStateListener();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        // TODO: Use the ViewModel
    }

    private void showAddSongDialog() {
        AddSongBottomSheetFragment bottomSheet = new AddSongBottomSheetFragment();
        bottomSheet.show(getChildFragmentManager(), "AddSongBottomSheet");
    }

    private boolean isUserAuthenticated() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    private void updateAuthenticationStatus() {
        if (tvAuthStatus != null) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            android.util.Log.d("ProfileFragment", "Updating auth status. User: " + (user != null ? user.getUid() : "null"));

            if (user != null) {
                String statusText = "Authenticated: " + (user.isAnonymous() ? "Anonymous User" : user.getEmail());
                tvAuthStatus.setText(statusText);
                tvAuthStatus.setTextColor(getResources().getColor(android.R.color.holo_green_light));
                android.util.Log.d("ProfileFragment", "Status: " + statusText);
            } else {
                tvAuthStatus.setText("Not authenticated");
                tvAuthStatus.setTextColor(getResources().getColor(android.R.color.holo_red_light));
                android.util.Log.d("ProfileFragment", "Status: Not authenticated");
            }
        }
    }

    private void setupAuthStateListener() {
        FirebaseAuth.getInstance().addAuthStateListener(firebaseAuth -> {
            android.util.Log.d("ProfileFragment", "Auth state changed");
            updateAuthenticationStatus();
        });
    }

    private void showAuthenticationRequiredDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Login Required")
                .setMessage("You need to login to add songs. Please go to the login screen to sign in.")
                .setPositiveButton("Go to Login", (dialog, which) -> {
                    // Navigate to login screen
                    navigator.navigate(com.prm.common.R.string.route_login);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void authenticateUser() {
        // Redirect to login screen instead of trying to authenticate here
        Toast.makeText(getContext(), "Please login first", Toast.LENGTH_SHORT).show();
        navigator.navigate(com.prm.common.R.string.route_login);
    }

}