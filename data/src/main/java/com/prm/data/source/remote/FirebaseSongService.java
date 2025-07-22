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


    public Single<List<Song>> getAllSongs() {
        return Single.create(emitter -> {
            // For regular users, only return approved songs
            songsCollection
                    .whereEqualTo("is_approved", true)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        List<Song> songs = queryDocumentSnapshots.toObjects(Song.class);
                        emitter.onSuccess(songs);
                    })
                    .addOnFailureListener(emitter::onError);
        });
    }


    public Observable<List<Song>> getAllSongsObservable() {
        return Observable.create(emitter -> {
            // For regular users, only return approved songs
            songsCollection
                    .whereEqualTo("is_approved", true)
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


    public Single<String> addSong(Song song) {
        return Single.create(emitter -> {
            songsCollection.add(song)
                    .addOnSuccessListener(documentReference -> {
                        emitter.onSuccess(documentReference.getId());
                    })
                    .addOnFailureListener(emitter::onError);
        });
    }


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


    public Completable deleteSong(String songId) {
        return Completable.create(emitter -> {
            songsCollection.document(songId).delete()
                    .addOnSuccessListener(aVoid -> emitter.onComplete())
                    .addOnFailureListener(emitter::onError);
        });
    }

    public Completable addNewSong(Song song) {
        return Completable.create(emitter -> {
            songsCollection.document(song.getId())
                    .set(song)
                    .addOnSuccessListener(aVoid -> emitter.onComplete())
                    .addOnFailureListener(emitter::onError);
        });
    }

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

    public Single<List<Song>> getSongsByIds(List<String> songIds) {
        return Single.create(emitter -> {
            if (songIds == null || songIds.isEmpty()) {
                emitter.onSuccess(new java.util.ArrayList<>());
                return;
            }
            List<Song> result = new java.util.ArrayList<>();
            List<com.google.android.gms.tasks.Task<com.google.firebase.firestore.DocumentSnapshot>> tasks = new java.util.ArrayList<>();
            for (String id : songIds) {
                tasks.add(songsCollection.document(id).get());
            }
            com.google.android.gms.tasks.Tasks.whenAllSuccess(tasks)
                    .addOnSuccessListener(objects -> {
                        for (Object obj : objects) {
                            com.google.firebase.firestore.DocumentSnapshot doc = (com.google.firebase.firestore.DocumentSnapshot) obj;
                            if (doc.exists()) {
                                Song song = doc.toObject(Song.class);
                                if (song != null) {
                                    song.setId(doc.getId()); // set document id
                                    result.add(song);
                                }
                            }
                        }
                        emitter.onSuccess(result);
                    })
                    .addOnFailureListener(emitter::onError);
        });
    }

    // Admin functionality methods
    public Single<List<Song>> getPendingSongs() {
        return Single.create(emitter -> {
            songsCollection
                    .whereEqualTo("is_approved", false)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        List<Song> songs = queryDocumentSnapshots.toObjects(Song.class);
                        emitter.onSuccess(songs);
                    })
                    .addOnFailureListener(emitter::onError);
        });
    }

    public Single<List<Song>> getApprovedSongs() {
        return Single.create(emitter -> {
            songsCollection
                    .whereEqualTo("is_approved", true)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        List<Song> songs = queryDocumentSnapshots.toObjects(Song.class);
                        emitter.onSuccess(songs);
                    })
                    .addOnFailureListener(emitter::onError);
        });
    }

    public Observable<List<Song>> getPendingSongsObservable() {
        return Observable.create(emitter -> {
            songsCollection
                    .whereEqualTo("is_approved", false)
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

    public Completable approveSong(String songId) {
        return Completable.create(emitter -> {
            songsCollection
                    .document(songId)
                    .update("is_approved", true)
                    .addOnSuccessListener(aVoid -> emitter.onComplete())
                    .addOnFailureListener(emitter::onError);
        });
    }

    public Completable denySong(String songId) {
        return Completable.create(emitter -> {
            songsCollection
                    .document(songId)
                    .delete()
                    .addOnSuccessListener(aVoid -> emitter.onComplete())
                    .addOnFailureListener(emitter::onError);
        });
    }
}