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
    private String userId;
    private List<String> songIds;
    private String coverImageUrl;
    private List<Song> songs; // Thêm trường này để chứa danh sách bài hát đầy đủ
}