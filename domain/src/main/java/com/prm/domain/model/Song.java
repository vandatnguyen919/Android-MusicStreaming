package com.prm.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Song {
    private String id;
    private String title;
    private String artistId;
    private String albumId;
    private int duration; // Duration in seconds
    private String url; // URL for the audio file
}