package com.prm.onboarding.permission;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.prm.onboarding.R;

public class NotificationPermissionFragment extends Fragment {

    private NotificationPermissionViewModel mViewModel;

    public static NotificationPermissionFragment newInstance() {
        return new NotificationPermissionFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification_permission, container, false);
        Button btnAllow = view.findViewById(R.id.btn_allow);
        btnAllow.setOnClickListener(v -> requestNotificationPermission());
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(NotificationPermissionViewModel.class);
    }

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permission granted
                    onPermissionGranted();
                } else {
                    // Permission denied
                    onPermissionDenied();
                }
            });

    private void requestNotificationPermission() {
        // Check if we're running on Android 13 (API level 33) or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                onPermissionGranted();
                return;
            }
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
        } else {
            // For Android 12 and below, notifications are enabled by default
            onPermissionGranted();
        }
    }

    private void onPermissionGranted() {
        Toast.makeText(requireContext(), "Notification permission granted!", Toast.LENGTH_SHORT).show();
    }

    private void onPermissionDenied() {
        Toast.makeText(requireContext(), "You can enable notifications later in Settings", Toast.LENGTH_LONG).show();
    }
}