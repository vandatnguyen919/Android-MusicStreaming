package com.prm.domain.usecase.user;

import com.prm.domain.model.User;
import com.prm.domain.repository.UserRepository;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Completable;

public class CreateUserUseCase {

    private final UserRepository userRepository;

    @Inject
    public CreateUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Completable execute(User user) {
        return userRepository.createUser(user);
    }
} 