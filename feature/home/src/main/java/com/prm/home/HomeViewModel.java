package com.prm.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.prm.domain.model.Artist;
import com.prm.domain.model.Song;
import com.prm.domain.repository.ArtistRepository;
import com.prm.domain.repository.SongRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class HomeViewModel extends ViewModel {

    private final SongRepository songRepository;
    private final ArtistRepository artistRepository;
    private final CompositeDisposable disposables = new CompositeDisposable();

    // LiveData for UI states
    private final MutableLiveData<List<Song>> songs = new MutableLiveData<>();
    private final MutableLiveData<List<Artist>> artists = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();

    @Inject
    public HomeViewModel(SongRepository songRepository, ArtistRepository artistRepository) {
        this.songRepository = songRepository;
        this.artistRepository = artistRepository;
        loadSongs();
        loadArtists();
    }

    private void loadSongs() {
        isLoading.setValue(true);
        disposables.add(songRepository.getAllSongs()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            songs.setValue(result);
                            isLoading.setValue(false);
                        },
                        throwable -> {
                            error.setValue("Error loading songs: " + throwable.getMessage());
                            isLoading.setValue(false);
                        }
                ));
    }

    private void loadArtists() {
        isLoading.setValue(true);
        disposables.add(artistRepository.getAllArtists()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            artists.setValue(result);
                            isLoading.setValue(false);
                        },
                        throwable -> {
                            error.setValue("Error loading artists: " + throwable.getMessage());
                            isLoading.setValue(false);
                        }
                ));
    }

    public void searchSongs(String query) {
        isLoading.setValue(true);
        disposables.add(songRepository.searchSongsByTitle(query)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            songs.setValue(result);
                            isLoading.setValue(false);
                        },
                        throwable -> {
                            error.setValue("Search failed: " + throwable.getMessage());
                            isLoading.setValue(false);
                        }
                ));
    }

    public void loadSongsByArtist(String artistId) {
        isLoading.setValue(true);
        disposables.add(songRepository.getSongsByArtistId(artistId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            songs.setValue(result);
                            isLoading.setValue(false);
                        },
                        throwable -> {
                            error.setValue("Error loading artist songs: " + throwable.getMessage());
                            isLoading.setValue(false);
                        }
                ));
    }

    // Getters for LiveData
    public LiveData<List<Song>> getSongs() {
        return songs;
    }

    public LiveData<List<Artist>> getArtists() {
        return artists;
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
        // Dispose all subscriptions to prevent memory leaks
        disposables.clear();
    }
}