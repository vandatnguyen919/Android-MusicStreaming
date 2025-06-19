package com.prm.data.repository;

import com.prm.domain.repository.MusicRepository;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MusicRepositoryImpl implements MusicRepository {

    @Inject
    public MusicRepositoryImpl() {
    }
}
