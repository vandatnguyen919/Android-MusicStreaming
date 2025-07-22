package com.prm.library;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.prm.domain.model.Playlist;
import com.prm.domain.usecase.playlist.AddSongToPlaylistUseCase;
import com.prm.domain.usecase.playlist.CreatePlaylistUseCase;
import com.prm.domain.usecase.playlist.DeletePlaylistUseCase;
import com.prm.domain.usecase.playlist.GetPlaylistsForUserUseCase;
import com.prm.domain.usecase.playlist.RemoveSongFromPlaylistUseCase;

import java.util.List;
import java.util.ArrayList;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

@HiltViewModel
public class LibraryViewModel extends ViewModel {

    private final GetPlaylistsForUserUseCase getPlaylistsForUserUseCase;
    private final CreatePlaylistUseCase createPlaylistUseCase;
    private final AddSongToPlaylistUseCase addSongToPlaylistUseCase;
    private final RemoveSongFromPlaylistUseCase removeSongFromPlaylistUseCase;
    private final DeletePlaylistUseCase deletePlaylistUseCase;
    private final CompositeDisposable disposables = new CompositeDisposable();

    private final MutableLiveData<List<Playlist>> playlists = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> _error = new MutableLiveData<>(null);

    public LiveData<Boolean> isLoading = _isLoading;
    public LiveData<String> error = _error;

    @Inject
    public LibraryViewModel(
            GetPlaylistsForUserUseCase getPlaylistsForUserUseCase,
            CreatePlaylistUseCase createPlaylistUseCase,
            AddSongToPlaylistUseCase addSongToPlaylistUseCase,
            RemoveSongFromPlaylistUseCase removeSongFromPlaylistUseCase,
            DeletePlaylistUseCase deletePlaylistUseCase) {
        this.getPlaylistsForUserUseCase = getPlaylistsForUserUseCase;
        this.createPlaylistUseCase = createPlaylistUseCase;
        this.addSongToPlaylistUseCase = addSongToPlaylistUseCase;
        this.removeSongFromPlaylistUseCase = removeSongFromPlaylistUseCase;
        this.deletePlaylistUseCase = deletePlaylistUseCase;
        fetchPlaylistsForUser();
    }

    public void fetchPlaylistsForUser() {
        _isLoading.setValue(true);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            _error.setValue("User not logged in");
            _isLoading.setValue(false);
            return;
        }
        String userId = user.getUid();
        disposables.add(getPlaylistsForUserUseCase.execute(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(playlists -> {
                    this.playlists.setValue(playlists);
                    _isLoading.setValue(false);
                    _error.setValue(null);
                }, throwable -> {
                    _error.setValue("Failed to load playlists: " + throwable.getMessage());
                    _isLoading.setValue(false);
                })
        );
    }

    public LiveData<List<Playlist>> getPlaylists() {
        return playlists;
    }

    public void createPlaylist(String name) {
        _isLoading.setValue(true);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            _error.setValue("User not logged in");
            _isLoading.setValue(false);
            return;
        }
        String userId = user.getUid();
        Playlist newPlaylist = new Playlist("", name, userId, new ArrayList<>(), "", new ArrayList<>());
        disposables.add(createPlaylistUseCase.execute(newPlaylist)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    _isLoading.setValue(false);
                    _error.setValue(null);
                    fetchPlaylistsForUser(); // Refresh the list after creating a new playlist
                }, throwable -> {
                    _error.setValue("Failed to create playlist: " + throwable.getMessage());
                    _isLoading.setValue(false);
                })
        );
    }

    public void addSongToPlaylist(String playlistId, String songId) {
        _isLoading.setValue(true);
        disposables.add(addSongToPlaylistUseCase.execute(playlistId, songId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    _isLoading.setValue(false);
                    _error.setValue(null);
                    fetchPlaylistsForUser();
                }, throwable -> {
                    _error.setValue("Failed to add song to playlist: " + throwable.getMessage());
                    _isLoading.setValue(false);
                })
        );
    }

    public void removeSongFromPlaylist(String playlistId, String songId) {
        _isLoading.setValue(true);
        disposables.add(removeSongFromPlaylistUseCase.execute(playlistId, songId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    _isLoading.setValue(false);
                    _error.setValue(null);
                    fetchPlaylistsForUser(); // Refresh the list after removing the song
                }, throwable -> {
                    _error.setValue("Failed to remove song from playlist: " + throwable.getMessage());
                    _isLoading.setValue(false);
                })
        );
    }

    public void deletePlaylist(String playlistId) {
        _isLoading.setValue(true);
        disposables.add(deletePlaylistUseCase.execute(playlistId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    _isLoading.setValue(false);
                    _error.setValue(null);
                    fetchPlaylistsForUser(); // Refresh the list after deleting the playlist
                }, throwable -> {
                    _error.setValue("Failed to delete playlist: " + throwable.getMessage());
                    _isLoading.setValue(false);
                })
        );
    }

    public LiveData<Boolean> getIsLoading() {
        return _isLoading;
    }

    public LiveData<String> getError() {
        return _error;
    }

    public LiveData<Playlist> getPlaylistById(String playlistId) {
        MutableLiveData<Playlist> playlistLiveData = new MutableLiveData<>();
        playlists.observeForever(list -> {
            if (list != null) {
                for (Playlist p : list) {
                    if (p.getId().equals(playlistId)) {
                        playlistLiveData.setValue(p);
                        break;
                    }
                }
            }
        });
        return playlistLiveData;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.clear();
    }
}