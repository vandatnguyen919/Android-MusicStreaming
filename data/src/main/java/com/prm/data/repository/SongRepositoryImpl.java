package com.prm.data.repository;

import com.prm.domain.repository.SongRepository;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SongRepositoryImpl implements SongRepository {

    @Inject
    public SongRepositoryImpl() {
    }
}
