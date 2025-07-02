package com.prm.home.model;

import com.prm.domain.model.Song;

public class ReviewUiModel {
    
    private final Song song;
    private final String artistName;
    private final String imageUrl;
    private final String reviewText;
    private final String reviewTitle;
    
    public ReviewUiModel(Song song, String artistName, String imageUrl, String reviewText, String reviewTitle) {
        this.song = song;
        this.artistName = artistName;
        this.imageUrl = imageUrl;
        this.reviewText = reviewText;
        this.reviewTitle = reviewTitle;
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
    
    public String getReviewText() {
        return reviewText != null ? reviewText : "";
    }
    
    public String getReviewTitle() {
        return reviewTitle != null ? reviewTitle : "";
    }
    
    public String getSongId() {
        return song != null ? song.getId() : "";
    }
    
    public String getArtistId() {
        return song != null ? song.getArtistId() : "";
    }
}
