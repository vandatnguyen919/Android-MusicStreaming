package com.prm.album;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.prm.common.MiniPlayerViewModel;
import com.prm.domain.model.Song;

import java.util.concurrent.TimeUnit;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TrackViewFragment extends Fragment implements
    TrackOptionsBottomSheetFragment.OnTrackOptionsListener,
    ShareBottomSheetFragment.OnShareListener {

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

    // Bottom Sheets
    private TrackOptionsBottomSheetFragment trackOptionsBottomSheet;
    private ShareBottomSheetFragment shareBottomSheet;

    private MiniPlayerViewModel miniPlayerViewModel;
    private boolean isUserSeeking = false;
    private final Handler progressHandler = new Handler(Looper.getMainLooper());
    private Runnable progressRunnable;

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
        miniPlayerViewModel = new ViewModelProvider(requireActivity()).get(MiniPlayerViewModel.class);

        initViews(view);
        setupClickListeners();
        setupObservers();
        setupProgressUpdater();
    }

    private void initViews(View view) {
        btnBack = view.findViewById(R.id.btn_back);
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
            // Handle back navigation
            getParentFragmentManager().popBackStack();
        });

//        btnMoreOptions.setOnClickListener(v -> {
//            showTrackOptionsBottomSheet();
//        });

        btnLike.setOnClickListener(v -> {
            // TODO: Toggle like
        });

        btnShuffle.setOnClickListener(v -> {
            // TODO: Toggle shuffle
        });

        btnPrevious.setOnClickListener(v -> {
            miniPlayerViewModel.skipToPrevious();
        });

        btnPlayPause.setOnClickListener(v -> {
            miniPlayerViewModel.playPause();
        });

        btnNext.setOnClickListener(v -> {
            miniPlayerViewModel.skipToNext();
        });

        btnRepeat.setOnClickListener(v -> {
            // TODO: Toggle repeat
        });

        btnQueue.setOnClickListener(v -> {
            // TODO: Show queue
        });

        btnShare.setOnClickListener(v -> {
            showShareBottomSheet();
        });

        seekBarProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    // Update time display while user is dragging
                    long newPosition = (long) progress * 1000; // Convert to milliseconds
                    tvCurrentTime.setText(formatTime(newPosition));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isUserSeeking = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isUserSeeking = false;
                long newPosition = (long) seekBar.getProgress() * 1000; // Convert to milliseconds
                miniPlayerViewModel.seekTo(newPosition);
            }
        });
    }

    private void setupObservers() {
        // Observe current song
        miniPlayerViewModel.getCurrentSong().observe(getViewLifecycleOwner(), song -> {
            if (song != null) {
                updateSongInfo(song);
                // Update play/pause button based on current song and playing state
                updatePlayPauseButtonForCurrentSong();
            } else {
                // No current song, reset to play state
                btnPlayPause.setImageResource(android.R.drawable.ic_media_play);
            }
        });

        // Observe play state
        miniPlayerViewModel.getIsPlaying().observe(getViewLifecycleOwner(), isPlaying -> {
            // Only update if there's a current song
            Song currentSong = miniPlayerViewModel.getCurrentSong().getValue();
            if (currentSong != null) {
                updatePlayPauseButton(isPlaying);
            }
        });

        // Observe current position
        miniPlayerViewModel.getCurrentPosition().observe(getViewLifecycleOwner(), position -> {
            if (!isUserSeeking && position != null) {
                updateProgress(position);
            }
        });

        // Observe duration
        miniPlayerViewModel.getDuration().observe(getViewLifecycleOwner(), duration -> {
            if (duration != null && duration > 0) {
                updateDuration(duration);
            }
        });
    }

    private void setupProgressUpdater() {
        progressRunnable = new Runnable() {
            @Override
            public void run() {
                if (!isUserSeeking) {
                    miniPlayerViewModel.updateProgress();
                }
                progressHandler.postDelayed(this, 1000); // Update every second
            }
        };
    }

    private void updateSongInfo(Song song) {
        Glide.with(this)
                .asBitmap() // Important: load as bitmap for Palette
                .load(song.getImageUrl())
                .placeholder(R.drawable.ic_info)
                .error(R.drawable.ic_info)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        // Set the image
                        ivAlbumCover.setImageBitmap(resource);

                        // Extract dominant color and create gradient
                        extractColorAndSetGradient(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        ivAlbumCover.setImageDrawable(placeholder);
                    }
                });

        tvSongTitle.setText(song.getTitle());
        tvArtistName.setText(song.getArtistId());
    }

    private void extractColorAndSetGradient(Bitmap bitmap) {
        Palette.from(bitmap).generate(palette -> {
            // Get the dominant color, fallback to a default color
            int dominantColor = palette.getDominantColor(0xFF424242);

            // Start with black, end with the dominant color (less darkened for brighter background)
            int blackColor = Color.parseColor("#121212");
            int endColor = darkenColor(dominantColor, 0.1f); // Reduced from 0.2f to 0.1f for brighter color

            // Create gradient drawable from black to dominant color
            GradientDrawable gradientDrawable = new GradientDrawable(
                    GradientDrawable.Orientation.BOTTOM_TOP,
                    new int[]{blackColor, endColor}
            );

            // Apply gradient to the fragment's background
            if (getView() != null) {
                getView().setBackground(gradientDrawable);
            }
        });
    }

    private int darkenColor(int color, float factor) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);

        // Reduce brightness moderately to maintain some color while ensuring text visibility
        hsv[2] *= (1 - factor);

        // Increased brightness threshold for brighter colors
        if (hsv[2] > 0.9f) {
            hsv[2] = 0.9f;
        }
        return Color.HSVToColor(hsv);
    }

    private void updatePlayPauseButton(Boolean isPlaying) {
        if (isPlaying != null && isPlaying) {
            btnPlayPause.setImageResource(android.R.drawable.ic_media_pause);
        } else {
            btnPlayPause.setImageResource(android.R.drawable.ic_media_play);
        }
    }

    private void updatePlayPauseButtonForCurrentSong() {
        Boolean isPlaying = miniPlayerViewModel.getIsPlaying().getValue();
        Song currentSong = miniPlayerViewModel.getCurrentSong().getValue();

        if (currentSong != null) {
            updatePlayPauseButton(isPlaying);
        } else {
            btnPlayPause.setImageResource(android.R.drawable.ic_media_play);
        }
    }

    private void updateProgress(Long position) {
        if (position != null) {
            tvCurrentTime.setText(formatTime(position));
            int progressInSeconds = (int) (position / 1000);
            seekBarProgress.setProgress(progressInSeconds);
        }
    }

    private void updateDuration(Long duration) {
        if (duration != null) {
            tvTotalTime.setText(formatTime(duration));
            int durationInSeconds = (int) (duration / 1000);
            seekBarProgress.setMax(durationInSeconds);
        }
    }

    private String formatTime(long timeInMillis) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(timeInMillis);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(timeInMillis) % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    @Override
    public void onResume() {
        super.onResume();
        progressHandler.post(progressRunnable);
    }

    @Override
    public void onPause() {
        super.onPause();
        progressHandler.removeCallbacks(progressRunnable);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        progressHandler.removeCallbacks(progressRunnable);
    }

    private void showTrackOptionsBottomSheet() {
        trackOptionsBottomSheet = TrackOptionsBottomSheetFragment.newInstance();
        trackOptionsBottomSheet.setOnTrackOptionsListener(this);
        trackOptionsBottomSheet.show(getChildFragmentManager(), "TrackOptionsBottomSheet");
    }

    private void showShareBottomSheet() {
        shareBottomSheet = ShareBottomSheetFragment.newInstance();
        shareBottomSheet.setOnShareListener(this);
        shareBottomSheet.show(getChildFragmentManager(), "ShareBottomSheet");
    }

    // TrackOptionsBottomSheetFragment.OnTrackOptionsListener implementation
    @Override
    public void onLikeClicked() {
        // TODO: Toggle like
    }

    @Override
    public void onHideSongClicked() {
        // TODO: Hide song
    }

    @Override
    public void onAddToPlaylistClicked() {
        // TODO: Show playlist selection dialog
    }

    @Override
    public void onAddToQueueClicked() {
        // TODO: Add to queue
    }

    @Override
    public void onShareClicked() {
        showShareBottomSheet();
    }

    @Override
    public void onGoToRadioClicked() {
        // TODO: Navigate to radio
    }

    @Override
    public void onViewAlbumClicked() {
        // TODO: Navigate to album
    }

    @Override
    public void onViewArtistClicked() {
        // TODO: Navigate to artist
    }

    @Override
    public void onSongCreditsClicked() {
        // TODO: Show song credits
    }

    @Override
    public void onSleepTimerClicked() {
        // TODO: Show sleep timer dialog
    }

    @Override
    public void onCloseClicked() {
        // Handle close for both bottom sheets
        if (trackOptionsBottomSheet != null) {
            trackOptionsBottomSheet = null;
        }
        if (shareBottomSheet != null) {
            shareBottomSheet = null;
        }
    }

    // ShareBottomSheetFragment.OnShareListener implementation
    @Override
    public void onCopyLinkClicked() {
        // TODO: Copy link to clipboard
    }

    @Override
    public void onWhatsAppClicked() {
        // TODO: Share via WhatsApp
    }

    @Override
    public void onTwitterClicked() {
        // TODO: Share via Twitter
    }

    @Override
    public void onMessagesClicked() {
        // TODO: Share via Messages
    }
}
