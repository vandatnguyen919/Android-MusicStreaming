package com.prm.musicstreaming.di;

import com.prm.data.repository.ArtistRepositoryImpl;
import com.prm.data.repository.CartRepositoryImpl;
import com.prm.data.repository.SongRepositoryImpl;
import com.prm.data.repository.UserRepositoryImpl;
import com.prm.domain.repository.ArtistRepository;
import com.prm.domain.repository.CartRepository;
import com.prm.domain.repository.SongRepository;
import com.prm.domain.repository.UserRepository;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public abstract class RepositoryModule {

    @Binds
    @Singleton
    public abstract SongRepository bindMusicRepository(SongRepositoryImpl impl);

    @Binds
    @Singleton
    public abstract ArtistRepository bindArtistRepository(ArtistRepositoryImpl impl);

    @Binds
    @Singleton
    public abstract CartRepository bindCartRepository(CartRepositoryImpl impl);

    @Binds
    @Singleton
    public abstract UserRepository bindUserRepository(UserRepositoryImpl impl);
}