package com.prm.data.repository;

import com.prm.data.source.remote.FirebaseArtistService;
import com.prm.domain.model.Artist;
import com.prm.domain.repository.ArtistRepository;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

@Singleton
public class ArtistRepositoryImpl implements ArtistRepository {

    private final FirebaseArtistService firebaseArtistService;

    @Inject
    public ArtistRepositoryImpl(FirebaseArtistService firebaseArtistService) {
        this.firebaseArtistService = firebaseArtistService;
    }

    @Override
    public Single<List<Artist>> getAllArtists() {
        return firebaseArtistService.getAllArtists();
    }

    @Override
    public Single<Artist> getArtistById(String artistId) {
        return firebaseArtistService.getArtistById(artistId);
    }

    @Override
    public Single<String> addArtist(Artist artist) {
        return firebaseArtistService.addArtist(artist);
    }

    @Override
    public Single<List<Artist>> searchArtistsByName(String searchTerm) {
        return firebaseArtistService.searchArtistsByName(searchTerm);
    }

    @Override
    public Single<List<Artist>> getArtistsWithPagination(int limit, Artist lastArtist) {
        return firebaseArtistService.getArtistsWithPagination(limit, lastArtist);
    }

    @Override
    public Observable<List<Artist>> getAllArtistsObservable() {
        return firebaseArtistService.getAllArtistsObservable();
    }

    @Override
    public Observable<Artist> getArtistByIdObservable(String artistId) {
        return firebaseArtistService.getArtistByIdObservable(artistId);
    }

    @Override
    public Completable updateArtist(Artist artist) {
        return firebaseArtistService.updateArtist(artist);
    }

    @Override
    public Completable deleteArtist(String artistId) {
        return firebaseArtistService.deleteArtist(artistId);
    }
}
