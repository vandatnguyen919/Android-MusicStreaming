package com.prm.onboarding;

import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class OnboardingFragment extends Fragment {

    private OnboardingViewModel mViewModel;
    private ViewPager2 viewPager;
    private OnboardingPagerAdapter pagerAdapter;
    private Button btnNext;
    private ImageButton btnBack;
    private int currentPage = 0;

    public static OnboardingFragment newInstance() {
        return new OnboardingFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_onboarding, container, false);

        initViews(view);
        setupViewPager();
        setupClickListeners();

        return view;
    }

    private void initViews(View view) {
        viewPager = view.findViewById(R.id.viewpager_onboarding);
        btnNext = view.findViewById(R.id.btn_next);
        btnBack = view.findViewById(R.id.btn_back);
    }

    private void setupViewPager() {
        pagerAdapter = new OnboardingPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                currentPage = position;
                updateButtonText();
            }
        });
    }

    private void setupClickListeners() {
        if (btnNext != null) {
            btnNext.setOnClickListener(v -> {
                if (currentPage < 3) {
                    viewPager.setCurrentItem(currentPage + 1);
                } else {
                    // Handle completion
                    handleOnboardingComplete();
                }
            });
        }

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                if (currentPage > 0) {
                    viewPager.setCurrentItem(currentPage - 1);
                } else {
                    // Handle back navigation
                    requireActivity().onBackPressed();
                }
            });
        }
    }



    private void updateButtonText() {
        if (currentPage == 3) {
            btnNext.setVisibility(View.GONE); // Hide Next button on name screen as it has its own Create Account button
        } else {
            btnNext.setVisibility(View.VISIBLE);
            btnNext.setText("Next");
        }
    }

    private void handleOnboardingComplete() {
        // TODO: Handle onboarding completion
        // This could navigate to the main app or save user data
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(OnboardingViewModel.class);
        // TODO: Use the ViewModel
    }
}