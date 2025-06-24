package com.prm.album.model;

import com.prm.domain.model.Song;

public class SongUiModel {
    
    private final Song song;
    private final boolean isPlaying;
    private final boolean isLiked;
    private final String artistName;
    private final String formattedDuration;
    
    public SongUiModel(Song song, boolean isPlaying, boolean isLiked, String artistName) {
        this.song = song;
        this.isPlaying = isPlaying;
        this.isLiked = isLiked;
        this.artistName = artistName;
        this.formattedDuration = formatDuration(song.getDuration());
    }
    
    private String formatDuration(int durationInSeconds) {
        int minutes = durationInSeconds / 60;
        int seconds = durationInSeconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }
    
    // Getters
    public Song getSong() { return song; }
    public boolean isPlaying() { return isPlaying; }
    public boolean isLiked() { return isLiked; }
    public String getArtistName() { return artistName; }
    public String getFormattedDuration() { return formattedDuration; }
    public String getSongId() { return song.getId(); }
    public String getTitle() { return song.getTitle(); }
}
