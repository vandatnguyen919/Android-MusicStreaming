package com.prm.home;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.prm.common.util.MusicPlayerHelper;
import com.prm.common.util.SampleSongs;
import com.prm.domain.model.Artist;
import com.prm.domain.model.Song;
import com.prm.home.adapter.EditorPicksAdapter;
import com.prm.home.adapter.RecentlyPlayedAdapter;
import com.prm.home.adapter.ReviewAdapter;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private HomeViewModel mViewModel;

    @Inject
    Navigator navigator;

    @Inject
    MusicPlayerHelper musicPlayerHelper;

    // Adapters
    private RecentlyPlayedAdapter recentlyPlayedAdapter;
    private EditorPicksAdapter editorPicksAdapter;
    private ReviewAdapter reviewAdapter;

    // RecyclerViews
    private RecyclerView rvRecentlyPlayed;
    private RecyclerView rvEditorPicks;
    private RecyclerView rvReview;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize RecyclerViews
        initializeRecyclerViews(view);

        // Initialize test button
        Button button = view.findViewById(R.id.btn);
        button.setOnClickListener(v -> {
            try {
                Log.d(TAG, "Play button clicked");
                Song testSong = SampleSongs.getSampleSong();
                Log.d(TAG, "Sample song created: " + testSong.getTitle() + " - " + testSong.getUrl());
                musicPlayerHelper.playSong(testSong);
                Log.d(TAG, "Play song called successfully");
            } catch (Exception e) {
                Log.e(TAG, "Error playing song", e);
            }
        });

        return view;
    }

    private void initializeRecyclerViews(View view) {
        // Find RecyclerViews
        rvRecentlyPlayed = view.findViewById(R.id.rv_recently_played);
        rvEditorPicks = view.findViewById(R.id.rv_editor_picks);
        rvReview = view.findViewById(R.id.rv_review);

        // Initialize adapters
        recentlyPlayedAdapter = new RecentlyPlayedAdapter();
        editorPicksAdapter = new EditorPicksAdapter();
        reviewAdapter = new ReviewAdapter();

        // Set up RecyclerViews
        setupRecyclerView(rvRecentlyPlayed, recentlyPlayedAdapter);
        setupRecyclerView(rvEditorPicks, editorPicksAdapter);
        setupRecyclerView(rvReview, reviewAdapter);

        // Set click listeners
        recentlyPlayedAdapter.setOnItemClickListener(item -> {
            Log.d(TAG, "Recently played item clicked: " + item.getTitle());
            musicPlayerHelper.playSong(item.getSong());
        });

        editorPicksAdapter.setOnItemClickListener(item -> {
            Log.d(TAG, "Editor pick item clicked: " + item.getTitle());
            musicPlayerHelper.playSong(item.getSong());
        });

        reviewAdapter.setOnItemClickListener(item -> {
            Log.d(TAG, "Review item clicked: " + item.getTitle());
            musicPlayerHelper.playSong(item.getSong());
        });
    }

    private void setupRecyclerView(RecyclerView recyclerView, RecyclerView.Adapter<?> adapter) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // Observe UI model data
        observeViewModel();

        // Observe songs data (for logging)
        mViewModel.getSongs().observe(getViewLifecycleOwner(), songs -> {
            Log.d(TAG, "Songs loaded, count: " + songs.size());
            for (Song song : songs) {
                Log.d(TAG, "Song: " + song.getTitle() + " - ID: " + song.getId());
            }
        });

        // Observe artists data (for logging)
        mViewModel.getArtists().observe(getViewLifecycleOwner(), artists -> {
            Log.d(TAG, "Artists loaded, count: " + artists.size());
            for (Artist artist : artists) {
                Log.d(TAG, "Artist: " + artist.getName() + " - ID: " + artist.getId());
            }
        });

        // Observe loading state
        mViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            Log.d(TAG, "Loading state: " + isLoading);
            // TODO: Show/hide loading indicator
        });

        // Observe errors
        mViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Log.e(TAG, "Error loading data: " + error);
                // TODO: Show error message to user
            }
        });
    }

    private void observeViewModel() {
        // Observe Recently Played data
        mViewModel.getRecentlyPlayed().observe(getViewLifecycleOwner(), recentlyPlayedList -> {
            Log.d(TAG, "Recently played loaded, count: " + recentlyPlayedList.size());
            recentlyPlayedAdapter.setItems(recentlyPlayedList);
        });

        // Observe Editor Picks data
        mViewModel.getEditorPicks().observe(getViewLifecycleOwner(), editorPicksList -> {
            Log.d(TAG, "Editor picks loaded, count: " + editorPicksList.size());
            editorPicksAdapter.setItems(editorPicksList);
        });

        // Observe Reviews data
        mViewModel.getReviews().observe(getViewLifecycleOwner(), reviewsList -> {
            Log.d(TAG, "Reviews loaded, count: " + reviewsList.size());
            reviewAdapter.setItems(reviewsList);
        });
    }
}