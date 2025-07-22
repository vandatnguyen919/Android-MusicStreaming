package com.prm.data.repository;

import com.google.firebase.auth.FirebaseAuth;
import com.prm.data.source.remote.FirebaseUserService;
import com.prm.domain.model.User;
import com.prm.domain.repository.UserRepository;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Singleton
public class UserRepositoryImpl implements UserRepository {

    private final FirebaseUserService firebaseUserService;

    @Inject
    public UserRepositoryImpl(FirebaseUserService firebaseUserService) {
        this.firebaseUserService = firebaseUserService;
    }

    @Override
    public Completable updateUser(User user) {
        return firebaseUserService.updateUser(user);
    }

    @Override
    public Completable createUser(User user) {
        return firebaseUserService.createUser(user);
    }

    @Override
    public Single<Boolean> checkUserExistsByUserId(String userId) {
        return firebaseUserService.checkUserExistsByUserId(userId);
    }

    @Override
    public Single<User> getCurrentUser() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;

        if (userId == null) {
            return Single.error(new Exception("No authenticated user found."));
        }
        return firebaseUserService.getUserById(userId);
    }
}
