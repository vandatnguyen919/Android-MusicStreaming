package com.prm.domain.repository;

import com.prm.domain.model.Song;

import java.util.List;

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
    Single<List<Song>> getSongsByIds(List<String> songIds);

    Single<List<Song>> getCurrentUserFavoriteSongs(int limit); // Added method

    // Admin functionality
    Single<List<Song>> getPendingSongs(); // Get songs waiting for approval
    Single<List<Song>> getApprovedSongs(); // Get only approved songs for regular users
    Completable approveSong(String songId); // Approve a song
    Completable denySong(String songId); // Deny/Delete a song

    // Observable emissions (real-time updates)
    Observable<List<Song>> getAllSongsObservable();
    Observable<Song> getSongByIdObservable(String songId);
    Observable<List<Song>> getSongsByArtistIdObservable(String artistId);
    Observable<List<Song>> getSongsByAlbumIdObservable(String albumId);
    Observable<List<Song>> getPendingSongsObservable(); // Real-time pending songs for admin

    // Completable emissions (operations without return values)
    Completable updateSong(Song song);
    Completable deleteSong(String songId);
}
