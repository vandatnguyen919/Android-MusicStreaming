package com.prm.musicstreaming.navigator;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.prm.common.Navigator;
import com.prm.musicstreaming.MainActivity;
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

    @Override
    public void navigateToHome(Context context) {
        try {
            Intent intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            // Log error for debugging
            android.util.Log.e("NavigatorImpl", "Error navigating to home", e);
        }
    }

    @Override
    public void navigateToAuth(Context context) {
        try {
            Class<?> authActivityClass = Class.forName("com.prm.login.AuthActivity");
            Intent intent = new Intent(context, authActivityClass);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
        } catch (ClassNotFoundException e) {
            // Fallback to login fragment navigation if AuthActivity not found
            getNavController().navigate(R.id.navigation_login);
        }
    }

    @Override
    public void navigateToLibrary() {
        getNavController().navigate(R.id.navigation_library);
    }

    @Override
    public void navigateToProfile() {
        getNavController().navigate(R.id.navigation_profile);
    }

    private NavController getNavController() {
        if (navController == null) {
            navController = Navigation.findNavController(activity, R.id.nav_host_fragment_activity_main);
        }
        return navController;
    }

    @Override
    public void navigateBack() {
        NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment_activity_main);
        navController.popBackStack();
    }
}
