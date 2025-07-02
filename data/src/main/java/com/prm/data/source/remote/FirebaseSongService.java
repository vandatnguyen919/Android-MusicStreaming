package com.prm.data.source.remote;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.prm.domain.model.Song;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

@Singleton
public class FirebaseSongService {
    private static final String COLLECTION_NAME = "songs";
    private final FirebaseFirestore firestore;
    private final CollectionReference songsCollection;

    @Inject
    public FirebaseSongService() {
        this.firestore = FirebaseFirestore.getInstance();
        this.songsCollection = firestore.collection(COLLECTION_NAME);
    }

    /**
     * Retrieve all songs as Single
     */
    public Single<List<Song>> getAllSongs() {
        return Single.create(emitter -> {
            songsCollection.get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        List<Song> songs = queryDocumentSnapshots.toObjects(Song.class);
                        emitter.onSuccess(songs);
                    })
                    .addOnFailureListener(emitter::onError);
        });
    }

    /**
     * Retrieve all songs as Observable (real-time updates)
     */
    public Observable<List<Song>> getAllSongsObservable() {
        return Observable.create(emitter -> {
            songsCollection.addSnapshotListener((queryDocumentSnapshots, error) -> {
                if (error != null) {
                    emitter.onError(error);
                    return;
                }

                if (queryDocumentSnapshots != null) {
                    List<Song> songs = queryDocumentSnapshots.toObjects(Song.class);
                    emitter.onNext(songs);
                }
            });
        });
    }

    /**
     * Retrieve song by ID
     */
    public Single<Song> getSongById(String songId) {
        return Single.create(emitter -> {
            songsCollection.document(songId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Song song = documentSnapshot.toObject(Song.class);
                            emitter.onSuccess(song);
                        } else {
                            emitter.onError(new Exception("Song not found"));
                        }
                    })
                    .addOnFailureListener(emitter::onError);
        });
    }

    /**
     * Retrieve song by ID as Observable (real-time updates)
     */
    public Observable<Song> getSongByIdObservable(String songId) {
        return Observable.create(emitter -> {
            songsCollection.document(songId).addSnapshotListener((documentSnapshot, error) -> {
                if (error != null) {
                    emitter.onError(error);
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    Song song = documentSnapshot.toObject(Song.class);
                    emitter.onNext(song);
                } else {
                    emitter.onError(new Exception("Song not found"));
                }
            });
        });
    }

    /**
     * Retrieve songs by artist ID
     */
    public Single<List<Song>> getSongsByArtistId(String artistId) {
        return Single.create(emitter -> {
            songsCollection.whereEqualTo("artist_id", artistId).get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        List<Song> songs = queryDocumentSnapshots.toObjects(Song.class);
                        emitter.onSuccess(songs);
                    })
                    .addOnFailureListener(emitter::onError);
        });
    }

    /**
     * Retrieve songs by artist ID as Observable (real-time updates)
     */
    public Observable<List<Song>> getSongsByArtistIdObservable(String artistId) {
        return Observable.create(emitter -> {
            songsCollection.whereEqualTo("artist_id", artistId)
                    .addSnapshotListener((queryDocumentSnapshots, error) -> {
                        if (error != null) {
                            emitter.onError(error);
                            return;
                        }

                        if (queryDocumentSnapshots != null) {
                            List<Song> songs = queryDocumentSnapshots.toObjects(Song.class);
                            emitter.onNext(songs);
                        }
                    });
        });
    }

    /**
     * Retrieve songs by album ID
     */
    public Single<List<Song>> getSongsByAlbumId(String albumId) {
        return Single.create(emitter -> {
            songsCollection.whereEqualTo("album_id", albumId).get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        List<Song> songs = queryDocumentSnapshots.toObjects(Song.class);
                        emitter.onSuccess(songs);
                    })
                    .addOnFailureListener(emitter::onError);
        });
    }

    /**
     * Retrieve songs by album ID as Observable (real-time updates)
     */
    public Observable<List<Song>> getSongsByAlbumIdObservable(String albumId) {
        return Observable.create(emitter -> {
            songsCollection.whereEqualTo("album_id", albumId)
                    .addSnapshotListener((queryDocumentSnapshots, error) -> {
                        if (error != null) {
                            emitter.onError(error);
                            return;
                        }

                        if (queryDocumentSnapshots != null) {
                            List<Song> songs = queryDocumentSnapshots.toObjects(Song.class);
                            emitter.onNext(songs);
                        }
                    });
        });
    }

    /**
     * Add a new song
     */
    public Single<String> addSong(Song song) {
        return Single.create(emitter -> {
            songsCollection.add(song)
                    .addOnSuccessListener(documentReference -> {
                        emitter.onSuccess(documentReference.getId());
                    })
                    .addOnFailureListener(emitter::onError);
        });
    }

    /**
     * Update existing song
     */
    public Completable updateSong(Song song) {
        return Completable.create(emitter -> {
            if (song.getId() == null) {
                emitter.onError(new IllegalArgumentException("Song ID cannot be null for update"));
                return;
            }

            songsCollection.document(song.getId()).set(song)
                    .addOnSuccessListener(aVoid -> emitter.onComplete())
                    .addOnFailureListener(emitter::onError);
        });
    }

    /**
     * Delete song by ID
     */
    public Completable deleteSong(String songId) {
        return Completable.create(emitter -> {
            songsCollection.document(songId).delete()
                    .addOnSuccessListener(aVoid -> emitter.onComplete())
                    .addOnFailureListener(emitter::onError);
        });
    }

    /**
     * Search songs by title (case-insensitive partial match)
     */
    public Single<List<Song>> searchSongsByTitle(String searchTerm) {
        return Single.create(emitter -> {
            // Note: Firestore doesn't support case-insensitive queries directly
            // This is a basic implementation - consider using Algolia for advanced search
            songsCollection
                    .orderBy("title")
                    .startAt(searchTerm.toLowerCase())
                    .endAt(searchTerm.toLowerCase() + "\uf8ff")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        List<Song> songs = queryDocumentSnapshots.toObjects(Song.class);
                        emitter.onSuccess(songs);
                    })
                    .addOnFailureListener(emitter::onError);
        });
    }

    /**
     * Get songs with pagination
     */
    public Single<List<Song>> getSongsWithPagination(int limit, Song lastSong) {
        return Single.create(emitter -> {
            Query query = songsCollection.orderBy("title").limit(limit);

            if (lastSong != null) {
                query = query.startAfter(lastSong.getTitle());
            }

            query.get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        List<Song> songs = queryDocumentSnapshots.toObjects(Song.class);
                        emitter.onSuccess(songs);
                    })
                    .addOnFailureListener(emitter::onError);
        });
    }
}