package com.prm.library;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.prm.common.Navigator;
import com.prm.domain.model.Playlist;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LibraryFragment extends Fragment implements CreatePlaylistDialogFragment.CreatePlaylistDialogListener {

    private static final String TAG = "LibraryFragment";
    private LibraryViewModel mViewModel;
    private RecyclerView playlistsRecyclerView;
    private PlaylistAdapter playlistAdapter;
    private ProgressBar progressBar;

    @Inject
    Navigator navigator;

    public static LibraryFragment newInstance() {
        return new LibraryFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library, container, false);

        ImageView addPlaylistButton = view.findViewById(R.id.iv_add_playlist);
        addPlaylistButton.setOnClickListener(v -> {
            CreatePlaylistDialogFragment dialog = new CreatePlaylistDialogFragment();
            dialog.setCreatePlaylistDialogListener(this);
            dialog.show(getParentFragmentManager(), "CreatePlaylistDialog");
        });

        playlistsRecyclerView = view.findViewById(R.id.rv_playlists);
        playlistsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        playlistAdapter = new PlaylistAdapter();
        playlistsRecyclerView.setAdapter(playlistAdapter);

        progressBar = view.findViewById(R.id.progress_bar);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(LibraryViewModel.class);

        // Observe playlists data
        mViewModel.getPlaylists().observe(getViewLifecycleOwner(), playlists -> {
            // Log each playlist from the repository
            Log.d(TAG, "Playlists loaded, count: " + playlists.size());
            for (Playlist playlist : playlists) {
                Log.d(TAG, "Playlist: " + playlist.getName() + " - ID: " + playlist.getId());
            }
            playlistAdapter.setPlaylists(playlists);
        });

        // Observe loading state
        mViewModel.isLoading.observe(getViewLifecycleOwner(), isLoading -> {
            if (progressBar != null) {
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
            Log.d(TAG, "Loading: " + isLoading);
        });

        // Observe error messages
        mViewModel.error.observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
                Log.e(TAG, "Error: " + error);
            }
        });

        // Trigger initial playlist load if needed
        mViewModel.fetchPlaylistsForUser();
    }

    @Override
    public void onPlaylistNameEntered(String playlistName) {
        mViewModel.createPlaylist(playlistName);
    }
}