package com.prm.data.source.remote;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.prm.domain.model.Artist;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.Completable;
import java.util.ArrayList;
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


    public Single<String> addArtist(Artist artist) {
        return Single.create(emitter -> {
            artistsCollection.add(artist)
                    .addOnSuccessListener(documentReference -> {
                        emitter.onSuccess(documentReference.getId());
                    })
                    .addOnFailureListener(emitter::onError);
        });
    }


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


    public Completable deleteArtist(String artistId) {
        return Completable.create(emitter -> {
            artistsCollection.document(artistId).delete()
                    .addOnSuccessListener(aVoid -> emitter.onComplete())
                    .addOnFailureListener(emitter::onError);
        });
    }


    public Single<List<Artist>> searchArtistsByName(String searchTerm) {
        return Single.create(emitter -> {
            // Thực hiện tìm kiếm với contains logic
            String lowercaseQuery = searchTerm.toLowerCase();
            artistsCollection
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        List<Artist> artists = new ArrayList<>();
                        for (var doc : queryDocumentSnapshots.getDocuments()) {
                            Artist artist = doc.toObject(Artist.class);
                            // Kiểm tra xem tên nghệ sĩ có chứa chuỗi tìm kiếm không (contains search)
                            if (artist != null && artist.getName() != null && 
                                artist.getName().toLowerCase().contains(lowercaseQuery)) {
                                artists.add(artist);
                            }
                        }
                        // Giới hạn kết quả trả về nếu cần
                        if (artists.size() > 20) {
                            artists = artists.subList(0, 20);
                        }
                        emitter.onSuccess(artists);
                    })
                    .addOnFailureListener(emitter::onError);
        });
    }


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
