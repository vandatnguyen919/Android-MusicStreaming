package com.prm.domain.usecase.playlist;

import com.prm.domain.model.Playlist;
import com.prm.domain.repository.PlaylistRepository;

import javax.inject.Inject;

public class UpdatePlaylistUseCase {

    private final PlaylistRepository playlistRepository;

    @Inject
    public UpdatePlaylistUseCase(PlaylistRepository playlistRepository) {
        this.playlistRepository = playlistRepository;
    }

    public void execute(Playlist playlist) {
        playlistRepository.updatePlaylist(playlist);
    }
} 