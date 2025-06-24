package com.prm.album;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.prm.album.adapter.SongAdapter;
import com.prm.album.model.AlbumUiState;
import com.prm.album.model.SongUiModel;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AlbumFragment extends Fragment implements
    SongAdapter.OnSongClickListener,
    AlbumControlBottomSheetFragment.OnBottomSheetActionListener {

    private AlbumViewModel viewModel;

    // Views
    private ImageButton btnBack;
    private ImageView ivAlbumCover;
    private TextView tvAlbumTitle;
    private ImageView ivArtistAvatar;
    private TextView tvArtistName;
    private TextView tvAlbumInfo;
    private ImageButton btnLike;
    private ImageButton btnDownload;
    private ImageButton btnMore;
    private FloatingActionButton fabPlay;
    private RecyclerView rvSongs;
    private LinearLayout layoutMiniPlayer;
    private ImageView ivMiniPlayerCover;
    private TextView tvMiniPlayerTitle;
    private TextView tvMiniPlayerArtist;
    private ImageButton btnMiniPlayerPlayPause;

    // Adapter
    private SongAdapter songAdapter;

    // Bottom Sheet
    private AlbumControlBottomSheetFragment bottomSheetFragment;

    public static AlbumFragment newInstance() {
        return new AlbumFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_album, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupRecyclerView();
        setupClickListeners();
        observeViewModel();

        // Load album data (in real app, get albumId from arguments)
        viewModel.loadAlbum("1");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(AlbumViewModel.class);
    }

    private void initViews(View view) {
        btnBack = view.findViewById(R.id.btn_back);
        ivAlbumCover = view.findViewById(R.id.iv_album_cover);
        tvAlbumTitle = view.findViewById(R.id.tv_album_title);
        ivArtistAvatar = view.findViewById(R.id.iv_artist_avatar);
        tvArtistName = view.findViewById(R.id.tv_artist_name);
        tvAlbumInfo = view.findViewById(R.id.tv_album_info);
        btnLike = view.findViewById(R.id.btn_like);
        btnDownload = view.findViewById(R.id.btn_download);
        btnMore = view.findViewById(R.id.btn_more);
        fabPlay = view.findViewById(R.id.fab_play);
        rvSongs = view.findViewById(R.id.rv_songs);
        layoutMiniPlayer = view.findViewById(R.id.layout_mini_player);
        ivMiniPlayerCover = view.findViewById(R.id.iv_mini_player_cover);
        tvMiniPlayerTitle = view.findViewById(R.id.tv_mini_player_title);
        tvMiniPlayerArtist = view.findViewById(R.id.tv_mini_player_artist);
        btnMiniPlayerPlayPause = view.findViewById(R.id.btn_mini_player_play_pause);
    }

    private void setupRecyclerView() {
        songAdapter = new SongAdapter();
        songAdapter.setOnSongClickListener(this);
        rvSongs.setLayoutManager(new LinearLayoutManager(getContext()));
        rvSongs.setAdapter(songAdapter);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        btnLike.setOnClickListener(v -> viewModel.toggleLike());

        btnDownload.setOnClickListener(v -> viewModel.downloadAlbum());

        btnMore.setOnClickListener(v -> viewModel.showMoreOptions());

        fabPlay.setOnClickListener(v -> viewModel.playPauseAlbum());

        btnMiniPlayerPlayPause.setOnClickListener(v -> viewModel.playPauseAlbum());
    }

    private void observeViewModel() {
        viewModel.uiState.observe(getViewLifecycleOwner(), this::updateUI);

        viewModel.songs.observe(getViewLifecycleOwner(), songs -> {
            songAdapter.setSongs(songs);
        });

        viewModel.showBottomSheet.observe(getViewLifecycleOwner(), show -> {
            if (show && bottomSheetFragment == null) {
                AlbumUiState currentState = viewModel.uiState.getValue();
                if (currentState != null) {
                    showBottomSheet(currentState);
                }
            }
        });
    }

    private void updateUI(AlbumUiState state) {
        if (state.isLoading()) {
            // Show loading state
            return;
        }

        if (state.getError() != null) {
            // Show error state
            return;
        }

        if (state.getAlbum() != null) {
            tvAlbumTitle.setText(state.getAlbum().getName());
            tvAlbumInfo.setText("Album • 2000"); // TODO: Get real year from album

            // TODO: Load album cover image using image loading library
            // Glide.with(this).load(state.getAlbum().getCoverImageUrl()).into(ivAlbumCover);
        }

        if (state.getArtist() != null) {
            tvArtistName.setText(state.getArtist().getName());

            // TODO: Load artist avatar using image loading library
            // Glide.with(this).load(state.getArtist().getProfileImageUrl()).into(ivArtistAvatar);
        }

        // Update like button state
        if (state.isLiked()) {
            btnLike.setImageResource(R.drawable.ic_heart_filled);
        } else {
            btnLike.setImageResource(R.drawable.ic_heart);
        }

        // Update play button state
        if (state.isPlaying()) {
            fabPlay.setImageResource(R.drawable.ic_pause);
        } else {
            fabPlay.setImageResource(R.drawable.ic_play);
        }

        // Update mini player
        if (state.getCurrentPlayingSongId() != null && state.isPlaying()) {
            layoutMiniPlayer.setVisibility(View.VISIBLE);
            // TODO: Update mini player with current song info
            btnMiniPlayerPlayPause.setImageResource(R.drawable.ic_pause);
        } else {
            layoutMiniPlayer.setVisibility(View.GONE);
        }
    }

    private void showBottomSheet(AlbumUiState albumState) {
        bottomSheetFragment = AlbumControlBottomSheetFragment.newInstance(albumState);
        bottomSheetFragment.setOnBottomSheetActionListener(this);
        bottomSheetFragment.show(getChildFragmentManager(), "AlbumControlBottomSheet");
    }

    // SongAdapter.OnSongClickListener implementation
    @Override
    public void onSongClick(SongUiModel song) {
        viewModel.playSong(song.getSongId());
    }

    @Override
    public void onSongMoreClick(SongUiModel song) {
        // TODO: Show song-specific options
    }

    // AlbumControlBottomSheetFragment.OnBottomSheetActionListener implementation
    @Override
    public void onLikeClicked() {
        viewModel.toggleLike();
    }

    @Override
    public void onViewArtistClicked() {
        // TODO: Navigate to artist page
    }

    @Override
    public void onShareClicked() {
        // TODO: Implement share functionality
    }

    @Override
    public void onLikeAllSongsClicked() {
        // TODO: Implement like all songs
    }

    @Override
    public void onAddToPlaylistClicked() {
        // TODO: Show playlist selection dialog
    }

    @Override
    public void onAddToQueueClicked() {
        viewModel.addToQueue();
    }

    @Override
    public void onGoToRadioClicked() {
        // TODO: Navigate to radio based on this album
    }

    @Override
    public void onCloseClicked() {
        viewModel.hideBottomSheet();
        bottomSheetFragment = null;
    }
}