package com.prm.domain.usecase.playlist;

import com.prm.domain.model.Playlist;
import com.prm.domain.repository.PlaylistRepository;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Observable;

public class GetPlaylistsForUserUseCase {

    private final PlaylistRepository playlistRepository;

    @Inject
    public GetPlaylistsForUserUseCase(PlaylistRepository playlistRepository) {
        this.playlistRepository = playlistRepository;
    }

    public Observable<List<Playlist>> execute(String userId) {
        return playlistRepository.getPlaylistsForUser(userId);
    }
} 