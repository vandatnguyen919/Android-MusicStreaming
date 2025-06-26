package com.prm.domain.usecase;

import com.prm.domain.repository.AlbumRepository;

import javax.inject.Inject;

public class AlbumActionsUseCase {
    
    private final AlbumRepository albumRepository;
    
    @Inject
    public AlbumActionsUseCase(AlbumRepository albumRepository) {
        this.albumRepository = albumRepository;
    }
    
    public void downloadAlbum(String albumId) {
        albumRepository.downloadAlbum(albumId);
    }
    
    public void addToQueue(String albumId) {
        albumRepository.addAlbumToQueue(albumId);
    }
    
    public void addToPlaylist(String albumId, String playlistId) {
        albumRepository.addAlbumToPlaylist(albumId, playlistId);
    }
}
