package com.prm.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.prm.domain.model.User;
import com.prm.domain.repository.UserRepository;
import com.prm.domain.usecase.user.UpdateUserProfileUseCase;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class ProfileViewModel extends ViewModel {

    private final UserRepository userRepository;
    private final UpdateUserProfileUseCase updateUserProfileUseCase;
    private final CompositeDisposable disposables = new CompositeDisposable();

    private final MutableLiveData<User> _currentUser = new MutableLiveData<>();
    public final LiveData<User> currentUser = _currentUser;

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    public final LiveData<Boolean> isLoading = _isLoading;

    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public final LiveData<String> error = _error;

    @Inject
    public ProfileViewModel(UserRepository userRepository, UpdateUserProfileUseCase updateUserProfileUseCase) {
        this.userRepository = userRepository;
        this.updateUserProfileUseCase = updateUserProfileUseCase;
        fetchCurrentUser();
    }

    public void fetchCurrentUser() {
        // Temporarily comment out authentication check for testing edit profile
        // FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        // if (firebaseUser == null) {
        //     _error.setValue("User not authenticated.");
        //     _isLoading.setValue(false);
        //     return;
        // }

        _isLoading.setValue(true);
        disposables.add(userRepository.getCurrentUser()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(user -> {
                    _currentUser.setValue(user);
                    _isLoading.setValue(false);
                }, throwable -> {
                    _error.setValue("Failed to load user: " + throwable.getMessage());
                    _isLoading.setValue(false);
                })
        );
    }

    public void updateProfile(User user) {
        _isLoading.setValue(true);
        disposables.add(updateUserProfileUseCase.execute(user)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    // On success
                    _isLoading.setValue(false);
                    _error.setValue(null); // Clear any previous error
                    fetchCurrentUser(); // Re-fetch user data to reflect changes
                }, (Throwable throwable) -> { // Explicitly cast throwable to Throwable
                    // On error
                    _error.setValue("Failed to update profile: " + throwable.getMessage());
                    _isLoading.setValue(false);
                })
        );
    }

    public LiveData<User> getCurrentUser() {
        return currentUser;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.clear();
    }
}