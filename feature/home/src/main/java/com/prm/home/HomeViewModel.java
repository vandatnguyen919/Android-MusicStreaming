package com.prm.home;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.prm.domain.model.Artist;
import com.prm.domain.model.Song;
import com.prm.domain.repository.ArtistRepository;
import com.prm.domain.repository.SongRepository;
import com.prm.home.model.EditorPickUiModel;
import com.prm.home.model.RecentlyPlayedUiModel;
import com.prm.home.model.ReviewUiModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class HomeViewModel extends ViewModel {
    private SongRepository songRepository;
    private ArtistRepository artistRepository;
    private final CompositeDisposable disposables = new CompositeDisposable();

    // LiveData for UI states
    private final MutableLiveData<List<Song>> songs = new MutableLiveData<>();
    private final MutableLiveData<List<Artist>> artists = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();

    // LiveData for UI models
    private final MutableLiveData<List<RecentlyPlayedUiModel>> recentlyPlayed = new MutableLiveData<>();
    private final MutableLiveData<List<EditorPickUiModel>> editorPicks = new MutableLiveData<>();
    private final MutableLiveData<List<ReviewUiModel>> reviews = new MutableLiveData<>();

    // Cache for artists to avoid repeated lookups
    private final Map<String, Artist> artistCache = new HashMap<>();

    @Inject
    public HomeViewModel(SongRepository songRepository, ArtistRepository artistRepository) {
        this.songRepository = songRepository;
        this.artistRepository = artistRepository;
        loadData();
    }

    private void loadData() {
        isLoading.setValue(true);

        // Load songs and artists simultaneously
        disposables.add(songRepository.getAllSongs()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        this::onSongsLoaded,
                        throwable -> {
                            error.setValue("Error loading songs: " + throwable.getMessage());
                            isLoading.setValue(false);
                        }
                ));

        disposables.add(artistRepository.getAllArtists()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        this::onArtistsLoaded,
                        throwable -> {
                            error.setValue("Error loading artists: " + throwable.getMessage());
                        }
                ));
    }

    private void onSongsLoaded(List<Song> songList) {
        songs.setValue(songList);

        // Process songs for different sections
        processRecentlyPlayed(songList);
        processEditorPicks(songList);
        processReviews(songList);

        isLoading.setValue(false);
    }

    private void onArtistsLoaded(List<Artist> artistList) {
        artists.setValue(artistList);

        // Populate artist cache
        artistCache.clear();
        for (Artist artist : artistList) {
            artistCache.put(artist.getId(), artist);
        }

        // Reprocess UI models if songs are already loaded
        if (songs.getValue() != null) {
            processRecentlyPlayed(songs.getValue());
            processEditorPicks(songs.getValue());
            processReviews(songs.getValue());
        }
    }

    private void processRecentlyPlayed(List<Song> songList) {
        List<RecentlyPlayedUiModel> recentlyPlayedList = new ArrayList<>();

        // Take first 3 songs as recently played (in real app, this would be based on user history)
        List<Song> recentSongs = songList.size() > 3 ? songList.subList(0, 3) : songList;

        for (Song song : recentSongs) {
            Artist artist = artistCache.get(song.getArtistId());
            String artistName = song.getArtistId();
            String imageUrl = artist != null ? artist.getProfileImageUrl() : "";

            recentlyPlayedList.add(new RecentlyPlayedUiModel(song, artistName, imageUrl, false));
        }

        recentlyPlayed.setValue(recentlyPlayedList);
    }

    private void processEditorPicks(List<Song> songList) {
        List<EditorPickUiModel> editorPicksList = new ArrayList<>();

        // Shuffle and take first 3 songs as editor picks
        List<Song> shuffledSongs = new ArrayList<>(songList);
        Collections.shuffle(shuffledSongs);
        List<Song> pickedSongs = shuffledSongs.size() > 3 ? shuffledSongs.subList(0, 3) : shuffledSongs;

        for (Song song : pickedSongs) {
            Artist artist = artistCache.get(song.getArtistId());
            String artistName = song.getArtistId();
            String imageUrl = artist != null ? artist.getProfileImageUrl() : "";
            String subtitle = "Recommended • " + artistName;

            editorPicksList.add(new EditorPickUiModel(song, artistName, imageUrl, subtitle, true));
        }

        editorPicks.setValue(editorPicksList);
    }

    private void processReviews(List<Song> songList) {
        List<ReviewUiModel> reviewList = new ArrayList<>();

        // Take last 2 songs as reviews
        int startIndex = Math.max(0, songList.size() - 2);
        List<Song> reviewSongs = songList.subList(startIndex, songList.size());

        for (Song song : reviewSongs) {
            Artist artist = artistCache.get(song.getArtistId());
            String artistName = artist != null ? artist.getName() : "Unknown Artist";
            String imageUrl = artist != null ? artist.getProfileImageUrl() : "";
            String reviewText = "Featured Review";
            String reviewTitle = song.getTitle();

            reviewList.add(new ReviewUiModel(song, artistName, imageUrl, reviewText, reviewTitle));
        }

        reviews.setValue(reviewList);
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

    public void refreshSongs() {
        songRepository.getAllSongs()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                updatedSongs -> songs.setValue(updatedSongs),
                error -> Log.e("HomeViewModel", "Error refreshing songs", error)
            );
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    // Getters for UI models
    public LiveData<List<RecentlyPlayedUiModel>> getRecentlyPlayed() {
        return recentlyPlayed;
    }

    public LiveData<List<EditorPickUiModel>> getEditorPicks() {
        return editorPicks;
    }

    public LiveData<List<ReviewUiModel>> getReviews() {
        return reviews;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // Dispose all subscriptions to prevent memory leaks
        disposables.clear();
    }
}