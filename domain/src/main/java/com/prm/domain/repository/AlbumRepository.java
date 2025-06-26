package com.prm.domain.repository;

import com.prm.domain.model.Album;
import com.prm.domain.model.Song;
import com.prm.domain.model.Artist;

import java.util.List;

public interface AlbumRepository {
    
    /**
     * Get album by ID
     */
    Album getAlbumById(String albumId);
    
    /**
     * Get songs in an album
     */
    List<Song> getSongsByAlbumId(String albumId);
    
    /**
     * Get artist information for an album
     */
    Artist getArtistByAlbumId(String albumId);
    
    /**
     * Like/Unlike an album
     */
    void toggleAlbumLike(String albumId, boolean isLiked);
    
    /**
     * Check if album is liked
     */
    boolean isAlbumLiked(String albumId);
    
    /**
     * Download album
     */
    void downloadAlbum(String albumId);
    
    /**
     * Add all songs from album to queue
     */
    void addAlbumToQueue(String albumId);
    
    /**
     * Add all songs from album to playlist
     */
    void addAlbumToPlaylist(String albumId, String playlistId);
}
