package com.prm.home;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.prm.common.Navigator;
import com.prm.domain.model.Artist;
import com.prm.domain.model.Song;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private HomeViewModel mViewModel;

    @Inject
    Navigator navigator;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        Button button = view.findViewById(R.id.btn);
        button.setOnClickListener(v -> navigator.navigate(com.prm.common.R.string.route_album));

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // Observe songs data
        mViewModel.getSongs().observe(getViewLifecycleOwner(), songs -> {
            // Log each song from the repository
            Log.d(TAG, "Songs loaded, count: " + songs.size());
            for (Song song : songs) {
                Log.d(TAG, "Song: " + song.getTitle() + " - ID: " + song.getId());
            }
        });

        // Observe artists data
        mViewModel.getArtists().observe(getViewLifecycleOwner(), artists -> {
            // Log each artist from the repository
            Log.d(TAG, "Artists loaded, count: " + artists.size());
            for (Artist artist : artists) {
                Log.d(TAG, "Artist: " + artist.getName() + " - ID: " + artist.getId());
            }
        });

        // Observe loading state
        mViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            Log.d(TAG, "Loading state: " + isLoading);
        });

        // Observe errors
        mViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Log.e(TAG, "Error loading songs: " + error);
            }
        });
    }
}