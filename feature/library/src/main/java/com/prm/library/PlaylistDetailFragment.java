package com.prm.library;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.prm.common.Navigator;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PlaylistDetailFragment extends Fragment {
    private static final String ARG_PLAYLIST_ID = "playlist_id";
    private String playlistId;
    private LibraryViewModel mViewModel;
    private SongInPlaylistAdapter songAdapter;
    private TextView tvPlaylistName, tvPlaylistDescription;

    @Inject
    Navigator navigator;

    public static PlaylistDetailFragment newInstance(String playlistId) {
        PlaylistDetailFragment fragment = new PlaylistDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PLAYLIST_ID, playlistId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            playlistId = getArguments().getString("playlistId");
            android.util.Log.d("PlaylistDetailFragment", "playlistId = " + playlistId);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlist_detail, container, false);
        tvPlaylistName = view.findViewById(R.id.tvPlaylistName);
        tvPlaylistDescription = view.findViewById(R.id.tvPlaylistDescription);
        RecyclerView rvSongs = view.findViewById(R.id.rvSongsInPlaylist);
        rvSongs.setLayoutManager(new LinearLayoutManager(getContext()));
        songAdapter = new SongInPlaylistAdapter();
        rvSongs.setAdapter(songAdapter);
        songAdapter.setOnSongActionListener(new SongInPlaylistAdapter.OnSongActionListener() {
            @Override
            public void onRemoveSong(com.prm.domain.model.Song song) {
                if (playlistId != null) {
                    mViewModel.removeSongFromPlaylist(playlistId, song.getId());
                }
            }
            @Override
            public void onSongClicked(com.prm.domain.model.Song song) {
                navigator.navigateToTrackView(song.getId());
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(requireActivity()).get(LibraryViewModel.class);
        // Bind NavController to navigator
        androidx.navigation.NavController navController = androidx.navigation.fragment.NavHostFragment.findNavController(this);
        navigator.bind(navController);
        if (playlistId == null) {
            android.util.Log.e("PlaylistDetailFragment", "playlistId is null!");
            android.widget.Toast.makeText(getContext(), "Playlist not found!", android.widget.Toast.LENGTH_SHORT).show();
            return;
        }
        mViewModel.getPlaylistById(playlistId).observe(getViewLifecycleOwner(), playlist -> {
            if (playlist != null) {
                tvPlaylistName.setText(playlist.getName());
                List<com.prm.domain.model.Song> songs = playlist.getSongs();
                if (songs != null) {
                    android.util.Log.d("PlaylistDetailFragment", "Number of songs: " + songs.size());
                    songAdapter.setSongs(songs);
                }
            } else {
                android.util.Log.e("PlaylistDetailFragment", "Playlist not found for id: " + playlistId);
                android.widget.Toast.makeText(getContext(), "Playlist not found!", android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }
} 