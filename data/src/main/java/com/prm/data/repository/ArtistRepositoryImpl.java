package com.prm.data.repository;

import com.prm.domain.repository.ArtistRepository;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ArtistRepositoryImpl implements ArtistRepository {

    @Inject
    public ArtistRepositoryImpl() {
    }
}
