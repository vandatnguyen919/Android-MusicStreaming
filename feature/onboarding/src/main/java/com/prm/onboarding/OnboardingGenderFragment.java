package com.prm.onboarding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class OnboardingGenderFragment extends Fragment {

    private Spinner spinnerGender;

    public static OnboardingGenderFragment newInstance() {
        return new OnboardingGenderFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_onboarding_gender, container, false);
        
        spinnerGender = view.findViewById(R.id.spinner_gender);
        
        // Set up spinner adapter
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.gender_options,
                R.layout.spinner_item
        );
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerGender.setAdapter(adapter);
        
        return view;
    }

    public String getSelectedGender() {
        if (spinnerGender != null && spinnerGender.getSelectedItemPosition() > 0) {
            return spinnerGender.getSelectedItem().toString();
        }
        return "";
    }

    public boolean isValid() {
        return spinnerGender != null && spinnerGender.getSelectedItemPosition() > 0;
    }
}
