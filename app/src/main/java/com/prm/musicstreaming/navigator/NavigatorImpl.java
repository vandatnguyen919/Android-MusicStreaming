package com.prm.musicstreaming.navigator;

import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.prm.common.Navigator;
import com.prm.musicstreaming.R;

import javax.inject.Inject;

public class NavigatorImpl implements Navigator {

    private final FragmentActivity activity;

    @Inject
    public NavigatorImpl(FragmentActivity activity) {
        this.activity = activity;
    }

    @Override
    public void navigate(int route) {
        NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment_activity_main);
        navController.navigate(activity.getString(route));
    }

    @Override
    public void navigate(String route) {
        NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment_activity_main);
        navController.navigate(route);
    }
}
