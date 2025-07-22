package com.prm.domain.repository;

import com.prm.domain.model.User;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public interface UserRepository {
    Completable updateUser(User user);
    Completable createUser(User user);
    Single<User> getCurrentUser();
    Single<Boolean> checkUserExistsByUserId(String userId);
}
