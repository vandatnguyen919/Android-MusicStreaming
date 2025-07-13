package com.prm.data.source.remote;

import com.google.firebase.firestore.FirebaseFirestore;
import com.prm.domain.model.User;
import com.prm.domain.util.Constants;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Singleton
public class FirebaseUserService {

    private static final String COLLECTION_NAME = Constants.Firebase.COLLECTION_USERS;
    private final FirebaseFirestore db;

    @Inject
    public FirebaseUserService() {
        this.db = FirebaseFirestore.getInstance();
    }

    public Single<User> getUserById(String userId) {
        return Single.create(emitter -> {
            db.collection(COLLECTION_NAME).document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            emitter.onSuccess(user);
                        } else {
                            emitter.onError(new Exception("User object is null after conversion."));
                        }
                    } else {
                        emitter.onError(new Exception("User not found with ID: " + userId));
                    }
                })
                .addOnFailureListener(e -> emitter.onError(e));
        });
    }

    public Completable updateUser(User user) {
        return Completable.create(emitter -> {
            if (user.getId() == null || user.getId().isEmpty()) {
                emitter.onError(new IllegalArgumentException("User ID cannot be null or empty for update."));
                return;
            }
            db.collection(COLLECTION_NAME).document(user.getId()).set(user)
                .addOnSuccessListener(aVoid -> emitter.onComplete())
                .addOnFailureListener(e -> emitter.onError(e));
        });
    }

    public Completable createUser(User user) {
        return Completable.create(emitter -> {
            if (user.getId() == null || user.getId().isEmpty()) {
                emitter.onError(new IllegalArgumentException("User ID cannot be null or empty for create."));
                return;
            }
            db.collection(COLLECTION_NAME).document(user.getId()).set(user)
                .addOnSuccessListener(aVoid -> emitter.onComplete())
                .addOnFailureListener(e -> emitter.onError(e));
        });
    }
} 