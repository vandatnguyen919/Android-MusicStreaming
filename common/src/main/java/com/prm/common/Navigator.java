package com.prm.common;

import androidx.annotation.IdRes;
import androidx.annotation.StringRes;
import androidx.navigation.NavController;

public interface Navigator {

    void navigate(@StringRes int route);

    void navigate(String route);

    void navigate(@IdRes int route, boolean inclusive);

    void navigate(String route, boolean inclusive);

    void navigateToLibrary();

    void navigateToProfile();

    void navigateBack();

    void clearAndNavigate(@StringRes int route);

    void clearAndNavigate(String route);

    void navigateToPlaylistDetail(String playlistId);
    void navigateToTrackView(String songId);
    void bind(NavController navController);
    void unbind();

    void navigateToMaps();
}
