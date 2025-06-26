package com.prm.album.model;

public class RadioSongUiModel {
    private final String songId;
    private final String title;
    private final String artist;
    private final boolean isPlaying;

    public RadioSongUiModel(String songId, String title, String artist, boolean isPlaying) {
        this.songId = songId;
        this.title = title;
        this.artist = artist;
        this.isPlaying = isPlaying;
    }

    public String getSongId() {
        return songId;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public boolean isPlaying() {
        return isPlaying;
    }
}
