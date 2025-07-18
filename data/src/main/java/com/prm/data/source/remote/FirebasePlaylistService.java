package com.prm.data.source.remote;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.prm.domain.model.Playlist;
import com.prm.domain.util.Constants;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;

@Singleton
public class FirebasePlaylistService {

    private static final String COLLECTION_NAME = Constants.Firebase.COLLECTION_PLAYLISTS;
    private final FirebaseFirestore db;

    @Inject
    public FirebasePlaylistService(FirebaseFirestore db) {
        this.db = db;
    }

    public Completable createPlaylist(Playlist playlist) {
        return Completable.create(emitter -> {
            String newId = db.collection(COLLECTION_NAME).document().getId();
            playlist.setId(newId);
            db.collection(COLLECTION_NAME).document(newId).set(playlist)
                    .addOnSuccessListener(aVoid -> emitter.onComplete())
                    .addOnFailureListener(emitter::onError);
        });
    }

    public Observable<List<Playlist>> getPlaylistsForUser(String userId) {
        return Observable.create(emitter -> {
            db.collection(COLLECTION_NAME)
                    .whereEqualTo("userId", userId)
                    .addSnapshotListener((value, error) -> {
                        if (error != null) {
                            emitter.onError(error);
                            return;
                        }
                        if (value != null) {
                            List<Playlist> playlists = new ArrayList<>();
                            for (QueryDocumentSnapshot document : value) {
                                Playlist playlist = document.toObject(Playlist.class);
                                playlists.add(playlist);
                            }
                            emitter.onNext(playlists);
                        }
                    });
        });
    }

    public Observable<List<Playlist>> getPlaylistsForUser(String userId, int limit) {
        return Observable.create(emitter -> {
            db.collection(COLLECTION_NAME)
                    .whereEqualTo("userId", userId)
                    .limit(limit) // Apply the limit
                    .addSnapshotListener((value, error) -> {
                        if (error != null) {
                            emitter.onError(error);
                            return;
                        }
                        if (value != null) {
                            List<Playlist> playlists = new ArrayList<>();
                            for (QueryDocumentSnapshot document : value) {
                                Playlist playlist = document.toObject(Playlist.class);
                                playlists.add(playlist);
                            }
                            emitter.onNext(playlists);
                        }
                    });
        });
    }

    public Completable updatePlaylist(Playlist playlist) {
        return Completable.create(emitter -> {
            if (playlist.getId() == null || playlist.getId().isEmpty()) {
                emitter.onError(new IllegalArgumentException("Playlist ID cannot be null or empty for update."));
                return;
            }
            db.collection(COLLECTION_NAME).document(playlist.getId()).set(playlist)
                    .addOnSuccessListener(aVoid -> emitter.onComplete())
                    .addOnFailureListener(emitter::onError);
        });
    }

    public Completable deletePlaylist(String playlistId) {
        return Completable.create(emitter -> {
            db.collection(COLLECTION_NAME).document(playlistId).delete()
                    .addOnSuccessListener(aVoid -> emitter.onComplete())
                    .addOnFailureListener(emitter::onError);
        });
    }

    public Completable addSongToPlaylist(String playlistId, String songId) {
        return Completable.create(emitter -> {
            db.collection(COLLECTION_NAME).document(playlistId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Playlist playlist = documentSnapshot.toObject(Playlist.class);
                            if (playlist != null && !playlist.getSongIds().contains(songId)) {
                                playlist.getSongIds().add(songId);
                                db.collection(COLLECTION_NAME).document(playlistId).set(playlist)
                                        .addOnSuccessListener(aVoid -> emitter.onComplete())
                                        .addOnFailureListener(emitter::onError);
                            } else {
                                emitter.onComplete();
                            }
                        } else {
                            emitter.onError(new Exception("Playlist not found with ID: " + playlistId));
                        }
                    })
                    .addOnFailureListener(emitter::onError);
        });
    }

    public Completable removeSongFromPlaylist(String playlistId, String songId) {
        return Completable.create(emitter -> {
            db.collection(COLLECTION_NAME).document(playlistId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Playlist playlist = documentSnapshot.toObject(Playlist.class);
                            if (playlist != null && playlist.getSongIds().contains(songId)) {
                                playlist.getSongIds().remove(songId);
                                db.collection(COLLECTION_NAME).document(playlistId).set(playlist)
                                        .addOnSuccessListener(aVoid -> emitter.onComplete())
                                        .addOnFailureListener(emitter::onError);
                            } else {
                                emitter.onComplete();
                            }
                        } else {
                            emitter.onError(new Exception("Playlist not found with ID: " + playlistId));
                        }
                    })
                    .addOnFailureListener(emitter::onError);
        });
    }
} 