package com.prm.profile;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.prm.domain.model.User;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class EditProfileFragment extends Fragment {

    private static final String TAG = "EditProfileFragment";
    private EditProfileViewModel mViewModel;

    // UI elements
    private TextInputEditText etUsername;
    private TextInputEditText etEmail;
    private Button btnSaveProfile;
    private ProgressBar progressBar;

    private FirebaseUser currentUser;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        // Initialize UI elements
        etUsername = view.findViewById(R.id.et_edit_username);
        btnSaveProfile = view.findViewById(R.id.btn_save_profile);
        progressBar = view.findViewById(R.id.progress_bar);

        etUsername.setText(currentUser.getDisplayName());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(EditProfileViewModel.class);

        // Get user from arguments
        if (getArguments() != null && getArguments().containsKey("user")) {
            User user = (User) getArguments().getSerializable("user");
            if (user != null) {
                mViewModel.setUserToEdit(user);
            }
        }

        mViewModel.userToEdit.observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                etUsername.setText(user.getUsername());
                etEmail.setText(user.getEmail());
            }
        });

        // Observe loading state
        mViewModel.isLoading.observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            btnSaveProfile.setEnabled(!isLoading);
        });

        // Observe error messages
        mViewModel.error.observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                showToast(error);
                Log.e(TAG, "Error updating profile: " + error);
            }
        });

        // Observe update success
        mViewModel.updateSuccess.observe(getViewLifecycleOwner(), isSuccess -> {
            if (isSuccess != null && isSuccess) {
                showToast("Profile updated successfully!");
                NavController navController = Navigation.findNavController(view);
                navController.navigateUp(); // Navigate back to profile view
            }
        });

        btnSaveProfile.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
//            String email = etEmail.getText().toString().trim();
//
//            if (username.isEmpty() || email.isEmpty()) {
//                showToast("Username and Email cannot be empty.");
//                return;
//            }
//
//            User currentUser = mViewModel.userToEdit.getValue();
//            if (currentUser != null) {
//                User updatedUser = new User(
//                        currentUser.getId(),
//                        username,
//                        email,
//                        currentUser.getProfileImageUrl()
//                );
//                mViewModel.updateProfile(updatedUser);
//            } else {
//                showToast("User data not available for update.");
//            }

            if (currentUser != null) {
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(username)
                        .build();

                currentUser.updateProfile(profileUpdates)
                        .addOnSuccessListener(aVoid -> {
                            Log.d("ProfileUpdate", "Display name updated");
                        })
                        .addOnFailureListener(e -> {
                            Log.e("ProfileUpdate", "Failed to update display name", e);
                        });
            }

        });
    }

    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}