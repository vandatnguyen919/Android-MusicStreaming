package com.prm.domain.usecase.playlist;

import com.prm.domain.repository.PlaylistRepository;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Completable;

public class RemoveSongFromPlaylistUseCase {

    private final PlaylistRepository playlistRepository;

    @Inject
    public RemoveSongFromPlaylistUseCase(PlaylistRepository playlistRepository) {
        this.playlistRepository = playlistRepository;
    }

    public Completable execute(String playlistId, String songId) {
        return playlistRepository.removeSongFromPlaylist(playlistId, songId);
    }
} 