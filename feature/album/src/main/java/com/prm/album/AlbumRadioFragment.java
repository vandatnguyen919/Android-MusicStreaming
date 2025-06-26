package com.prm.album;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prm.album.adapter.RadioSongAdapter;
import com.prm.album.model.RadioSongUiModel;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AlbumRadioFragment extends Fragment implements 
    RadioSongAdapter.OnRadioSongClickListener {

    // Views
    private ImageButton btnClose;
    private ImageView ivNowPlayingCover;
    private TextView tvNowPlayingTitle;
    private TextView tvNowPlayingArtist;
    private RecyclerView rvRadioSongs;
    private ImageButton btnShuffle;
    private ImageButton btnPrevious;
    private ImageButton btnPlayPause;
    private ImageButton btnNext;
    private ImageButton btnRepeat;

    // Adapter
    private RadioSongAdapter radioSongAdapter;

    public static AlbumRadioFragment newInstance() {
        return new AlbumRadioFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_album_radio, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupRecyclerView();
        setupClickListeners();
        setupDummyData();
    }

    private void initViews(View view) {
        btnClose = view.findViewById(R.id.btn_close);
        ivNowPlayingCover = view.findViewById(R.id.iv_now_playing_cover);
        tvNowPlayingTitle = view.findViewById(R.id.tv_now_playing_title);
        tvNowPlayingArtist = view.findViewById(R.id.tv_now_playing_artist);
        rvRadioSongs = view.findViewById(R.id.rv_radio_songs);
        btnShuffle = view.findViewById(R.id.btn_shuffle);
        btnPrevious = view.findViewById(R.id.btn_previous);
        btnPlayPause = view.findViewById(R.id.btn_play_pause);
        btnNext = view.findViewById(R.id.btn_next);
        btnRepeat = view.findViewById(R.id.btn_repeat);
    }

    private void setupRecyclerView() {
        radioSongAdapter = new RadioSongAdapter();
        radioSongAdapter.setOnRadioSongClickListener(this);
        rvRadioSongs.setLayoutManager(new LinearLayoutManager(getContext()));
        rvRadioSongs.setAdapter(radioSongAdapter);
    }

    private void setupClickListeners() {
        btnClose.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        btnShuffle.setOnClickListener(v -> {
            // TODO: Toggle shuffle
        });

        btnPrevious.setOnClickListener(v -> {
            // TODO: Previous song
        });

        btnPlayPause.setOnClickListener(v -> {
            // TODO: Toggle play/pause
        });

        btnNext.setOnClickListener(v -> {
            // TODO: Next song
        });

        btnRepeat.setOnClickListener(v -> {
            // TODO: Toggle repeat
        });
    }

    private void setupDummyData() {
        // Set now playing info
        tvNowPlayingTitle.setText("From Me to You - Mono / Remastered");
        tvNowPlayingArtist.setText("Watched Out");
        ivNowPlayingCover.setBackgroundColor(getResources().getColor(R.color.primary_red));

        // Create dummy radio songs
        List<RadioSongUiModel> radioSongs = new ArrayList<>();
        radioSongs.add(new RadioSongUiModel("1", "Troubled Paradise", "Slayyyter", false));
        radioSongs.add(new RadioSongUiModel("2", "Walk Like a Man", "Frankie Valli & The Four Seasons", false));
        radioSongs.add(new RadioSongUiModel("3", "Don't Let Me Down - Remastered 2009", "The Beatles", false));
        radioSongs.add(new RadioSongUiModel("4", "Eleanor", "The Turtles", false));
        radioSongs.add(new RadioSongUiModel("5", "Hey Moon", "John Maus", false));
        radioSongs.add(new RadioSongUiModel("6", "home with you", "FKA twigs", false));
        radioSongs.add(new RadioSongUiModel("7", "Mercurial World", "Magdalena Bay", false));
        radioSongs.add(new RadioSongUiModel("8", "Hound Dog", "Elvis Presley", false));

        radioSongAdapter.setRadioSongs(radioSongs);
    }

    // RadioSongAdapter.OnRadioSongClickListener implementation
    @Override
    public void onRadioSongClick(RadioSongUiModel song) {
        // TODO: Play selected song
    }

    @Override
    public void onRadioSongMoreClick(RadioSongUiModel song) {
        // TODO: Show song options
    }
}
