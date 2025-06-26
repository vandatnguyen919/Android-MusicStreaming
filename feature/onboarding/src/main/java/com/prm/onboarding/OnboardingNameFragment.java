package com.prm.onboarding;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class OnboardingNameFragment extends Fragment {

    private EditText etName;
    private ImageView ivCheck;
    private CheckBox cbMarketing;
    private CheckBox cbShareData;
    private Button btnCreateAccount;
    private TextView tvTermsLink;
    private TextView tvPrivacyLink;

    public static OnboardingNameFragment newInstance() {
        return new OnboardingNameFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_onboarding_name, container, false);
        
        initViews(view);
        setupListeners();
        
        return view;
    }

    private void initViews(View view) {
        etName = view.findViewById(R.id.et_name);
        ivCheck = view.findViewById(R.id.iv_check);
        cbMarketing = view.findViewById(R.id.cb_marketing);
        cbShareData = view.findViewById(R.id.cb_share_data);
        btnCreateAccount = view.findViewById(R.id.btn_create_account);
        tvTermsLink = view.findViewById(R.id.tv_terms_link);
        tvPrivacyLink = view.findViewById(R.id.tv_privacy_link);
    }

    private void setupListeners() {
        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    ivCheck.setVisibility(View.VISIBLE);
                } else {
                    ivCheck.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnCreateAccount.setOnClickListener(v -> {
            // Handle create account
            handleCreateAccount();
        });

        tvTermsLink.setOnClickListener(v -> {
            // Open Terms of Use
        });

        tvPrivacyLink.setOnClickListener(v -> {
            // Open Privacy Policy
        });
    }

    private void handleCreateAccount() {
        // TODO: Implement account creation logic
    }

    public String getName() {
        return etName != null ? etName.getText().toString().trim() : "";
    }

    public boolean isMarketingOptIn() {
        return cbMarketing != null && cbMarketing.isChecked();
    }

    public boolean isDataSharingOptIn() {
        return cbShareData != null && cbShareData.isChecked();
    }

    public boolean isValid() {
        String name = getName();
        return !name.isEmpty();
    }
}
