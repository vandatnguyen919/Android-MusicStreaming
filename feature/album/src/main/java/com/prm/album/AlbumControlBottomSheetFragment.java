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
import com.prm.album.model.AlbumUiState;

public class AlbumControlBottomSheetFragment extends BottomSheetDialogFragment {
    
    private static final String ARG_ALBUM_STATE = "album_state";
    
    private ImageView ivAlbumCover;
    private TextView tvAlbumTitle;
    private TextView tvArtistName;
    private LinearLayout layoutLike;
    private LinearLayout layoutViewArtist;
    private LinearLayout layoutShare;
    private LinearLayout layoutLikeAllSongs;
    private LinearLayout layoutAddToPlaylist;
    private LinearLayout layoutAddToQueue;
    private LinearLayout layoutGoToRadio;
    private TextView tvClose;
    
    private OnBottomSheetActionListener listener;
    private AlbumUiState albumState;
    
    public interface OnBottomSheetActionListener {
        void onLikeClicked();
        void onViewArtistClicked();
        void onShareClicked();
        void onLikeAllSongsClicked();
        void onAddToPlaylistClicked();
        void onAddToQueueClicked();
        void onGoToRadioClicked();
        void onCloseClicked();
    }
    
    public static AlbumControlBottomSheetFragment newInstance(AlbumUiState albumState) {
        AlbumControlBottomSheetFragment fragment = new AlbumControlBottomSheetFragment();
        Bundle args = new Bundle();
        // Note: In real implementation, you'd need to make AlbumUiState Parcelable
        // For now, we'll pass the data through the listener
        fragment.setArguments(args);
        fragment.albumState = albumState;
        return fragment;
    }
    
    public void setOnBottomSheetActionListener(OnBottomSheetActionListener listener) {
        this.listener = listener;
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_album_control, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViews(view);
        setupClickListeners();
        bindData();
    }
    
    private void initViews(View view) {
        ivAlbumCover = view.findViewById(R.id.iv_bottom_sheet_album_cover);
        tvAlbumTitle = view.findViewById(R.id.tv_bottom_sheet_album_title);
        tvArtistName = view.findViewById(R.id.tv_bottom_sheet_artist_name);
        layoutLike = view.findViewById(R.id.layout_like);
        layoutViewArtist = view.findViewById(R.id.layout_view_artist);
        layoutShare = view.findViewById(R.id.layout_share);
        layoutLikeAllSongs = view.findViewById(R.id.layout_like_all_songs);
        layoutAddToPlaylist = view.findViewById(R.id.layout_add_to_playlist);
        layoutAddToQueue = view.findViewById(R.id.layout_add_to_queue);
        layoutGoToRadio = view.findViewById(R.id.layout_go_to_radio);
        tvClose = view.findViewById(R.id.tv_close);
    }
    
    private void setupClickListeners() {
        layoutLike.setOnClickListener(v -> {
            if (listener != null) listener.onLikeClicked();
            dismiss();
        });
        
        layoutViewArtist.setOnClickListener(v -> {
            if (listener != null) listener.onViewArtistClicked();
            dismiss();
        });
        
        layoutShare.setOnClickListener(v -> {
            if (listener != null) listener.onShareClicked();
            dismiss();
        });
        
        layoutLikeAllSongs.setOnClickListener(v -> {
            if (listener != null) listener.onLikeAllSongsClicked();
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
        
        layoutGoToRadio.setOnClickListener(v -> {
            if (listener != null) listener.onGoToRadioClicked();
            dismiss();
        });
        
        tvClose.setOnClickListener(v -> {
            if (listener != null) listener.onCloseClicked();
            dismiss();
        });
    }
    
    private void bindData() {
        if (albumState != null && albumState.getAlbum() != null && albumState.getArtist() != null) {
            tvAlbumTitle.setText(albumState.getAlbum().getName());
            tvArtistName.setText(albumState.getArtist().getName());
            
            // TODO: Load album cover image using image loading library
            // Glide.with(this).load(albumState.getAlbum().getCoverImageUrl()).into(ivAlbumCover);
        }
    }
    
    public void updateAlbumState(AlbumUiState albumState) {
        this.albumState = albumState;
        if (getView() != null) {
            bindData();
        }
    }
}
