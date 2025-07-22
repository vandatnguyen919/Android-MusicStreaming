package com.prm.profile;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.prm.common.CommonRoutes;
import com.prm.common.Navigator;
import com.prm.profile.chatbot.ChatbotDialogFragment;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
//    private ProfileViewModel mViewModel;
    private ImageView ivProfile;
    private TextView tvUsername;
    private TextView tvEmail;
    private Button btnEditProfile;
    private Button btnLogout;

    @Inject
    Navigator navigator;

    private FirebaseAuth firebaseAuth;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        firebaseAuth = FirebaseAuth.getInstance();
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
        btnLogout = view.findViewById(R.id.btnLogout);

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        Glide.with(this)
                .load(currentUser.getPhotoUrl())
                .placeholder(R.drawable.ic_profile)
                .error(R.drawable.ic_profile)
                .circleCrop()
                .into(ivProfile);
        tvUsername.setText(currentUser.getDisplayName());
        tvEmail.setText(currentUser.getEmail());

        btnEditProfile.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(CommonRoutes.EDIT_PROFILE);
        });

        btnLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Confirm Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        firebaseAuth.signOut();
                        Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();
                        navigator.clearAndNavigate(com.prm.common.R.string.route_login);
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        FloatingActionButton fabChatbot = view.findViewById(R.id.fabChatbot);

        fabChatbot.setOnClickListener(v -> {
            ChatbotDialogFragment.newInstance().show(getChildFragmentManager(), ChatbotDialogFragment.TAG);
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        mViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(com.prm.common.R.menu.add_menu, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();

                if (id == com.prm.common.R.id.action_add) {
                    if (isUserAuthenticated()) {
                        showAddSongDialog();
                    } else {
                        showAuthenticationRequiredDialog();
                    }
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
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