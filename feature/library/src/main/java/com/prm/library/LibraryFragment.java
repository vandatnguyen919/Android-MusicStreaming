package com.prm.library;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.NavController;

import com.prm.common.Navigator;
import com.prm.domain.model.Playlist;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LibraryFragment extends Fragment implements CreatePlaylistDialogFragment.CreatePlaylistDialogListener {

    private static final String TAG = "LibraryFragment";
    private LibraryViewModel mViewModel;
    private RecyclerView playlistsRecyclerView;
    private PlaylistAdapter playlistAdapter;
    private ProgressBar progressBar;

    @Inject
    Navigator navigator;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library, container, false);

        playlistsRecyclerView = view.findViewById(R.id.rv_playlists);
        playlistsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        playlistAdapter = new PlaylistAdapter();
        playlistsRecyclerView.setAdapter(playlistAdapter);
        playlistAdapter.setOnPlaylistActionListener(new PlaylistAdapter.OnPlaylistActionListener() {
            @Override
            public void onDeletePlaylist(Playlist playlist) {
                mViewModel.deletePlaylist(playlist.getId());
            }

            public void onPlaylistClicked(Playlist playlist) {
                navigator.navigateToPlaylistDetail(playlist.getId());
            }
        });

        progressBar = view.findViewById(R.id.progress_bar);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(LibraryViewModel.class);
        // Bind NavController to navigator
        NavController navController = NavHostFragment.findNavController(this);
        navigator.bind(navController);

        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(com.prm.common.R.menu.add_menu, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();

                if (id == com.prm.common.R.id.action_add) {
                    CreatePlaylistDialogFragment dialog = new CreatePlaylistDialogFragment();
                    dialog.setCreatePlaylistDialogListener(LibraryFragment.this);
                    dialog.show(getParentFragmentManager(), "CreatePlaylistDialog");
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);

        // Observe playlists data
        mViewModel.getPlaylists().observe(getViewLifecycleOwner(), playlists -> {
            // Log each playlist from the repository
            Log.d(TAG, "Playlists loaded, count: " + playlists.size());
            for (Playlist playlist : playlists) {
                Log.d(TAG, "Playlist: " + playlist.getName() + " - ID: " + playlist.getId());
            }
            playlistAdapter.setPlaylists(playlists);
        });

        // Observe loading state
        mViewModel.isLoading.observe(getViewLifecycleOwner(), isLoading -> {
            if (progressBar != null) {
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
            Log.d(TAG, "Loading: " + isLoading);
        });

        // Observe error messages
        mViewModel.error.observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
                Log.e(TAG, "Error: " + error);
            }
        });

        // Trigger initial playlist load if needed
        mViewModel.fetchPlaylistsForUser();
    }

    @Override
    public void onPlaylistNameEntered(String playlistName) {
        mViewModel.createPlaylist(playlistName);
    }
}