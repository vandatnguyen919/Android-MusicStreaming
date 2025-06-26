package com.prm.album;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class TrackOptionsBottomSheetFragment extends BottomSheetDialogFragment {
    
    private ImageView ivTrackCover;
    private TextView tvTrackTitle;
    private TextView tvTrackArtist;
    private LinearLayout layoutLike;
    private LinearLayout layoutHideSong;
    private LinearLayout layoutAddToPlaylist;
    private LinearLayout layoutAddToQueue;
    private LinearLayout layoutShare;
    private LinearLayout layoutGoToRadio;
    private LinearLayout layoutViewAlbum;
    private LinearLayout layoutViewArtist;
    private LinearLayout layoutSongCredits;
    private LinearLayout layoutSleepTimer;
    private TextView tvClose;
    
    private OnTrackOptionsListener listener;
    
    public interface OnTrackOptionsListener {
        void onLikeClicked();
        void onHideSongClicked();
        void onAddToPlaylistClicked();
        void onAddToQueueClicked();
        void onShareClicked();
        void onGoToRadioClicked();
        void onViewAlbumClicked();
        void onViewArtistClicked();
        void onSongCreditsClicked();
        void onSleepTimerClicked();
        void onCloseClicked();
    }
    
    public static TrackOptionsBottomSheetFragment newInstance() {
        return new TrackOptionsBottomSheetFragment();
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_track_options, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViews(view);
        setupClickListeners();
        setupDummyData();
    }
    
    private void initViews(View view) {
        ivTrackCover = view.findViewById(R.id.iv_track_cover);
        tvTrackTitle = view.findViewById(R.id.tv_track_title);
        tvTrackArtist = view.findViewById(R.id.tv_track_artist);
        layoutLike = view.findViewById(R.id.layout_like);
        layoutHideSong = view.findViewById(R.id.layout_hide_song);
        layoutAddToPlaylist = view.findViewById(R.id.layout_add_to_playlist);
        layoutAddToQueue = view.findViewById(R.id.layout_add_to_queue);
        layoutShare = view.findViewById(R.id.layout_share);
        layoutGoToRadio = view.findViewById(R.id.layout_go_to_radio);
        layoutViewAlbum = view.findViewById(R.id.layout_view_album);
        layoutViewArtist = view.findViewById(R.id.layout_view_artist);
        layoutSongCredits = view.findViewById(R.id.layout_song_credits);
        layoutSleepTimer = view.findViewById(R.id.layout_sleep_timer);
        tvClose = view.findViewById(R.id.tv_close);
    }
    
    private void setupClickListeners() {
        layoutLike.setOnClickListener(v -> {
            if (listener != null) listener.onLikeClicked();
            dismiss();
        });
        
        layoutHideSong.setOnClickListener(v -> {
            if (listener != null) listener.onHideSongClicked();
            dismiss();
        });
        
        layoutAddToPlaylist.setOnClickListener(v -> {
            if (listener != null) listener.onAddToPlaylistClicked();
            dismiss();
        });
        
        layoutAddToQueue.setOnClickListener(v -> {
            if (listener != null) listener.onAddToQueueClicked();
            dismiss();
        });
        
        layoutShare.setOnClickListener(v -> {
            if (listener != null) listener.onShareClicked();
            dismiss();
        });
        
        layoutGoToRadio.setOnClickListener(v -> {
            if (listener != null) listener.onGoToRadioClicked();
            dismiss();
        });
        
        layoutViewAlbum.setOnClickListener(v -> {
            if (listener != null) listener.onViewAlbumClicked();
            dismiss();
        });
        
        layoutViewArtist.setOnClickListener(v -> {
            if (listener != null) listener.onViewArtistClicked();
            dismiss();
        });
        
        layoutSongCredits.setOnClickListener(v -> {
            if (listener != null) listener.onSongCreditsClicked();
            dismiss();
        });
        
        layoutSleepTimer.setOnClickListener(v -> {
            if (listener != null) listener.onSleepTimerClicked();
            dismiss();
        });
        
        tvClose.setOnClickListener(v -> {
            if (listener != null) listener.onCloseClicked();
            dismiss();
        });
    }
    
    private void setupDummyData() {
        // Set dummy data for preview
        tvTrackTitle.setText("1(Remastered)");
        tvTrackArtist.setText("The Beatles");
        
        // Set album cover background color (placeholder)
        ivTrackCover.setBackgroundColor(getResources().getColor(R.color.primary_red));
    }
    
    public void setOnTrackOptionsListener(OnTrackOptionsListener listener) {
        this.listener = listener;
    }
}
