package com.prm.domain.usecase.playlist;

import com.prm.domain.repository.PlaylistRepository;

import javax.inject.Inject;

public class DeletePlaylistUseCase {

    private final PlaylistRepository playlistRepository;

    @Inject
    public DeletePlaylistUseCase(PlaylistRepository playlistRepository) {
        this.playlistRepository = playlistRepository;
    }

    public void execute(String playlistId) {
        playlistRepository.deletePlaylist(playlistId);
    }
} 