package com.prm.domain.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Playlist {
    private String id;
    private String name;
    private String userId; // Creator of the playlist
    private List<String> songIds;
    private String coverImageUrl;
}