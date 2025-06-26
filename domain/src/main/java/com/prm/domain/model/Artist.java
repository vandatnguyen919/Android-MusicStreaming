package com.prm.domain.model;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.PropertyName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Artist {

    @DocumentId
    private String id;

    @PropertyName("name")
    private String name;

    @PropertyName("bio")
    private String bio;

    @PropertyName("profile_image_url")
    private String profileImageUrl;

    // Constructor without ID (for creating new artists)
    public Artist(String name, String bio, String profileImageUrl) {
        this.name = name;
        this.bio = bio;
        this.profileImageUrl = profileImageUrl;
    }
}