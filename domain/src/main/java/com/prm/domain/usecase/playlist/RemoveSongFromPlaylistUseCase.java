package com.prm.domain.usecase.playlist;

import com.prm.domain.repository.PlaylistRepository;

import javax.inject.Inject;

public class RemoveSongFromPlaylistUseCase {

    private final PlaylistRepository playlistRepository;

    @Inject
    public RemoveSongFromPlaylistUseCase(PlaylistRepository playlistRepository) {
        this.playlistRepository = playlistRepository;
    }

    public void execute(String playlistId, String songId) {
        playlistRepository.removeSongFromPlaylist(playlistId, songId);
    }
} 