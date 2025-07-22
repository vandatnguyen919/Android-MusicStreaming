package com.prm.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.prm.domain.model.Song;
import com.prm.domain.repository.SongRepository;
import com.prm.profile.utils.Result;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class AdminDashboardViewModel extends ViewModel {

    private final SongRepository songRepository;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<List<Song>> pendingSongs = new MutableLiveData<>();
    private final MutableLiveData<Integer> pendingCount = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> approvedCount = new MutableLiveData<>(0);
    private final MutableLiveData<Result<String>> actionResult = new MutableLiveData<>();

    @Inject
    public AdminDashboardViewModel(SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<List<Song>> getPendingSongs() {
        return pendingSongs;
    }

    public LiveData<Integer> getPendingCount() {
        return pendingCount;
    }

    public LiveData<Integer> getApprovedCount() {
        return approvedCount;
    }

    public LiveData<Result<String>> getActionResult() {
        return actionResult;
    }

    public void loadPendingSongs() {
        isLoading.setValue(true);
        compositeDisposable.add(
                songRepository.getPendingSongs()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                songs -> {
                                    pendingSongs.setValue(songs);
                                    isLoading.setValue(false);
                                },
                                throwable -> {
                                    isLoading.setValue(false);
                                    actionResult.setValue(Result.error("Failed to load pending songs: " + throwable.getMessage()));
                                }
                        )
        );
    }

    public void loadStats() {
        // Load pending count
        compositeDisposable.add(
                songRepository.getPendingSongs()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                songs -> pendingCount.setValue(songs.size()),
                                throwable -> pendingCount.setValue(0)
                        )
        );

        // Load approved count
        compositeDisposable.add(
                songRepository.getApprovedSongs()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                songs -> approvedCount.setValue(songs.size()),
                                throwable -> approvedCount.setValue(0)
                        )
        );
    }

    public void approveSong(String songId) {
        compositeDisposable.add(
                songRepository.approveSong(songId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> actionResult.setValue(Result.success("Song approved successfully")),
                                throwable -> actionResult.setValue(Result.error("Failed to approve song: " + throwable.getMessage()))
                        )
        );
    }

    public void denySong(String songId) {
        compositeDisposable.add(
                songRepository.denySong(songId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> actionResult.setValue(Result.success("Song denied and removed")),
                                throwable -> actionResult.setValue(Result.error("Failed to deny song: " + throwable.getMessage()))
                        )
        );
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}
