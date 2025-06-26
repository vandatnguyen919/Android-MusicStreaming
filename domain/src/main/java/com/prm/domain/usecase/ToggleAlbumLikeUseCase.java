package com.prm.domain.usecase;

import com.prm.domain.repository.AlbumRepository;

import javax.inject.Inject;

public class ToggleAlbumLikeUseCase {
    
    private final AlbumRepository albumRepository;
    
    @Inject
    public ToggleAlbumLikeUseCase(AlbumRepository albumRepository) {
        this.albumRepository = albumRepository;
    }
    
    public boolean execute(String albumId) {
        boolean currentLikeStatus = albumRepository.isAlbumLiked(albumId);
        boolean newLikeStatus = !currentLikeStatus;
        albumRepository.toggleAlbumLike(albumId, newLikeStatus);
        return newLikeStatus;
    }
}
