package com.prm.domain.model;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.PropertyName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Song {

    @DocumentId
    private String id;
    
    @PropertyName("title")
    private String title;
    
    @PropertyName("artist_id")
    private String artistId;
    
    @PropertyName("album_id")
    private String albumId;
    
    @PropertyName("duration")
    private int duration; // Duration in seconds
    
    @PropertyName("url")
    private String url; // URL for the audio file
    
    // Constructor without ID (for creating new songs)
    public Song(String title, String artistId, String albumId, int duration, String url) {
        this.title = title;
        this.artistId = artistId;
        this.albumId = albumId;
        this.duration = duration;
        this.url = url;
    }
}