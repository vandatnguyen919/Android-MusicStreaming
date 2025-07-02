package com.prm.search;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prm.common.Navigator;
import com.prm.domain.model.Album;
import com.prm.domain.model.Artist;
import com.prm.domain.model.Playlist;
import com.prm.domain.model.Song;
import com.prm.search.adapters.AlbumAdapter;
import com.prm.search.adapters.ArtistAdapter;
import com.prm.search.adapters.PlaylistAdapter;
import com.prm.search.adapters.SongAdapter;

import java.util.Collections;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SearchResultsFragment extends Fragment implements 
        ArtistAdapter.OnArtistClickListener,
        SongAdapter.OnSongClickListener,
        AlbumAdapter.OnAlbumClickListener,
        PlaylistAdapter.OnPlaylistClickListener {

    private SearchViewModel viewModel;
    private EditText etSearch;
    private TextView tvCancel;
    private RecyclerView rvArtists;
    private RecyclerView rvSongs;
    private RecyclerView rvAlbums;
    private RecyclerView rvPlaylists;
    private ProgressBar progressBar;
    private View emptyResultsView;
    private ArtistAdapter artistAdapter;
    private SongAdapter songAdapter;
    private AlbumAdapter albumAdapter;
    private PlaylistAdapter playlistAdapter;
    
    @Inject
    Navigator navigator;

    public static SearchResultsFragment newInstance() {
        return new SearchResultsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_result, container, false);
        initViews(view);
        return view;
    }
    
    private void initViews(View view) {
        etSearch = view.findViewById(R.id.et_search);
        tvCancel = view.findViewById(R.id.btn_cancel);
        progressBar = view.findViewById(R.id.progress_bar);
        
        // Khởi tạo RecyclerView cho artists
        rvArtists = view.findViewById(R.id.rv_artists);
        rvArtists.setLayoutManager(new LinearLayoutManager(requireContext()));
        artistAdapter = new ArtistAdapter(this);
        rvArtists.setAdapter(artistAdapter);
        
        // Khởi tạo RecyclerView cho songs
        rvSongs = view.findViewById(R.id.rv_songs);
        rvSongs.setLayoutManager(new LinearLayoutManager(requireContext()));
        songAdapter = new SongAdapter(this);
        rvSongs.setAdapter(songAdapter);
        
        // Khởi tạo RecyclerView cho albums
        rvAlbums = view.findViewById(R.id.rv_albums);
        rvAlbums.setLayoutManager(new LinearLayoutManager(requireContext()));
        albumAdapter = new AlbumAdapter(this);
        rvAlbums.setAdapter(albumAdapter);
        
        // Khởi tạo RecyclerView cho playlists
        rvPlaylists = view.findViewById(R.id.rv_playlists);
        rvPlaylists.setLayoutManager(new LinearLayoutManager(requireContext()));
        playlistAdapter = new PlaylistAdapter(this);
        rvPlaylists.setAdapter(playlistAdapter);
        
        tvCancel.setOnClickListener(v -> requireActivity().getOnBackPressedDispatcher().onBackPressed());
        
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.onSearchQueryChanged(s.toString());
            }
        });

        // Automatically focus and show keyboard when fragment is created
        etSearch.requestFocus();
        etSearch.post(() -> {
            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(etSearch, InputMethodManager.SHOW_IMPLICIT);
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(SearchViewModel.class);
        observeViewModel();
    }
    
    private void observeViewModel() {
        viewModel.artists.observe(getViewLifecycleOwner(), artists -> {
            if (artists != null) {
                View header = getView().findViewById(R.id.tv_artists_header);
                header.setVisibility(artists.isEmpty() ? View.GONE : View.VISIBLE);
                
                artistAdapter.submitList(artists);
                rvArtists.setVisibility(artists.isEmpty() ? View.GONE : View.VISIBLE);
                
                android.util.Log.d("SearchFragment", "Artists updated: " + artists.size() + " items");
                for (com.prm.domain.model.Artist artist : artists) {
                    android.util.Log.d("SearchFragment", "Artist: " + artist.getName());
                }
            } else {
                artistAdapter.submitList(Collections.emptyList());
                rvArtists.setVisibility(View.GONE);
                getView().findViewById(R.id.tv_artists_header).setVisibility(View.GONE);
            }
        });
        
        viewModel.songs.observe(getViewLifecycleOwner(), songs -> {
            if (songs != null) {
                View header = getView().findViewById(R.id.tv_songs_header);
                header.setVisibility(songs.isEmpty() ? View.GONE : View.VISIBLE);
                
                songAdapter.submitList(songs);
                rvSongs.setVisibility(songs.isEmpty() ? View.GONE : View.VISIBLE);
                
                android.util.Log.d("SearchFragment", "Songs updated: " + songs.size() + " items");
            } else {
                songAdapter.submitList(Collections.emptyList());
                rvSongs.setVisibility(View.GONE);
                getView().findViewById(R.id.tv_songs_header).setVisibility(View.GONE);
            }
        });
        
        viewModel.albums.observe(getViewLifecycleOwner(), albums -> {
            if (albums != null) {
                View header = getView().findViewById(R.id.tv_albums_header);
                header.setVisibility(albums.isEmpty() ? View.GONE : View.VISIBLE);
                
                albumAdapter.submitList(albums);
                rvAlbums.setVisibility(albums.isEmpty() ? View.GONE : View.VISIBLE);
                
                android.util.Log.d("SearchFragment", "Albums updated: " + albums.size() + " items");
            } else {
                albumAdapter.submitList(Collections.emptyList());
                rvAlbums.setVisibility(View.GONE);
                getView().findViewById(R.id.tv_albums_header).setVisibility(View.GONE);
            }
        });
        
        viewModel.playlists.observe(getViewLifecycleOwner(), playlists -> {
            if (playlists != null) {
                View header = getView().findViewById(R.id.tv_playlists_header);
                header.setVisibility(playlists.isEmpty() ? View.GONE : View.VISIBLE);
                
                playlistAdapter.submitList(playlists);
                rvPlaylists.setVisibility(playlists.isEmpty() ? View.GONE : View.VISIBLE);
                
                android.util.Log.d("SearchFragment", "Playlists updated: " + playlists.size() + " items");
            } else {
                playlistAdapter.submitList(Collections.emptyList());
                rvPlaylists.setVisibility(View.GONE);
                getView().findViewById(R.id.tv_playlists_header).setVisibility(View.GONE);
            }
        });
        
        viewModel.isLoading.observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });
        
        viewModel.isEmpty.observe(getViewLifecycleOwner(), isEmpty -> {
            if (isEmpty && !etSearch.getText().toString().trim().isEmpty()) {
                Toast.makeText(requireContext(), "No results found", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    @Override
    public void onArtistClick(Artist artist) {
        Toast.makeText(requireContext(), "Selected artist: " + artist.getName(), Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public void onSongClick(Song song) {
        Toast.makeText(requireContext(), "Playing song: " + song.getTitle(), Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public void onSongOptionsClick(Song song, View view) {
        Toast.makeText(requireContext(), "Options for song: " + song.getTitle(), Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public void onAlbumClick(Album album) {
        Toast.makeText(requireContext(), "Selected album: " + album.getName(), Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public void onPlaylistClick(Playlist playlist) {
        Toast.makeText(requireContext(), "Selected playlist: " + playlist.getName(), Toast.LENGTH_SHORT).show();
    }
}
