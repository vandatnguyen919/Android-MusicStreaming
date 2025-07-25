package com.prm.data.repository;

import com.prm.data.source.remote.FirebaseSongService;
import com.prm.domain.model.Song;
import com.prm.domain.repository.SongRepository;

import java.util.List;
import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

@Singleton
public class SongRepositoryImpl implements SongRepository {

    private final FirebaseSongService firebaseSongService;

    @Inject
    public SongRepositoryImpl(FirebaseSongService firebaseSongService) {
        this.firebaseSongService = firebaseSongService;
    }

    @Override
    public Single<List<Song>> getAllSongs() {
        return firebaseSongService.getAllSongs();
    }

    @Override
    public Observable<List<Song>> getAllSongsObservable() {
        return firebaseSongService.getAllSongsObservable();
    }

    @Override
    public Single<Song> getSongById(String songId) {
        return firebaseSongService.getSongById(songId);
    }

    @Override
    public Observable<Song> getSongByIdObservable(String songId) {
        return firebaseSongService.getSongByIdObservable(songId);
    }

    @Override
    public Single<List<Song>> getSongsByArtistId(String artistId) {
        return firebaseSongService.getSongsByArtistId(artistId);
    }

    @Override
    public Observable<List<Song>> getSongsByArtistIdObservable(String artistId) {
        return firebaseSongService.getSongsByArtistIdObservable(artistId);
    }

    @Override
    public Single<List<Song>> getSongsByAlbumId(String albumId) {
        return firebaseSongService.getSongsByAlbumId(albumId);
    }

    @Override
    public Observable<List<Song>> getSongsByAlbumIdObservable(String albumId) {
        return firebaseSongService.getSongsByAlbumIdObservable(albumId);
    }

    @Override
    public Single<String> addSong(Song song) {
        return firebaseSongService.addSong(song);
    }

    @Override
    public Completable updateSong(Song song) {
        return firebaseSongService.updateSong(song);
    }

    public Completable addNewSong(Song song) {
        return firebaseSongService.addNewSong(song);
    }

    @Override
    public Completable deleteSong(String songId) {
        return firebaseSongService.deleteSong(songId);
    }

    @Override
    public Single<List<Song>> searchSongsByTitle(String searchTerm) {
        return firebaseSongService.searchSongsByTitle(searchTerm);
    }

    @Override
    public Single<List<Song>> getSongsWithPagination(int limit, Song lastSong) {
        return firebaseSongService.getSongsWithPagination(limit, lastSong);
    }

    @Override
    public Single<List<Song>> getSongsByIds(List<String> songIds) {
        return firebaseSongService.getSongsByIds(songIds);
    }

    @Override
    public Single<List<Song>> getCurrentUserFavoriteSongs(int limit) {
        // TODO: Implement actual fetching of user's favorite songs from Firebase
        // For now, return an empty list or a dummy list.
        // You would typically get the current user's ID here and query a 'favorites' collection.
        return Single.just(new ArrayList<>());
    }
}