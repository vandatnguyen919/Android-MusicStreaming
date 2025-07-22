package com.prm.payment.checkout;

import androidx.lifecycle.ViewModel;

import com.prm.domain.model.User;
import com.prm.domain.repository.UserRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class CheckoutViewModel extends ViewModel {
    // TODO: Implement the ViewModel

    private final UserRepository userRepository;

    @Inject
    public CheckoutViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void addPremiumUser(String userId) {
        User user = new User();
        user.setId(userId);
        userRepository.createUser(user)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    // Handle success
                }, throwable -> {
                    // Handle error
                });
    }
}