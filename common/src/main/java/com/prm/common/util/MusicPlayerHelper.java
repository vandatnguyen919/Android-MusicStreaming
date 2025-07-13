package com.prm.common.util;

import androidx.media3.common.util.UnstableApi;

import com.prm.common.controller.MusicController;
import com.prm.domain.model.Song;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;


@UnstableApi @Singleton
public class MusicPlayerHelper {

    private final MusicController musicController;

    @Inject
    public MusicPlayerHelper(MusicController musicController) {
        this.musicController = musicController;
    }


    public void playSong(Song song) {
        musicController.playMusic(song);
    }


    public void playPlaylist(List<Song> songs, int startIndex) {
        musicController.playPlaylist(songs, startIndex);
    }


    public void playPlaylist(List<Song> songs) {
        playPlaylist(songs, 0);
    }


    public void togglePlayPause() {
        musicController.playPause();
    }


    public void skipNext() {
        musicController.skipToNext();
    }


    public void skipPrevious() {
        musicController.skipToPrevious();
    }


    public boolean isPlaying() {
        return musicController.isPlaying();
    }


    public Song getCurrentSong() {
        return musicController.getCurrentSong();
    }
}
