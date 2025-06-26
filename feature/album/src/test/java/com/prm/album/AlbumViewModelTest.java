package com.prm.album;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.prm.album.model.AlbumUiState;
import com.prm.album.model.SongUiModel;
import com.prm.domain.model.Album;
import com.prm.domain.model.Artist;
import com.prm.domain.model.Song;
import com.prm.domain.usecase.AlbumActionsUseCase;
import com.prm.domain.usecase.GetAlbumDetailsUseCase;
import com.prm.domain.usecase.ToggleAlbumLikeUseCase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AlbumViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private GetAlbumDetailsUseCase getAlbumDetailsUseCase;

    @Mock
    private ToggleAlbumLikeUseCase toggleAlbumLikeUseCase;

    @Mock
    private AlbumActionsUseCase albumActionsUseCase;

    @Mock
    private Observer<AlbumUiState> uiStateObserver;

    @Mock
    private Observer<List<SongUiModel>> songsObserver;

    @Mock
    private Observer<Boolean> showBottomSheetObserver;

    private AlbumViewModel viewModel;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        viewModel = new AlbumViewModel(
            getAlbumDetailsUseCase,
            toggleAlbumLikeUseCase,
            albumActionsUseCase
        );
        
        viewModel.uiState.observeForever(uiStateObserver);
        viewModel.songs.observeForever(songsObserver);
        viewModel.showBottomSheet.observeForever(showBottomSheetObserver);
    }

    @Test
    public void loadAlbum_success_updatesUiState() {
        // Given
        String albumId = "test_album_id";
        Album album = new Album(albumId, "Test Album", "artist_1", "cover_url", Arrays.asList("song_1"));
        Artist artist = new Artist("artist_1", "Test Artist", "Bio", "avatar_url");
        List<Song> songs = Arrays.asList(
            new Song("song_1", "Test Song", "artist_1", albumId, 180, "song_url")
        );
        
        GetAlbumDetailsUseCase.AlbumDetails albumDetails = 
            new GetAlbumDetailsUseCase.AlbumDetails(album, songs, artist, false);
        
        when(getAlbumDetailsUseCase.execute(albumId)).thenReturn(albumDetails);

        // When
        viewModel.loadAlbum(albumId);

        // Then
        verify(getAlbumDetailsUseCase).execute(albumId);
        verify(uiStateObserver).onChanged(any(AlbumUiState.class));
        verify(songsObserver).onChanged(any(List.class));
    }

    @Test
    public void toggleLike_success_updatesLikeStatus() {
        // Given
        String albumId = "test_album_id";
        when(toggleAlbumLikeUseCase.execute(albumId)).thenReturn(true);
        
        // Setup initial state
        Album album = new Album(albumId, "Test Album", "artist_1", "cover_url", Arrays.asList("song_1"));
        Artist artist = new Artist("artist_1", "Test Artist", "Bio", "avatar_url");
        List<Song> songs = Arrays.asList(
            new Song("song_1", "Test Song", "artist_1", albumId, 180, "song_url")
        );
        
        GetAlbumDetailsUseCase.AlbumDetails albumDetails = 
            new GetAlbumDetailsUseCase.AlbumDetails(album, songs, artist, false);
        
        when(getAlbumDetailsUseCase.execute(albumId)).thenReturn(albumDetails);
        viewModel.loadAlbum(albumId);

        // When
        viewModel.toggleLike();

        // Then
        verify(toggleAlbumLikeUseCase).execute(albumId);
        verify(uiStateObserver).onChanged(any(AlbumUiState.class));
    }

    @Test
    public void downloadAlbum_callsUseCase() {
        // Given
        String albumId = "test_album_id";
        
        // Setup initial state
        Album album = new Album(albumId, "Test Album", "artist_1", "cover_url", Arrays.asList("song_1"));
        Artist artist = new Artist("artist_1", "Test Artist", "Bio", "avatar_url");
        List<Song> songs = Arrays.asList(
            new Song("song_1", "Test Song", "artist_1", albumId, 180, "song_url")
        );
        
        GetAlbumDetailsUseCase.AlbumDetails albumDetails = 
            new GetAlbumDetailsUseCase.AlbumDetails(album, songs, artist, false);
        
        when(getAlbumDetailsUseCase.execute(albumId)).thenReturn(albumDetails);
        viewModel.loadAlbum(albumId);

        // When
        viewModel.downloadAlbum();

        // Then
        verify(albumActionsUseCase).downloadAlbum(albumId);
    }

    @Test
    public void showMoreOptions_updatesBottomSheetState() {
        // When
        viewModel.showMoreOptions();

        // Then
        verify(showBottomSheetObserver).onChanged(true);
    }

    @Test
    public void hideBottomSheet_updatesBottomSheetState() {
        // When
        viewModel.hideBottomSheet();

        // Then
        verify(showBottomSheetObserver).onChanged(false);
    }

    @Test
    public void addToQueue_callsUseCaseAndHidesBottomSheet() {
        // Given
        String albumId = "test_album_id";
        
        // Setup initial state
        Album album = new Album(albumId, "Test Album", "artist_1", "cover_url", Arrays.asList("song_1"));
        Artist artist = new Artist("artist_1", "Test Artist", "Bio", "avatar_url");
        List<Song> songs = Arrays.asList(
            new Song("song_1", "Test Song", "artist_1", albumId, 180, "song_url")
        );
        
        GetAlbumDetailsUseCase.AlbumDetails albumDetails = 
            new GetAlbumDetailsUseCase.AlbumDetails(album, songs, artist, false);
        
        when(getAlbumDetailsUseCase.execute(albumId)).thenReturn(albumDetails);
        viewModel.loadAlbum(albumId);

        // When
        viewModel.addToQueue();

        // Then
        verify(albumActionsUseCase).addToQueue(albumId);
        verify(showBottomSheetObserver).onChanged(false);
    }

    @Test
    public void playPauseAlbum_togglesPlayingState() {
        // Given
        String albumId = "test_album_id";
        
        // Setup initial state
        Album album = new Album(albumId, "Test Album", "artist_1", "cover_url", Arrays.asList("song_1"));
        Artist artist = new Artist("artist_1", "Test Artist", "Bio", "avatar_url");
        List<Song> songs = Arrays.asList(
            new Song("song_1", "Test Song", "artist_1", albumId, 180, "song_url")
        );
        
        GetAlbumDetailsUseCase.AlbumDetails albumDetails = 
            new GetAlbumDetailsUseCase.AlbumDetails(album, songs, artist, false);
        
        when(getAlbumDetailsUseCase.execute(albumId)).thenReturn(albumDetails);
        viewModel.loadAlbum(albumId);

        // When
        viewModel.playPauseAlbum();

        // Then
        verify(uiStateObserver).onChanged(any(AlbumUiState.class));
        verify(songsObserver).onChanged(any(List.class));
    }

    @Test
    public void playSong_updatesCurrentPlayingSong() {
        // Given
        String albumId = "test_album_id";
        String songId = "song_1";
        
        // Setup initial state
        Album album = new Album(albumId, "Test Album", "artist_1", "cover_url", Arrays.asList(songId));
        Artist artist = new Artist("artist_1", "Test Artist", "Bio", "avatar_url");
        List<Song> songs = Arrays.asList(
            new Song(songId, "Test Song", "artist_1", albumId, 180, "song_url")
        );
        
        GetAlbumDetailsUseCase.AlbumDetails albumDetails = 
            new GetAlbumDetailsUseCase.AlbumDetails(album, songs, artist, false);
        
        when(getAlbumDetailsUseCase.execute(albumId)).thenReturn(albumDetails);
        viewModel.loadAlbum(albumId);

        // When
        viewModel.playSong(songId);

        // Then
        verify(uiStateObserver).onChanged(any(AlbumUiState.class));
        verify(songsObserver).onChanged(any(List.class));
    }
}
