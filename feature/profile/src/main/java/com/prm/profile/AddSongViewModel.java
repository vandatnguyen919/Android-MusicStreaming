package com.prm.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.prm.domain.model.Song;
import com.prm.domain.repository.SongRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class AddSongViewModel extends ViewModel {

    private final SongRepository songRepository;
    private final CompositeDisposable disposables = new CompositeDisposable();

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    public final LiveData<Boolean> isLoading = _isLoading;

    private final MutableLiveData<AddSongResult> _addSongResult = new MutableLiveData<>();
    public final LiveData<AddSongResult> addSongResult = _addSongResult;

    @Inject
    public AddSongViewModel(SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    public void addSong(Song song) {
        // Check authentication first
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        android.util.Log.d("AddSongViewModel", "Checking authentication...");
        android.util.Log.d("AddSongViewModel", "Current user: " + (currentUser != null ? currentUser.getUid() : "null"));

        if (currentUser == null) {
            android.util.Log.e("AddSongViewModel", "No authenticated user found");
            _addSongResult.setValue(AddSongResult.error("Authentication required. Please log in to add songs."));
            return;
        }

        android.util.Log.d("AddSongViewModel", "User authenticated, proceeding with song addition");
        android.util.Log.d("AddSongViewModel", "User is anonymous: " + currentUser.isAnonymous());

        _isLoading.setValue(true);

        disposables.add(
            songRepository.addSong(song)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    songId -> {
                        _isLoading.setValue(false);
                        _addSongResult.setValue(AddSongResult.success(songId));
                    },
                    error -> {
                        _isLoading.setValue(false);
                        String errorMessage = getFormattedErrorMessage(error);
                        _addSongResult.setValue(AddSongResult.error(errorMessage));
                    }
                )
        );
    }

    private String getFormattedErrorMessage(Throwable error) {
        String message = error.getMessage();
        if (message == null) {
            return "Unknown error occurred while adding song.";
        }

        // Handle specific Firebase errors
        if (message.contains("PERMISSION_DENIED")) {
            return "Permission denied. Please check your authentication status and try again.";
        } else if (message.contains("UNAUTHENTICATED")) {
            return "Authentication required. Please log in to add songs.";
        } else if (message.contains("NETWORK_ERROR")) {
            return "Network error. Please check your internet connection and try again.";
        } else if (message.contains("UNAVAILABLE")) {
            return "Service temporarily unavailable. Please try again later.";
        }

        return "Error adding song: " + message;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<AddSongResult> getAddSongResult() {
        return addSongResult;
    }

    public boolean isUserAuthenticated() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    public void retryAuthentication() {
        android.util.Log.d("AddSongViewModel", "Authentication retry requested - user needs to login");
        _addSongResult.setValue(AddSongResult.error("Please login first to add songs."));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.clear();
    }

    // Result class for add song operation
    public static class AddSongResult {
        private final boolean success;
        private final String songId;
        private final String errorMessage;

        private AddSongResult(boolean success, String songId, String errorMessage) {
            this.success = success;
            this.songId = songId;
            this.errorMessage = errorMessage;
        }

        public static AddSongResult success(String songId) {
            return new AddSongResult(true, songId, null);
        }

        public static AddSongResult error(String errorMessage) {
            return new AddSongResult(false, null, errorMessage);
        }

        public boolean isSuccess() {
            return success;
        }

        public String getSongId() {
            return songId;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}
