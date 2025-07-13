package com.prm.domain.repository;

import com.prm.domain.model.Album;
import com.prm.domain.model.Artist;
import com.prm.domain.model.Playlist;
import com.prm.domain.model.Song;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;


public interface MusicRepository {
    

    Observable<List<Artist>> searchArtists(String query);
    

    Observable<List<Song>> searchSongs(String query);
    

    Observable<List<Album>> searchAlbums(String query);
    

    Observable<List<Playlist>> searchPlaylists(String query);
    
    // Thêm  repo như fetching bài hát, album, playlist.
}
