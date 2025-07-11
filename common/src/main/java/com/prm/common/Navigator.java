package com.prm.common;

import android.content.Context;

import androidx.annotation.IdRes;
import androidx.annotation.StringRes;

public interface Navigator {

    void navigate(@StringRes int route);

    void navigate(String route);

    void navigate(@IdRes int route, boolean inclusive);

    void navigate(String route, boolean inclusive);

    void navigateToHome(Context context);

    void navigateToAuth(Context context);

    void navigateToLibrary();

    void navigateToProfile();

    void navigateBack();
}
