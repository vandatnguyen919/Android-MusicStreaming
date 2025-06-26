package com.prm.data.repository;

import com.prm.domain.model.Album;
import com.prm.domain.model.Artist;
import com.prm.domain.model.Song;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class AlbumRepositoryImplTest {

    private AlbumRepositoryImpl repository;

    @Before
    public void setUp() {
        repository = new AlbumRepositoryImpl();
    }

    @Test
    public void getAlbumById_returnsAlbum() {
        // Given
        String albumId = "1";

        // When
        Album album = repository.getAlbumById(albumId);

        // Then
        assertNotNull(album);
        assertEquals("1", album.getId());
        assertEquals("1(Remastered)", album.getName());
        assertEquals("artist_1", album.getArtistId());
        assertNotNull(album.getCoverImageUrl());
        assertNotNull(album.getSongIds());
        assertFalse(album.getSongIds().isEmpty());
    }

    @Test
    public void getSongsByAlbumId_returnsSongs() {
        // Given
        String albumId = "1";

        // When
        List<Song> songs = repository.getSongsByAlbumId(albumId);

        // Then
        assertNotNull(songs);
        assertFalse(songs.isEmpty());
        assertEquals(4, songs.size());
        
        Song firstSong = songs.get(0);
        assertEquals("song_1", firstSong.getId());
        assertEquals("Love Me Do - Mono / Remastered", firstSong.getTitle());
        assertEquals("artist_1", firstSong.getArtistId());
        assertEquals(albumId, firstSong.getAlbumId());
        assertEquals(143, firstSong.getDuration());
    }

    @Test
    public void getArtistByAlbumId_returnsArtist() {
        // Given
        String albumId = "1";

        // When
        Artist artist = repository.getArtistByAlbumId(albumId);

        // Then
        assertNotNull(artist);
        assertEquals("artist_1", artist.getId());
        assertEquals("The Beatles", artist.getName());
        assertNotNull(artist.getBio());
        assertNotNull(artist.getProfileImageUrl());
    }

    @Test
    public void isAlbumLiked_returnsFalseByDefault() {
        // Given
        String albumId = "1";

        // When
        boolean isLiked = repository.isAlbumLiked(albumId);

        // Then
        assertFalse(isLiked);
    }

    @Test
    public void toggleAlbumLike_doesNotThrowException() {
        // Given
        String albumId = "1";

        // When & Then (should not throw exception)
        repository.toggleAlbumLike(albumId, true);
        repository.toggleAlbumLike(albumId, false);
    }

    @Test
    public void downloadAlbum_doesNotThrowException() {
        // Given
        String albumId = "1";

        // When & Then (should not throw exception)
        repository.downloadAlbum(albumId);
    }

    @Test
    public void addAlbumToQueue_doesNotThrowException() {
        // Given
        String albumId = "1";

        // When & Then (should not throw exception)
        repository.addAlbumToQueue(albumId);
    }

    @Test
    public void addAlbumToPlaylist_doesNotThrowException() {
        // Given
        String albumId = "1";
        String playlistId = "playlist_1";

        // When & Then (should not throw exception)
        repository.addAlbumToPlaylist(albumId, playlistId);
    }
}
