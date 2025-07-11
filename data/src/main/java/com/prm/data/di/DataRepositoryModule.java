package com.prm.data.di;

import com.prm.data.repository.MusicRepositoryImpl;
import com.prm.data.repository.PlaylistRepositoryImpl;
import com.prm.domain.repository.MusicRepository;
import com.prm.domain.repository.PlaylistRepository;

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
}
