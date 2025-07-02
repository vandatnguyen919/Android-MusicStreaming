package com.prm.search;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.prm.domain.model.Album;
import com.prm.domain.model.Artist;
import com.prm.domain.model.Playlist;
import com.prm.domain.model.Song;
import com.prm.domain.usecase.SearchUseCase;
import com.prm.domain.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;

@HiltViewModel
public class SearchViewModel extends ViewModel {
    private final SearchUseCase searchUseCase;
    private final CompositeDisposable disposables = new CompositeDisposable();
    private final PublishSubject<String> searchSubject = PublishSubject.create();
    
    private final MutableLiveData<List<Artist>> _artists = new MutableLiveData<>(new ArrayList<>());
    public LiveData<List<Artist>> artists = _artists;
    
    private final MutableLiveData<List<Song>> _songs = new MutableLiveData<>(new ArrayList<>());
    public LiveData<List<Song>> songs = _songs;
    
    private final MutableLiveData<List<Album>> _albums = new MutableLiveData<>(new ArrayList<>());
    public LiveData<List<Album>> albums = _albums;
    
    private final MutableLiveData<List<Playlist>> _playlists = new MutableLiveData<>(new ArrayList<>());
    public LiveData<List<Playlist>> playlists = _playlists;
    
    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    public LiveData<Boolean> isLoading = _isLoading;
    
    private final MutableLiveData<Boolean> _isEmpty = new MutableLiveData<>(false);
    public LiveData<Boolean> isEmpty = _isEmpty;

    @Inject
    public SearchViewModel(SearchUseCase searchUseCase) {
        this.searchUseCase = searchUseCase;
        setupSearchObservable();
    }
    
    private void setupSearchObservable() {
        disposables.add(
            searchSubject
                .debounce(Constants.Search.SEARCH_DEBOUNCE_TIME_MS, TimeUnit.MILLISECONDS) // Đợi sau khi người dùng ngừng gõ
                .distinctUntilChanged() // Bỏ qua các lần gọi với cùng 1 query
                .filter(query -> !query.isEmpty())
                .doOnNext(__ -> _isLoading.postValue(true))
                .switchMap(query -> 
                    Observable.zip(
                        searchUseCase.searchArtists(query).subscribeOn(Schedulers.io()),
                        searchUseCase.searchSongs(query).subscribeOn(Schedulers.io()),
                        searchUseCase.searchAlbums(query).subscribeOn(Schedulers.io()),
                        searchUseCase.searchPlaylists(query).subscribeOn(Schedulers.io()),
                        (artists, songs, albums, playlists) -> {
                            _artists.postValue(artists);
                            _songs.postValue(songs);
                            _albums.postValue(albums);
                            _playlists.postValue(playlists);
                            
                            boolean resultsEmpty = artists.isEmpty() && 
                                songs.isEmpty() && 
                                albums.isEmpty() && 
                                playlists.isEmpty();
                            _isEmpty.postValue(resultsEmpty);
                            
                            return resultsEmpty;
                        }
                    )
                )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    __ -> _isLoading.setValue(false),
                    error -> {
                        _isLoading.setValue(false);
                    }
                )
        );
    }
    
    public void onSearchQueryChanged(String query) {
        if (query.trim().isEmpty()) {
            clearResults();
            return;
        }
        searchSubject.onNext(query.trim());
    }
    
    private void clearResults() {
        _artists.setValue(new ArrayList<>());
        _songs.setValue(new ArrayList<>());
        _albums.setValue(new ArrayList<>());
        _playlists.setValue(new ArrayList<>());
        _isEmpty.setValue(false);
    }
    
    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.clear();
    }
}
