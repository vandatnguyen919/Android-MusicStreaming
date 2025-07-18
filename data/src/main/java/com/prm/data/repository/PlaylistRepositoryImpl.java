package com.prm.data.repository;

import com.prm.data.source.remote.FirebasePlaylistService;
import com.prm.domain.model.Playlist;
import com.prm.domain.repository.PlaylistRepository;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;

@Singleton
public class PlaylistRepositoryImpl implements PlaylistRepository {

    private final FirebasePlaylistService firebasePlaylistService;

    @Inject
    public PlaylistRepositoryImpl(FirebasePlaylistService firebasePlaylistService) {
        this.firebasePlaylistService = firebasePlaylistService;
    }

    @Override
    public Completable createPlaylist(Playlist playlist) {
        return firebasePlaylistService.createPlaylist(playlist);
    }

    @Override
    public Observable<List<Playlist>> getPlaylistsForUser(String userId) {
        return firebasePlaylistService.getPlaylistsForUser(userId);
    }

    @Override
    public Completable updatePlaylist(Playlist playlist) {
        return firebasePlaylistService.updatePlaylist(playlist);
    }

    @Override
    public Completable deletePlaylist(String playlistId) {
        return firebasePlaylistService.deletePlaylist(playlistId);
    }

    @Override
    public Completable addSongToPlaylist(String playlistId, String songId) {
        return firebasePlaylistService.addSongToPlaylist(playlistId, songId);
    }

    @Override
    public Completable removeSongFromPlaylist(String playlistId, String songId) {
        return firebasePlaylistService.removeSongFromPlaylist(playlistId, songId);
    }

    @Override
    public Observable<List<Playlist>> getCurrentUserPlaylists(int limit) {
        // In a real application, you'd get the current user's ID from an authentication service
        // For now, let's use a dummy user ID or fetch from a placeholder
        String currentUserId = "dummyUserId"; // Replace with actual user ID logic
        return firebasePlaylistService.getPlaylistsForUser(currentUserId, limit);
    }
} 