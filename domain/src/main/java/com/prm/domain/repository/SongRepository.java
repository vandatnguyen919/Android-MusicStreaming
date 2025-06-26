package com.prm.domain.repository;

import com.prm.domain.model.Song;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

public interface SongRepository {

    // Single emissions (one-time data fetch)
    Single<List<Song>> getAllSongs();
    Single<Song> getSongById(String songId);
    Single<List<Song>> getSongsByArtistId(String artistId);
    Single<List<Song>> getSongsByAlbumId(String albumId);
    Single<String> addSong(Song song);
    Single<List<Song>> searchSongsByTitle(String searchTerm);
    Single<List<Song>> getSongsWithPagination(int limit, Song lastSong);

    // Observable emissions (real-time updates)
    Observable<List<Song>> getAllSongsObservable();
    Observable<Song> getSongByIdObservable(String songId);
    Observable<List<Song>> getSongsByArtistIdObservable(String artistId);
    Observable<List<Song>> getSongsByAlbumIdObservable(String albumId);

    // Completable emissions (operations without return values)
    Completable updateSong(Song song);
    Completable deleteSong(String songId);
}
