package com.prm.domain.model;

import com.google.firebase.firestore.DocumentId;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {

    @DocumentId
    private String id;
//    private String username;
//    private String email;
//    private String profileImageUrl;
}