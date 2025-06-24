package com.prm.album;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TrackViewFragment extends Fragment {

    // Views
    private ImageButton btnBack;
    private ImageButton btnMoreOptions;
    private ImageView ivAlbumCover;
    private TextView tvSongTitle;
    private TextView tvArtistName;
    private ImageButton btnLike;
    private SeekBar seekBarProgress;
    private TextView tvCurrentTime;
    private TextView tvTotalTime;
    private ImageButton btnShuffle;
    private ImageButton btnPrevious;
    private ImageButton btnPlayPause;
    private ImageButton btnNext;
    private ImageButton btnRepeat;
    private ImageButton btnQueue;
    private ImageButton btnShare;

    public static TrackViewFragment newInstance() {
        return new TrackViewFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_track_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupClickListeners();
        setupDummyData();
    }

    private void initViews(View view) {
        btnBack = view.findViewById(R.id.btn_back);
        btnMoreOptions = view.findViewById(R.id.btn_more_options);
        ivAlbumCover = view.findViewById(R.id.iv_album_cover);
        tvSongTitle = view.findViewById(R.id.tv_song_title);
        tvArtistName = view.findViewById(R.id.tv_artist_name);
        btnLike = view.findViewById(R.id.btn_like);
        seekBarProgress = view.findViewById(R.id.seek_bar_progress);
        tvCurrentTime = view.findViewById(R.id.tv_current_time);
        tvTotalTime = view.findViewById(R.id.tv_total_time);
        btnShuffle = view.findViewById(R.id.btn_shuffle);
        btnPrevious = view.findViewById(R.id.btn_previous);
        btnPlayPause = view.findViewById(R.id.btn_play_pause);
        btnNext = view.findViewById(R.id.btn_next);
        btnRepeat = view.findViewById(R.id.btn_repeat);
        btnQueue = view.findViewById(R.id.btn_queue);
        btnShare = view.findViewById(R.id.btn_share);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        btnMoreOptions.setOnClickListener(v -> {
            // TODO: Show more options
        });

        btnLike.setOnClickListener(v -> {
            // TODO: Toggle like
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

        btnQueue.setOnClickListener(v -> {
            // TODO: Show queue
        });

        btnShare.setOnClickListener(v -> {
            // TODO: Share song
        });

        seekBarProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    // TODO: Update playback position
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO: Pause progress updates
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO: Resume progress updates
            }
        });
    }

    private void setupDummyData() {
        // Set dummy data for preview
        tvSongTitle.setText("From Me to You - Mono / Remastered");
        tvArtistName.setText("The Beatles");
        tvCurrentTime.setText("1:23");
        tvTotalTime.setText("2:23");
        seekBarProgress.setProgress(30);
        
        // Set album cover background color (placeholder)
        ivAlbumCover.setBackgroundColor(getResources().getColor(R.color.primary_red));
    }
}
