package com.prm.musicstreaming.navigator;

import androidx.annotation.StringRes;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.prm.common.Navigator;
import com.prm.musicstreaming.MainActivity;
import com.prm.musicstreaming.MapsActivity;
import com.prm.musicstreaming.R;

import javax.inject.Inject;

import android.content.Intent;
import android.os.Bundle;

public class NavigatorImpl implements Navigator {

    private final FragmentActivity activity;
    private NavController navController;

    private NavController getNavController() {
        if (navController == null) {
            navController = Navigation.findNavController(activity, R.id.nav_host_fragment_activity_main);
        }
        return navController;
    }

    @Inject
    public NavigatorImpl(FragmentActivity activity) {
        this.activity = activity;
    }

    @Override
    public void navigate(int route) {
        this.navigate(activity.getString(route));
    }

    @Override
    public void navigate(String route) {
        getNavController().navigate(route);
    }

    @Override
    public void clearAndNavigate(@StringRes int route) {
        this.clearAndNavigate(activity.getString(route));
    }

    @Override
    public void clearAndNavigate(String route) {
        NavOptions navOptions = new NavOptions.Builder()
                .setPopUpTo(R.id.mobile_navigation, true)
                .build();
        getNavController().navigate(route, navOptions);
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
    public void navigateToLibrary() {
        getNavController().navigate(R.id.navigation_library);
    }

    @Override
    public void navigateToProfile() {
        getNavController().navigate(R.id.navigation_profile);
    }

    @Override
    public void navigateBack() {
        NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment_activity_main);
        navController.popBackStack();
    }

    @Override
    public void navigateToPlaylistDetail(String playlistId) {
        Bundle bundle = new Bundle();
        bundle.putString("playlistId", playlistId);
        navController.navigate(R.id.action_library_to_playlist_detail, bundle);
    }

    @Override
    public void navigateToTrackView(String songId) {
        Bundle bundle = new Bundle();
        bundle.putString("songId", songId);
        navController.navigate(R.id.action_playlist_detail_to_track_view, bundle);
    }

    @Override
    public void bind(NavController navController) {
        this.navController = navController;
    }

    @Override
    public void unbind() {

    }

    @Override
    public void navigateToMaps() {
        Intent intent = new Intent(activity, MapsActivity.class);
        activity.startActivity(intent);
    }
}
