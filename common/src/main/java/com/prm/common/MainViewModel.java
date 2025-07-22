package com.prm.common;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.prm.domain.repository.UserRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class MainViewModel extends ViewModel {

    private final UserRepository userRepository;
    private final CompositeDisposable disposables = new CompositeDisposable();

    private final MutableLiveData<Boolean> _shouldShowAds = new MutableLiveData<>(true);
    public LiveData<Boolean> shouldShowAds = _shouldShowAds;

    private final MutableLiveData<Boolean> _isUserRegistered = new MutableLiveData<>(false);
    public LiveData<Boolean> isUserRegistered = _isUserRegistered;

    @Inject
    public MainViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
        checkUserRegistration();
    }

    private void checkUserRegistration() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            _shouldShowAds.setValue(true);
            _isUserRegistered.setValue(false);
            return;
        }

        String userId = currentUser.getUid();
        disposables.add(
            userRepository.checkUserExistsByUserId(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    exists -> {
                        _isUserRegistered.setValue(exists);
                        // Turn off ads if user exists in repository (registered user)
                        _shouldShowAds.setValue(!exists);
                    },
                    error -> {
                        // On error, default to showing ads and user not registered
                        _shouldShowAds.setValue(true);
                        _isUserRegistered.setValue(false);
                    }
                )
        );
    }

    public void refreshUserStatus() {
        checkUserRegistration();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.clear();
    }
}
