package com.prm.common.util;

import androidx.media3.common.util.UnstableApi;

import com.prm.domain.model.Song;
import com.prm.common.controller.MusicController;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Helper class to provide easy access to music playback functionality
 * for use in feature modules and fragments
 */
@UnstableApi @Singleton
public class MusicPlayerHelper {

    private final MusicController musicController;

    @Inject
    public MusicPlayerHelper(MusicController musicController) {
        this.musicController = musicController;
    }

    /**
     * Play a single song
     */
    public void playSong(Song song) {
        musicController.playMusic(song);
    }

    /**
     * Play a playlist starting from a specific index
     */
    public void playPlaylist(List<Song> songs, int startIndex) {
        musicController.playPlaylist(songs, startIndex);
    }

    /**
     * Play a playlist from the beginning
     */
    public void playPlaylist(List<Song> songs) {
        playPlaylist(songs, 0);
    }

    /**
     * Toggle play/pause
     */
    public void togglePlayPause() {
        musicController.playPause();
    }

    /**
     * Skip to next song
     */
    public void skipNext() {
        musicController.skipToNext();
    }

    /**
     * Skip to previous song
     */
    public void skipPrevious() {
        musicController.skipToPrevious();
    }

    /**
     * Check if music is currently playing
     */
    public boolean isPlaying() {
        return musicController.isPlaying();
    }

    /**
     * Get the currently playing song
     */
    public Song getCurrentSong() {
        return musicController.getCurrentSong();
    }
}
