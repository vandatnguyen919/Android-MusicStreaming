package com.prm.album;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.prm.domain.model.Playlist;
import com.prm.domain.repository.PlaylistRepository;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@AndroidEntryPoint
public class PlaylistDialogFragment extends DialogFragment {

    @Inject
    PlaylistRepository playlistRepository;

    private final CompositeDisposable disposables = new CompositeDisposable();
    private String songId;

    public PlaylistDialogFragment(String songId) {
        this.songId = songId;
    }

    @Override
public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
    // Show a loading dialog first
    AlertDialog loadingDialog = new AlertDialog.Builder(requireContext())
        .setTitle("Select Playlist")
        .setMessage("Loading playlists...")
        .create();

    disposables.add(playlistRepository.getCurrentUserPlaylists(10)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(playlists -> {
            loadingDialog.dismiss();
            if (playlists != null && !playlists.isEmpty()) {
                String[] playlistNames = playlists.stream()
                        .map(Playlist::getName)
                        .toArray(String[]::new);

                new AlertDialog.Builder(requireContext())
                        .setTitle("Select Playlist")
                        .setItems(playlistNames, (dialog, which) -> {
                            Playlist selectedPlaylist = playlists.get(which);
                            addSongToPlaylist(selectedPlaylist.getId());
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            } else {
                Toast.makeText(requireContext(), "No playlists available", Toast.LENGTH_SHORT).show();
            }
        }, throwable -> {
            loadingDialog.dismiss();
            Toast.makeText(requireContext(), "Failed to load playlists", Toast.LENGTH_SHORT).show();
        }));

    return loadingDialog;
}

    private void addSongToPlaylist(String playlistId) {
        disposables.add(playlistRepository.addSongToPlaylist(playlistId, songId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    Toast.makeText(requireContext(), "Added to playlist successfully!", Toast.LENGTH_SHORT).show();
                }, throwable -> {
                    Toast.makeText(requireContext(), "Failed to add song to playlist", Toast.LENGTH_SHORT).show();
                }));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposables.clear();
    }
}