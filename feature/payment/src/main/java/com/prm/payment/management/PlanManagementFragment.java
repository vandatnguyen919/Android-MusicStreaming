package com.prm.payment.management;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.prm.common.Navigator;
import com.prm.payment.R;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PlanManagementFragment extends Fragment {

    private PlanManagementViewModel mViewModel;

    @Inject
    Navigator navigator;

    public static PlanManagementFragment newInstance() {
        return new PlanManagementFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_plan_management, container, false);

        view.findViewById(R.id.membership_plan_card).setOnClickListener(v -> {
            navigator.navigate(com.prm.common.R.string.route_plan_management_details);
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(PlanManagementViewModel.class);
    }
}