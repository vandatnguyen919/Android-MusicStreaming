package com.prm.domain.usecase;

import com.prm.domain.model.Album;
import com.prm.domain.model.Artist;
import com.prm.domain.model.Playlist;
import com.prm.domain.model.Song;
import com.prm.domain.repository.MusicRepository;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Observable;

public class SearchUseCase {
    
    private final MusicRepository musicRepository;
    
    @Inject
    public SearchUseCase(MusicRepository musicRepository) {
        this.musicRepository = musicRepository;
    }
    
    public Observable<List<Artist>> searchArtists(String query) {
        return musicRepository.searchArtists(query);
    }
    
    public Observable<List<Song>> searchSongs(String query) {
        return musicRepository.searchSongs(query);
    }
    
    public Observable<List<Album>> searchAlbums(String query) {
        return musicRepository.searchAlbums(query);
    }
    
    public Observable<List<Playlist>> searchPlaylists(String query) {
        return musicRepository.searchPlaylists(query);
    }
}
