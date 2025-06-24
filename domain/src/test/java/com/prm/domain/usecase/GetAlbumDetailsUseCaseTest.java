package com.prm.domain.usecase;

import com.prm.domain.model.Album;
import com.prm.domain.model.Artist;
import com.prm.domain.model.Song;
import com.prm.domain.repository.AlbumRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GetAlbumDetailsUseCaseTest {

    @Mock
    private AlbumRepository albumRepository;

    private GetAlbumDetailsUseCase useCase;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        useCase = new GetAlbumDetailsUseCase(albumRepository);
    }

    @Test
    public void execute_returnsAlbumDetails() {
        // Given
        String albumId = "test_album_id";
        
        Album album = new Album(albumId, "Test Album", "artist_1", "cover_url", Arrays.asList("song_1"));
        Artist artist = new Artist("artist_1", "Test Artist", "Bio", "avatar_url");
        List<Song> songs = Arrays.asList(
            new Song("song_1", "Test Song", "artist_1", albumId, 180, "song_url")
        );
        boolean isLiked = false;

        when(albumRepository.getAlbumById(albumId)).thenReturn(album);
        when(albumRepository.getSongsByAlbumId(albumId)).thenReturn(songs);
        when(albumRepository.getArtistByAlbumId(albumId)).thenReturn(artist);
        when(albumRepository.isAlbumLiked(albumId)).thenReturn(isLiked);

        // When
        GetAlbumDetailsUseCase.AlbumDetails result = useCase.execute(albumId);

        // Then
        assertNotNull(result);
        assertEquals(album, result.getAlbum());
        assertEquals(songs, result.getSongs());
        assertEquals(artist, result.getArtist());
        assertFalse(result.isLiked());

        verify(albumRepository).getAlbumById(albumId);
        verify(albumRepository).getSongsByAlbumId(albumId);
        verify(albumRepository).getArtistByAlbumId(albumId);
        verify(albumRepository).isAlbumLiked(albumId);
    }
}
