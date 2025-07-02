package com.prm.home.model;

import com.prm.domain.model.Song;

public class EditorPickUiModel {
    
    private final Song song;
    private final String artistName;
    private final String imageUrl;
    private final String subtitle;
    private final boolean isRecommended;
    
    public EditorPickUiModel(Song song, String artistName, String imageUrl, String subtitle, boolean isRecommended) {
        this.song = song;
        this.artistName = artistName;
        this.imageUrl = imageUrl;
        this.subtitle = subtitle;
        this.isRecommended = isRecommended;
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
    
    public String getSubtitle() {
        return subtitle != null ? subtitle : "";
    }
    
    public boolean isRecommended() {
        return isRecommended;
    }
    
    public String getSongId() {
        return song != null ? song.getId() : "";
    }
    
    public String getArtistId() {
        return song != null ? song.getArtistId() : "";
    }
    
    public String getFormattedDuration() {
        if (song == null) return "0:00";
        int duration = song.getDuration();
        int minutes = duration / 60;
        int seconds = duration % 60;
        return String.format("%d:%02d", minutes, seconds);
    }
}
