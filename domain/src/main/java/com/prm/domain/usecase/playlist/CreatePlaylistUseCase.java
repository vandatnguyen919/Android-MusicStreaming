package com.prm.domain.usecase.playlist;

import com.prm.domain.model.Playlist;
import com.prm.domain.repository.PlaylistRepository;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Completable;

public class CreatePlaylistUseCase {

    private final PlaylistRepository playlistRepository;

    @Inject
    public CreatePlaylistUseCase(PlaylistRepository playlistRepository) {
        this.playlistRepository = playlistRepository;
    }

    public Completable execute(Playlist playlist) {
        return playlistRepository.createPlaylist(playlist);
    }
} 