package com.prm.data.repository;

import com.prm.domain.repository.UserRepository;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class UserRepositoryImpl implements UserRepository {

    @Inject
    public UserRepositoryImpl() {
    }
}
