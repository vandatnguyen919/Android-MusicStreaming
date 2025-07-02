package com.prm.data.di;

import com.prm.data.repository.MusicRepositoryImpl;
import com.prm.domain.repository.MusicRepository;

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
    
    // Thêm các repository binding khác
}
