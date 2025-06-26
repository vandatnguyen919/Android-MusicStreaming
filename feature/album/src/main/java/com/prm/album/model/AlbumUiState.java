package com.prm.album.model;

import com.prm.domain.model.Album;
import com.prm.domain.model.Artist;
import com.prm.domain.model.Song;

import java.util.List;

public class AlbumUiState {
    
    private final boolean isLoading;
    private final Album album;
    private final List<Song> songs;
    private final Artist artist;
    private final boolean isLiked;
    private final boolean isPlaying;
    private final String currentPlayingSongId;
    private final String error;
    
    private AlbumUiState(Builder builder) {
        this.isLoading = builder.isLoading;
        this.album = builder.album;
        this.songs = builder.songs;
        this.artist = builder.artist;
        this.isLiked = builder.isLiked;
        this.isPlaying = builder.isPlaying;
        this.currentPlayingSongId = builder.currentPlayingSongId;
        this.error = builder.error;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static AlbumUiState loading() {
        return builder().setLoading(true).build();
    }
    
    public static AlbumUiState error(String error) {
        return builder().setError(error).build();
    }
    
    // Getters
    public boolean isLoading() { return isLoading; }
    public Album getAlbum() { return album; }
    public List<Song> getSongs() { return songs; }
    public Artist getArtist() { return artist; }
    public boolean isLiked() { return isLiked; }
    public boolean isPlaying() { return isPlaying; }
    public String getCurrentPlayingSongId() { return currentPlayingSongId; }
    public String getError() { return error; }
    
    public static class Builder {
        private boolean isLoading = false;
        private Album album;
        private List<Song> songs;
        private Artist artist;
        private boolean isLiked = false;
        private boolean isPlaying = false;
        private String currentPlayingSongId;
        private String error;
        
        public Builder setLoading(boolean loading) {
            this.isLoading = loading;
            return this;
        }
        
        public Builder setAlbum(Album album) {
            this.album = album;
            return this;
        }
        
        public Builder setSongs(List<Song> songs) {
            this.songs = songs;
            return this;
        }
        
        public Builder setArtist(Artist artist) {
            this.artist = artist;
            return this;
        }
        
        public Builder setLiked(boolean liked) {
            this.isLiked = liked;
            return this;
        }
        
        public Builder setPlaying(boolean playing) {
            this.isPlaying = playing;
            return this;
        }
        
        public Builder setCurrentPlayingSongId(String songId) {
            this.currentPlayingSongId = songId;
            return this;
        }
        
        public Builder setError(String error) {
            this.error = error;
            return this;
        }
        
        public AlbumUiState build() {
            return new AlbumUiState(this);
        }
    }
}
