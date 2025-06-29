package com.prm.musicstreaming.navigator;

import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.prm.common.Navigator;
import com.prm.musicstreaming.R;

import javax.inject.Inject;

public class NavigatorImpl implements Navigator {

    private final FragmentActivity activity;
    private NavController navController;

    @Inject
    public NavigatorImpl(FragmentActivity activity) {
        this.activity = activity;
    }

    @Override
    public void navigate(int route) {
        getNavController().navigate(activity.getString(route));
    }

    @Override
    public void navigate(String route) {
        getNavController().navigate(route);
    }

    @Override
    public void navigate(int route, boolean inclusive) {
        NavOptions navOptions = new NavOptions.Builder()
                .setPopUpTo(navController.getCurrentDestination().getId(), inclusive)
                .build();
        getNavController().navigate(route, null, navOptions);
    }

    @Override
    public void navigate(String route, boolean inclusive) {
        NavOptions navOptions = new NavOptions.Builder()
                .setPopUpTo(route, inclusive)
                .build();
        getNavController().navigate(route, navOptions);
    }

    private NavController getNavController() {
        if (navController == null) {
            navController = Navigation.findNavController(activity, R.id.nav_host_fragment_activity_main);
        }
        return navController;
    }
}
