package com.prm.data.source.remote;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.prm.domain.model.Artist;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.Completable;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class FirebaseArtistService {
    private static final String COLLECTION_NAME = "artists";
    private final CollectionReference artistsCollection;

    @Inject
    public FirebaseArtistService() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        this.artistsCollection = firestore.collection(COLLECTION_NAME);
    }

    /**
     * Retrieve all artists as Single
     */
    public Single<List<Artist>> getAllArtists() {
        return Single.create(emitter -> {
            artistsCollection.get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        List<Artist> artists = queryDocumentSnapshots.toObjects(Artist.class);
                        emitter.onSuccess(artists);
                    })
                    .addOnFailureListener(emitter::onError);
        });
    }

    /**
     * Retrieve all artists as Observable (real-time updates)
     */
    public Observable<List<Artist>> getAllArtistsObservable() {
        return Observable.create(emitter -> {
            artistsCollection.addSnapshotListener((queryDocumentSnapshots, error) -> {
                if (error != null) {
                    emitter.onError(error);
                    return;
                }

                if (queryDocumentSnapshots != null) {
                    List<Artist> artists = queryDocumentSnapshots.toObjects(Artist.class);
                    emitter.onNext(artists);
                }
            });
        });
    }

    /**
     * Retrieve artist by ID
     */
    public Single<Artist> getArtistById(String artistId) {
        return Single.create(emitter -> {
            artistsCollection.document(artistId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Artist artist = documentSnapshot.toObject(Artist.class);
                            emitter.onSuccess(artist);
                        } else {
                            emitter.onError(new Exception("Artist not found"));
                        }
                    })
                    .addOnFailureListener(emitter::onError);
        });
    }

    /**
     * Retrieve artist by ID as Observable (real-time updates)
     */
    public Observable<Artist> getArtistByIdObservable(String artistId) {
        return Observable.create(emitter -> {
            artistsCollection.document(artistId).addSnapshotListener((documentSnapshot, error) -> {
                if (error != null) {
                    emitter.onError(error);
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    Artist artist = documentSnapshot.toObject(Artist.class);
                    emitter.onNext(artist);
                } else {
                    emitter.onError(new Exception("Artist not found"));
                }
            });
        });
    }

    /**
     * Add a new artist
     */
    public Single<String> addArtist(Artist artist) {
        return Single.create(emitter -> {
            artistsCollection.add(artist)
                    .addOnSuccessListener(documentReference -> {
                        emitter.onSuccess(documentReference.getId());
                    })
                    .addOnFailureListener(emitter::onError);
        });
    }

    /**
     * Update existing artist
     */
    public Completable updateArtist(Artist artist) {
        return Completable.create(emitter -> {
            if (artist.getId() == null) {
                emitter.onError(new IllegalArgumentException("Artist ID cannot be null for update"));
                return;
            }

            artistsCollection.document(artist.getId()).set(artist)
                    .addOnSuccessListener(aVoid -> emitter.onComplete())
                    .addOnFailureListener(emitter::onError);
        });
    }

    /**
     * Delete artist by ID
     */
    public Completable deleteArtist(String artistId) {
        return Completable.create(emitter -> {
            artistsCollection.document(artistId).delete()
                    .addOnSuccessListener(aVoid -> emitter.onComplete())
                    .addOnFailureListener(emitter::onError);
        });
    }

    /**
     * Search artists by name (case-insensitive partial match)
     */
    public Single<List<Artist>> searchArtistsByName(String searchTerm) {
        return Single.create(emitter -> {
            // Note: Firestore doesn't support case-insensitive queries directly
            artistsCollection
                    .orderBy("name")
                    .startAt(searchTerm.toLowerCase())
                    .endAt(searchTerm.toLowerCase() + "\uf8ff")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        List<Artist> artists = queryDocumentSnapshots.toObjects(Artist.class);
                        emitter.onSuccess(artists);
                    })
                    .addOnFailureListener(emitter::onError);
        });
    }

    /**
     * Get artists with pagination
     */
    public Single<List<Artist>> getArtistsWithPagination(int limit, Artist lastArtist) {
        return Single.create(emitter -> {
            Query query = artistsCollection.orderBy("name").limit(limit);

            if (lastArtist != null) {
                query = query.startAfter(lastArtist.getName());
            }

            query.get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        List<Artist> artists = queryDocumentSnapshots.toObjects(Artist.class);
                        emitter.onSuccess(artists);
                    })
                    .addOnFailureListener(emitter::onError);
        });
    }
}
