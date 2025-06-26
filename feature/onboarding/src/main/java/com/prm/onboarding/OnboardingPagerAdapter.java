package com.prm.onboarding;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class OnboardingPagerAdapter extends FragmentStateAdapter {

    private static final int NUM_PAGES = 4;

    public OnboardingPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public OnboardingPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return OnboardingEmailFragment.newInstance();
            case 1:
                return OnboardingPasswordFragment.newInstance();
            case 2:
                return OnboardingGenderFragment.newInstance();
            case 3:
                return OnboardingNameFragment.newInstance();
            default:
                return OnboardingEmailFragment.newInstance();
        }
    }

    @Override
    public int getItemCount() {
        return NUM_PAGES;
    }
}
