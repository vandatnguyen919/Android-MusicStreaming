package com.prm.onboarding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class OnboardingEmailFragment extends Fragment {

    private EditText etEmail;

    public static OnboardingEmailFragment newInstance() {
        return new OnboardingEmailFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_onboarding_email, container, false);
        
        etEmail = view.findViewById(R.id.et_email);
        
        return view;
    }

    public String getEmail() {
        return etEmail != null ? etEmail.getText().toString().trim() : "";
    }

    public boolean isValid() {
        String email = getEmail();
        return !email.isEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
