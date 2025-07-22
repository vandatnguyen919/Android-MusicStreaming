package com.prm.common;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.prm.common.controller.MusicController;
import com.prm.domain.model.Song;
import com.prm.domain.repository.SongRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;

@HiltViewModel
public class MiniPlayerViewModel extends ViewModel {

    private final MusicController musicController;
    private final SongRepository songRepository;

    @Inject
    public MiniPlayerViewModel(MusicController musicController, SongRepository songRepository) {
        this.musicController = musicController;
        this.songRepository = songRepository;
    }

    public void playSongById(String songId) {
        songRepository.getSongById(songId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(song -> {
                    musicController.playMusic(song);
                }, throwable -> {
                    Log.d("MiniPlayerViewModel", "Error fetching song: " + throwable.getMessage());
                });
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
