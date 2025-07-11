package com.prm.library;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.prm.common.Navigator;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TestHomeFragment extends Fragment {

    @Inject
    Navigator navigator;

    public TestHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_test_home, container, false);

        Button goToLibraryButton = view.findViewById(R.id.btn_go_to_library);
        Button goToProfileButton = view.findViewById(R.id.btn_go_to_profile);

        goToLibraryButton.setOnClickListener(v -> {
            navigator.navigateToLibrary();
        });

        goToProfileButton.setOnClickListener(v -> {
            navigator.navigateToProfile();
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
} 