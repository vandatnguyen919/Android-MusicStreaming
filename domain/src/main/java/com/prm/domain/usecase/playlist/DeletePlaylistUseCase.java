package com.prm.domain.usecase.playlist;

import com.prm.domain.repository.PlaylistRepository;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Completable;

public class DeletePlaylistUseCase {

    private final PlaylistRepository playlistRepository;

    @Inject
    public DeletePlaylistUseCase(PlaylistRepository playlistRepository) {
        this.playlistRepository = playlistRepository;
    }

    public Completable execute(String playlistId) {
        return playlistRepository.deletePlaylist(playlistId);
    }
}