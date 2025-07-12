package com.prm.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.prm.domain.model.User;
import com.prm.domain.repository.UserRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class EditProfileViewModel extends ViewModel {

    private final UserRepository userRepository;
    private final CompositeDisposable disposables = new CompositeDisposable();

    private final MutableLiveData<User> _userToEdit = new MutableLiveData<>();
    public LiveData<User> userToEdit = _userToEdit;

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    public final LiveData<Boolean> isLoading = _isLoading;

    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public final LiveData<String> error = _error;

    private final MutableLiveData<Boolean> _updateSuccess = new MutableLiveData<>();
    public final LiveData<Boolean> updateSuccess = _updateSuccess;

    @Inject
    public EditProfileViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void setUserToEdit(User user) {
        _userToEdit.setValue(user);
    }

    public void updateProfile(User user) {
        _isLoading.setValue(true);
        _updateSuccess.setValue(false);

        disposables.add(userRepository.updateUser(user)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    _isLoading.setValue(false);
                    _updateSuccess.setValue(true);
                }, throwable -> {
                    _isLoading.setValue(false);
                    _error.setValue("Failed to update profile: " + throwable.getMessage());
                    _updateSuccess.setValue(false);
                })
        );
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Boolean> getUpdateSuccess() {
        return updateSuccess;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.clear();
    }
} 