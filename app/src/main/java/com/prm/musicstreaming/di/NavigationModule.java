package com.prm.musicstreaming.di;

import android.content.Context;

import androidx.fragment.app.FragmentActivity;

import com.prm.common.Navigator;
import com.prm.musicstreaming.navigator.NavigatorImpl;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;
import dagger.hilt.android.qualifiers.ActivityContext;

@Module
@InstallIn(ActivityComponent.class)
public class NavigationModule {

    @Provides
    public Navigator provideNavigator(@ActivityContext Context context) {
        return new NavigatorImpl((FragmentActivity) context);
    }
}