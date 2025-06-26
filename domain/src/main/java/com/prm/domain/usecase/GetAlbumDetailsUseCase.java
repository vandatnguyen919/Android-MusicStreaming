package com.prm.domain.usecase;

import com.prm.domain.model.Album;
import com.prm.domain.model.Artist;
import com.prm.domain.model.Song;
import com.prm.domain.repository.AlbumRepository;

import java.util.List;

import javax.inject.Inject;

public class GetAlbumDetailsUseCase {
    
    private final AlbumRepository albumRepository;
    
    @Inject
    public GetAlbumDetailsUseCase(AlbumRepository albumRepository) {
        this.albumRepository = albumRepository;
    }
    
    public AlbumDetails execute(String albumId) {
        Album album = albumRepository.getAlbumById(albumId);
        List<Song> songs = albumRepository.getSongsByAlbumId(albumId);
        Artist artist = albumRepository.getArtistByAlbumId(albumId);
        boolean isLiked = albumRepository.isAlbumLiked(albumId);
        
        return new AlbumDetails(album, songs, artist, isLiked);
    }
    
    public static class AlbumDetails {
        private final Album album;
        private final List<Song> songs;
        private final Artist artist;
        private final boolean isLiked;
        
        public AlbumDetails(Album album, List<Song> songs, Artist artist, boolean isLiked) {
            this.album = album;
            this.songs = songs;
            this.artist = artist;
            this.isLiked = isLiked;
        }
        
        public Album getAlbum() { return album; }
        public List<Song> getSongs() { return songs; }
        public Artist getArtist() { return artist; }
        public boolean isLiked() { return isLiked; }
    }
}
