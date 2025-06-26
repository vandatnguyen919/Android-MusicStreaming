package com.prm.domain.usecase;

import com.prm.domain.repository.AlbumRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ToggleAlbumLikeUseCaseTest {

    @Mock
    private AlbumRepository albumRepository;

    private ToggleAlbumLikeUseCase useCase;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        useCase = new ToggleAlbumLikeUseCase(albumRepository);
    }

    @Test
    public void execute_whenAlbumNotLiked_returnsTrue() {
        // Given
        String albumId = "test_album_id";
        when(albumRepository.isAlbumLiked(albumId)).thenReturn(false);

        // When
        boolean result = useCase.execute(albumId);

        // Then
        assertTrue(result);
        verify(albumRepository).isAlbumLiked(albumId);
        verify(albumRepository).toggleAlbumLike(albumId, true);
    }

    @Test
    public void execute_whenAlbumLiked_returnsFalse() {
        // Given
        String albumId = "test_album_id";
        when(albumRepository.isAlbumLiked(albumId)).thenReturn(true);

        // When
        boolean result = useCase.execute(albumId);

        // Then
        assertFalse(result);
        verify(albumRepository).isAlbumLiked(albumId);
        verify(albumRepository).toggleAlbumLike(albumId, false);
    }
}
