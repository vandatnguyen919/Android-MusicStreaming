package com.prm.common;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.prm.common.controller.MusicController;
import com.prm.domain.model.Song;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class MiniPlayerViewModel extends ViewModel {

    private final MusicController musicController;

    @Inject
    public MiniPlayerViewModel(MusicController musicController) {
        this.musicController = musicController;
    }

    public void playMusic(Song song) {
        musicController.playMusic(song);
    }

    public void playPlaylist(List<Song> songs, int startIndex) {
        musicController.playPlaylist(songs, startIndex);
    }

    public void playPause() {
        musicController.playPause();
    }

    public void skipToNext() {
        musicController.skipToNext();
    }

    public void skipToPrevious() {
        musicController.skipToPrevious();
    }

    public void seekTo(long position) {
        musicController.seekTo(position);
    }

    // LiveData getters
    public LiveData<Boolean> getIsPlaying() {
        return musicController.getIsPlayingLiveData();
    }

    public LiveData<Song> getCurrentSong() {
        return musicController.getCurrentSongLiveData();
    }

    public LiveData<Long> getCurrentPosition() {
        return musicController.getCurrentPositionLiveData();
    }

    public LiveData<Long> getDuration() {
        return musicController.getDurationLiveData();
    }

    public void connect() {
        musicController.connect();
    }

    public void disconnect() {
        musicController.disconnect();
    }

    public void updateProgress() {
        musicController.updateProgress();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        musicController.disconnect();
    }
}
