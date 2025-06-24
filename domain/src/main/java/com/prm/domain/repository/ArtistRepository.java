package com.prm.domain.repository;

import com.prm.domain.model.Artist;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

public interface ArtistRepository {
    // Single emissions (one-time data fetch)
    Single<List<Artist>> getAllArtists();
    Single<Artist> getArtistById(String artistId);
    Single<String> addArtist(Artist artist);
    Single<List<Artist>> searchArtistsByName(String searchTerm);
    Single<List<Artist>> getArtistsWithPagination(int limit, Artist lastArtist);

    // Observable emissions (real-time updates)
    Observable<List<Artist>> getAllArtistsObservable();
    Observable<Artist> getArtistByIdObservable(String artistId);

    // Completable emissions (operations without return values)
    Completable updateArtist(Artist artist);
    Completable deleteArtist(String artistId);
}
