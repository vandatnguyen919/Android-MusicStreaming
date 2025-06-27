package com.prm.payment.membershipplan;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.prm.common.Navigator;
import com.prm.payment.R;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MembershipPlanFragment extends Fragment {

    private MembershipPlanViewModel mViewModel;

    @Inject
    Navigator navigator;

    public static MembershipPlanFragment newInstance() {
        return new MembershipPlanFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_membership_plan, container, false);

        Button btnTryPremium = view.findViewById(R.id.btn_try_premium);
        Button btnOneTimePayment = view.findViewById(R.id.btn_one_time_payment);

        btnTryPremium.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Starting Premium subscription...", Toast.LENGTH_SHORT).show();
            navigator.navigate(com.prm.common.R.string.route_checkout);
        });

        btnOneTimePayment.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Processing one-time payment...", Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MembershipPlanViewModel.class);
    }
}