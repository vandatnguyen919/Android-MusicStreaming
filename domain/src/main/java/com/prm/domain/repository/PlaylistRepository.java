package com.prm.domain.repository;

import com.prm.domain.model.Playlist;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;

public interface PlaylistRepository {
    Completable createPlaylist(Playlist playlist);
    Observable<List<Playlist>> getPlaylistsForUser(String userId);
    Completable updatePlaylist(Playlist playlist);
    Completable deletePlaylist(String playlistId);
    Completable addSongToPlaylist(String playlistId, String songId);
    Completable removeSongFromPlaylist(String playlistId, String songId);
} 