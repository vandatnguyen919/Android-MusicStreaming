package com.prm.domain.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Album {
    private String id;
    private String name;
    private String artistId;
    private String coverImageUrl; // Album cover image URL
    private List<String> songIds; // List of songs in the album
}