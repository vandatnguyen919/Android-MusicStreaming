package com.prm.data.di;

import com.prm.data.repository.AlbumRepositoryImpl;
import com.prm.data.repository.ArtistRepositoryImpl;
import com.prm.data.repository.CartRepositoryImpl;
import com.prm.data.repository.MusicRepositoryImpl;
import com.prm.data.repository.PlaylistRepositoryImpl;
import com.prm.data.repository.SongRepositoryImpl;
import com.prm.data.repository.UserRepositoryImpl;
import com.prm.domain.repository.AlbumRepository;
import com.prm.domain.repository.ArtistRepository;
import com.prm.domain.repository.CartRepository;
import com.prm.domain.repository.MusicRepository;
import com.prm.domain.repository.PlaylistRepository;
import com.prm.domain.repository.SongRepository;
import com.prm.domain.repository.UserRepository;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

import javax.inject.Singleton;

@Module
@InstallIn(SingletonComponent.class)
public abstract class DataRepositoryModule {

    @Binds
    @Singleton
    public abstract MusicRepository bindMusicRepository(MusicRepositoryImpl musicRepositoryImpl);
    
    @Binds
    @Singleton
    public abstract PlaylistRepository bindPlaylistRepository(PlaylistRepositoryImpl playlistRepositoryImpl);

    @Binds
    @Singleton
    public abstract UserRepository bindUserRepository(UserRepositoryImpl userRepositoryImpl);

    @Binds
    @Singleton
    public abstract SongRepository bindSongRepository(SongRepositoryImpl songRepositoryImpl);

    @Binds
    @Singleton
    public abstract AlbumRepository bindAlbumRepository(AlbumRepositoryImpl albumRepositoryImpl);

    @Binds
    @Singleton
    public abstract ArtistRepository bindArtistRepository(ArtistRepositoryImpl artistRepositoryImpl);

    @Binds
    @Singleton
    public abstract CartRepository bindCartRepository(CartRepositoryImpl cartRepositoryImpl);
}
