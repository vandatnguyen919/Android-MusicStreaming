package com.prm.album;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ShareBottomSheetFragment extends BottomSheetDialogFragment {
    
    private ImageButton btnClose;
    private ImageView ivShareAlbumCover;
    private TextView tvShareSongTitle;
    private TextView tvShareArtistName;
    private LinearLayout layoutCopyLink;
    private LinearLayout layoutWhatsapp;
    private LinearLayout layoutTwitter;
    private LinearLayout layoutMessages;
    
    private OnShareListener listener;
    
    public interface OnShareListener {
        void onCopyLinkClicked();
        void onWhatsAppClicked();
        void onTwitterClicked();
        void onMessagesClicked();
        void onCloseClicked();
    }
    
    public static ShareBottomSheetFragment newInstance() {
        return new ShareBottomSheetFragment();
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_share, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViews(view);
        setupClickListeners();
        setupDummyData();
    }
    
    private void initViews(View view) {
        btnClose = view.findViewById(R.id.btn_close);
        ivShareAlbumCover = view.findViewById(R.id.iv_share_album_cover);
        tvShareSongTitle = view.findViewById(R.id.tv_share_song_title);
        tvShareArtistName = view.findViewById(R.id.tv_share_artist_name);
        layoutCopyLink = view.findViewById(R.id.layout_copy_link);
        layoutWhatsapp = view.findViewById(R.id.layout_whatsapp);
        layoutTwitter = view.findViewById(R.id.layout_twitter);
        layoutMessages = view.findViewById(R.id.layout_messages);
    }
    
    private void setupClickListeners() {
        btnClose.setOnClickListener(v -> {
            if (listener != null) listener.onCloseClicked();
            dismiss();
        });
        
        layoutCopyLink.setOnClickListener(v -> {
            if (listener != null) listener.onCopyLinkClicked();
            dismiss();
        });
        
        layoutWhatsapp.setOnClickListener(v -> {
            if (listener != null) listener.onWhatsAppClicked();
            dismiss();
        });
        
        layoutTwitter.setOnClickListener(v -> {
            if (listener != null) listener.onTwitterClicked();
            dismiss();
        });
        
        layoutMessages.setOnClickListener(v -> {
            if (listener != null) listener.onMessagesClicked();
            dismiss();
        });
    }
    
    private void setupDummyData() {
        // Set dummy data for preview
        tvShareSongTitle.setText("From Me to You - Mono / Remastered");
        tvShareArtistName.setText("The Beatles");
        
        // Set album cover background color (placeholder)
        ivShareAlbumCover.setBackgroundColor(getResources().getColor(R.color.primary_red));
    }
    
    public void setOnShareListener(OnShareListener listener) {
        this.listener = listener;
    }
}
