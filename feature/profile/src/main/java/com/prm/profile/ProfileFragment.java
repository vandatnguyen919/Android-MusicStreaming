package com.prm.profile;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.prm.common.Navigator;
import com.prm.common.CommonRoutes;
import com.prm.domain.model.User;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    private ProfileViewModel mViewModel;
    private ImageView ivProfile;
    private TextView tvUsername;
    private TextView tvEmail;
    private Button btnEditProfile;
    private Button btnAddSong;
    private Button btnLogout;

    @Inject
    Navigator navigator;

    private FirebaseUser currentUser;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize views
        ivProfile = view.findViewById(R.id.iv_profile);
        tvUsername = view.findViewById(R.id.tv_username);
        tvEmail = view.findViewById(R.id.tv_email);
        btnEditProfile = view.findViewById(R.id.btn_edit_profile);
        btnAddSong = view.findViewById(R.id.btn_add_song);
        btnLogout = view.findViewById(R.id.btnLogout);

        Glide.with(this)
                .load(currentUser.getPhotoUrl())
                .placeholder(R.drawable.ic_profile)
                .error(R.drawable.ic_profile)
                .circleCrop()
                .into(ivProfile);
        tvUsername.setText(currentUser.getDisplayName());
        tvEmail.setText(currentUser.getEmail());

        // Set click listeners
        btnAddSong.setOnClickListener(v -> {
            if (isUserAuthenticated()) {
                showAddSongDialog();
            } else {
                showAuthenticationRequiredDialog();
            }
        });

        btnEditProfile.setOnClickListener(v -> {
            // if (isUserAuthenticated() && mViewModel.currentUser.getValue() != null) {
                NavController navController = Navigation.findNavController(v);
                navController.navigate(CommonRoutes.EDIT_PROFILE);
            // } else {
            //     showToast("Please login to edit profile.");
            // }
        });

        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();
            navigator.clearAndNavigate(com.prm.common.R.string.route_login);
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        // Observe loading state
        mViewModel.isLoading.observe(getViewLifecycleOwner(), isLoading -> {
            // Show/hide loading indicator if you have one
            // progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            Log.d(TAG, "Loading: " + isLoading);
        });

        // Observe error messages
        mViewModel.error.observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error: " + error);
            }
        });

        // Refresh user data on view creation (optional, as it's already fetched in ViewModel's constructor)
        // mViewModel.fetchCurrentUser();

        // Set up authentication state listener using Firebase directly for immediate UI updates on auth changes
        FirebaseAuth.getInstance().addAuthStateListener(firebaseAuth -> {
            Log.d(TAG, "Auth state changed");
            if (firebaseAuth.getCurrentUser() != null) {
                mViewModel.fetchCurrentUser(); // Re-fetch user data if auth state changes
            } else {
                // Clear UI if user logs out
                tvUsername.setText("");
                tvEmail.setText("");
                btnEditProfile.setVisibility(View.GONE);
            }
        });
    }

    private void showAddSongDialog() {
        AddSongBottomSheetFragment bottomSheet = new AddSongBottomSheetFragment();
        bottomSheet.show(getChildFragmentManager(), "AddSongBottomSheet");
    }

    private boolean isUserAuthenticated() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    private void showAuthenticationRequiredDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Login Required")
                .setMessage("You need to login to add songs. Please go to the login screen to sign in.")
                .setPositiveButton("Go to Login", (dialog, which) -> {
                    navigator.navigate(com.prm.common.R.string.route_login);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}