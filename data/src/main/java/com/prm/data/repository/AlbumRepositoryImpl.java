package com.prm.data.repository;

import com.prm.domain.model.Album;
import com.prm.domain.model.Artist;
import com.prm.domain.model.Song;
import com.prm.domain.repository.AlbumRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AlbumRepositoryImpl implements AlbumRepository {

    @Inject
    public AlbumRepositoryImpl() {
    }

    @Override
    public Album getAlbumById(String albumId) {
        // Mock data - replace with actual API call
        return new Album(
            "1",
            "1(Remastered)",
            "artist_1",
            "https://example.com/album_cover.jpg",
            Arrays.asList("song_1", "song_2", "song_3", "song_4")
        );
    }

    @Override
    public List<Song> getSongsByAlbumId(String albumId) {
        // Mock data - replace with actual API call
        List<Song> songs = new ArrayList<>();
        songs.add(new Song("song_1", "Love Me Do - Mono / Remastered", "artist_1", albumId, 143, "url1"));
        songs.add(new Song("song_2", "From Me to You - Mono / Remastered", "artist_1", albumId, 116, "url2"));
        songs.add(new Song("song_3", "She Loves You - Mono / Remastered", "artist_1", albumId, 122, "url3"));
        songs.add(new Song("song_4", "I Want To Hold Your Hand - Remastered 2015", "artist_1", albumId, 145, "url4"));
        return songs;
    }

    @Override
    public Artist getArtistByAlbumId(String albumId) {
        // Mock data - replace with actual API call
        return new Artist(
            "artist_1",
            "The Beatles",
            "The Beatles were an English rock band...",
            "https://example.com/artist_avatar.jpg"
        );
    }

    @Override
    public void toggleAlbumLike(String albumId, boolean isLiked) {
        // Mock implementation - replace with actual API call
        // In real implementation, this would save to local database or API
    }

    @Override
    public boolean isAlbumLiked(String albumId) {
        // Mock data - replace with actual local database query
        return false;
    }

    @Override
    public void downloadAlbum(String albumId) {
        // Mock implementation - replace with actual download logic
    }

    @Override
    public void addAlbumToQueue(String albumId) {
        // Mock implementation - replace with actual queue management
    }

    @Override
    public void addAlbumToPlaylist(String albumId, String playlistId) {
        // Mock implementation - replace with actual playlist management
    }
}
