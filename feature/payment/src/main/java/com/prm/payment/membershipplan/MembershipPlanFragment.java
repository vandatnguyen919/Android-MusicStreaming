package com.prm.payment.membershipplan;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.prm.common.Navigator;
import com.prm.payment.R;

import javax.inject.Inject;

import coil3.ImageLoader;
import coil3.request.ImageRequest;
import coil3.request.SuccessResult;
import coil3.target.ImageViewTarget;
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

        ImageView imageView = view.findViewById(R.id.imageView);
        Button btnTryPremium = view.findViewById(R.id.btn_try_premium);
        Button btnOneTimePayment = view.findViewById(R.id.btn_one_time_payment);

        // Create ImageLoader instance
        ImageLoader imageLoader = new ImageLoader.Builder(requireContext()).build();

        // Create ImageRequest with success callback for animation
        ImageRequest request = new ImageRequest.Builder(requireContext())
                .data(R.drawable.bg_header)
                .target(new ImageViewTarget(imageView))
                .listener(new ImageRequest.Listener() {
                    @Override
                    public void onSuccess(@NonNull ImageRequest request, @NonNull SuccessResult result) {
                        // Animate visibility after successful load
                        imageView.setAlpha(0f);
                        imageView.animate()
                                .alpha(1f)
                                .setDuration(200)
                                .start();
                    }
                })
                .build();

        // Execute the request
        imageLoader.enqueue(request);

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