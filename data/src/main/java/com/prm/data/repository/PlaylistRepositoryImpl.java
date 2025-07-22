package com.prm.data.repository;

import com.prm.data.source.remote.FirebasePlaylistService;
import com.prm.domain.model.Playlist;
import com.prm.domain.repository.PlaylistRepository;
import com.prm.domain.model.Song;
import com.prm.domain.repository.SongRepository;
import com.prm.domain.repository.ArtistRepository;
import com.prm.domain.model.Artist;
import java.util.List;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

@Singleton
public class PlaylistRepositoryImpl implements PlaylistRepository {

    private final FirebasePlaylistService firebasePlaylistService;
    private final SongRepository songRepository;
    private final ArtistRepository artistRepository;

    @Inject
    public PlaylistRepositoryImpl(FirebasePlaylistService firebasePlaylistService, SongRepository songRepository, ArtistRepository artistRepository) {
        this.firebasePlaylistService = firebasePlaylistService;
        this.songRepository = songRepository;
        this.artistRepository = artistRepository;
    }

    @Override
    public Completable createPlaylist(Playlist playlist) {
        return firebasePlaylistService.createPlaylist(playlist);
    }

    @Override
    public Observable<List<Playlist>> getPlaylistsForUser(String userId) {
        return firebasePlaylistService.getPlaylistsForUser(userId)
                .flatMapSingle(playlists -> {
                    // For each playlist, fetch songs by songIds
                    return Observable.fromIterable(playlists)
                            .flatMapSingle(playlist -> {
                                List<String> songIds = playlist.getSongIds();
                                if (songIds != null && !songIds.isEmpty()) {
                                    return songRepository.getSongsByIds(songIds)
                                            .flatMap(songs -> {
                                                return Observable.fromIterable(songs)
                                                        .flatMapSingle(song -> artistRepository.getArtistById(song.getArtistId())
                                                                .map(artist -> {
                                                                    song.setArtist(artist.getName());
                                                                    return song;
                                                                })
                                                                .onErrorReturnItem(song) // In case of error, return song without artist name
                                                        )
                                                        .toList()
                                                        .map(songsWithArtists -> {
                                                            playlist.setSongs(songsWithArtists);
                                                            return playlist;
                                                        });
                                            });
                                } else {
                                    playlist.setSongs(new java.util.ArrayList<>());
                                    return Single.just(playlist);
                                }
                            })
                            .toList(); // Collect the merged playlists into a list
                });
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
        String currentUserId = null;
        com.google.firebase.auth.FirebaseUser user = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            currentUserId = user.getUid();
        }
        if (currentUserId == null) {
            return Observable.just(new java.util.ArrayList<>());
        }
        return firebasePlaylistService.getPlaylistsForUser(currentUserId, limit);
    }
} 