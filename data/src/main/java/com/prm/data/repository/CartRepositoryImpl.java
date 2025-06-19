package com.prm.data.repository;

import com.prm.domain.repository.CartRepository;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CartRepositoryImpl implements CartRepository {

    @Inject
    public CartRepositoryImpl() {
    }
}
