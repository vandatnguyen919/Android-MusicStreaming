package com.prm.domain.usecase.playlist;

import com.prm.domain.repository.PlaylistRepository;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Completable;

public class AddSongToPlaylistUseCase {

    private final PlaylistRepository playlistRepository;

    @Inject
    public AddSongToPlaylistUseCase(PlaylistRepository playlistRepository) {
        this.playlistRepository = playlistRepository;
    }

    public Completable execute(String playlistId, String songId) {
        return playlistRepository.addSongToPlaylist(playlistId, songId);
    }
} 