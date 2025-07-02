package com.prm.home.model;

import com.prm.domain.model.Song;

public class RecentlyPlayedUiModel {
    
    private final Song song;
    private final String artistName;
    private final String imageUrl;
    private final boolean isPlaying;
    
    public RecentlyPlayedUiModel(Song song, String artistName, String imageUrl, boolean isPlaying) {
        this.song = song;
        this.artistName = artistName;
        this.imageUrl = imageUrl;
        this.isPlaying = isPlaying;
    }
    
    public Song getSong() {
        return song;
    }
    
    public String getTitle() {
        return song != null ? song.getTitle() : "";
    }
    
    public String getArtistName() {
        return artistName != null ? artistName : "";
    }
    
    public String getImageUrl() {
        // Priority: Song image -> Artist image -> empty string
        if (song != null && song.getImageUrl() != null && !song.getImageUrl().isEmpty()) {
            return song.getImageUrl();
        }
        return imageUrl != null ? imageUrl : "";
    }
    
    public boolean isPlaying() {
        return isPlaying;
    }
    
    public String getSongId() {
        return song != null ? song.getId() : "";
    }
    
    public String getArtistId() {
        return song != null ? song.getArtistId() : "";
    }
}
