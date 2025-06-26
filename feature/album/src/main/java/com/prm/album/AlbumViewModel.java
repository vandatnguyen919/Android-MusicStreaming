package com.prm.album;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.prm.album.model.AlbumUiState;
import com.prm.album.model.SongUiModel;
import com.prm.domain.model.Album;
import com.prm.domain.model.Artist;
import com.prm.domain.model.Song;
import com.prm.domain.usecase.AlbumActionsUseCase;
import com.prm.domain.usecase.GetAlbumDetailsUseCase;
import com.prm.domain.usecase.ToggleAlbumLikeUseCase;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AlbumViewModel extends ViewModel {

    private final GetAlbumDetailsUseCase getAlbumDetailsUseCase;
    private final ToggleAlbumLikeUseCase toggleAlbumLikeUseCase;
    private final AlbumActionsUseCase albumActionsUseCase;

    private final MutableLiveData<AlbumUiState> _uiState = new MutableLiveData<>();
    public final LiveData<AlbumUiState> uiState = _uiState;

    private final MutableLiveData<List<SongUiModel>> _songs = new MutableLiveData<>();
    public final LiveData<List<SongUiModel>> songs = _songs;

    private final MutableLiveData<Boolean> _showBottomSheet = new MutableLiveData<>();
    public final LiveData<Boolean> showBottomSheet = _showBottomSheet;

    private String currentAlbumId;
    private String currentPlayingSongId;
    private boolean isPlaying = false;

    @Inject
    public AlbumViewModel(
        GetAlbumDetailsUseCase getAlbumDetailsUseCase,
        ToggleAlbumLikeUseCase toggleAlbumLikeUseCase,
        AlbumActionsUseCase albumActionsUseCase
    ) {
        this.getAlbumDetailsUseCase = getAlbumDetailsUseCase;
        this.toggleAlbumLikeUseCase = toggleAlbumLikeUseCase;
        this.albumActionsUseCase = albumActionsUseCase;

        _uiState.setValue(AlbumUiState.loading());
        _showBottomSheet.setValue(false);
    }

    public void loadAlbum(String albumId) {
        this.currentAlbumId = albumId;
        _uiState.setValue(AlbumUiState.loading());

        try {
            GetAlbumDetailsUseCase.AlbumDetails details = getAlbumDetailsUseCase.execute(albumId);

            AlbumUiState newState = AlbumUiState.builder()
                .setAlbum(details.getAlbum())
                .setArtist(details.getArtist())
                .setLiked(details.isLiked())
                .setPlaying(isPlaying)
                .setCurrentPlayingSongId(currentPlayingSongId)
                .build();

            _uiState.setValue(newState);

            // Convert songs to UI models
            List<SongUiModel> songUiModels = new ArrayList<>();
            for (Song song : details.getSongs()) {
                boolean isSongPlaying = song.getId().equals(currentPlayingSongId) && isPlaying;
                SongUiModel songUiModel = new SongUiModel(
                    song,
                    isSongPlaying,
                    false, // TODO: Get individual song like status
                    details.getArtist().getName()
                );
                songUiModels.add(songUiModel);
            }
            _songs.setValue(songUiModels);

        } catch (Exception e) {
            _uiState.setValue(AlbumUiState.error("Failed to load album: " + e.getMessage()));
        }
    }

    public void toggleLike() {
        if (currentAlbumId != null) {
            try {
                boolean newLikeStatus = toggleAlbumLikeUseCase.execute(currentAlbumId);

                AlbumUiState currentState = _uiState.getValue();
                if (currentState != null) {
                    AlbumUiState newState = AlbumUiState.builder()
                        .setAlbum(currentState.getAlbum())
                        .setArtist(currentState.getArtist())
                        .setSongs(currentState.getSongs())
                        .setLiked(newLikeStatus)
                        .setPlaying(currentState.isPlaying())
                        .setCurrentPlayingSongId(currentState.getCurrentPlayingSongId())
                        .build();

                    _uiState.setValue(newState);
                }
            } catch (Exception e) {
                // Handle error
            }
        }
    }

    public void downloadAlbum() {
        if (currentAlbumId != null) {
            albumActionsUseCase.downloadAlbum(currentAlbumId);
        }
    }

    public void showMoreOptions() {
        _showBottomSheet.setValue(true);
    }

    public void hideBottomSheet() {
        _showBottomSheet.setValue(false);
    }

    public void addToQueue() {
        if (currentAlbumId != null) {
            albumActionsUseCase.addToQueue(currentAlbumId);
        }
        hideBottomSheet();
    }

    public void addToPlaylist(String playlistId) {
        if (currentAlbumId != null) {
            albumActionsUseCase.addToPlaylist(currentAlbumId, playlistId);
        }
        hideBottomSheet();
    }

    public void playPauseAlbum() {
        isPlaying = !isPlaying;

        AlbumUiState currentState = _uiState.getValue();
        if (currentState != null) {
            AlbumUiState newState = AlbumUiState.builder()
                .setAlbum(currentState.getAlbum())
                .setArtist(currentState.getArtist())
                .setSongs(currentState.getSongs())
                .setLiked(currentState.isLiked())
                .setPlaying(isPlaying)
                .setCurrentPlayingSongId(currentPlayingSongId)
                .build();

            _uiState.setValue(newState);
        }

        // Update songs list
        updateSongsPlayingState();
    }

    public void playSong(String songId) {
        currentPlayingSongId = songId;
        isPlaying = true;

        AlbumUiState currentState = _uiState.getValue();
        if (currentState != null) {
            AlbumUiState newState = AlbumUiState.builder()
                .setAlbum(currentState.getAlbum())
                .setArtist(currentState.getArtist())
                .setSongs(currentState.getSongs())
                .setLiked(currentState.isLiked())
                .setPlaying(isPlaying)
                .setCurrentPlayingSongId(currentPlayingSongId)
                .build();

            _uiState.setValue(newState);
        }

        // Update songs list
        updateSongsPlayingState();
    }

    private void updateSongsPlayingState() {
        List<SongUiModel> currentSongs = _songs.getValue();
        if (currentSongs != null) {
            List<SongUiModel> updatedSongs = new ArrayList<>();
            for (SongUiModel songUiModel : currentSongs) {
                boolean isSongPlaying = songUiModel.getSongId().equals(currentPlayingSongId) && isPlaying;
                SongUiModel updatedSong = new SongUiModel(
                    songUiModel.getSong(),
                    isSongPlaying,
                    songUiModel.isLiked(),
                    songUiModel.getArtistName()
                );
                updatedSongs.add(updatedSong);
            }
            _songs.setValue(updatedSongs);
        }
    }
}