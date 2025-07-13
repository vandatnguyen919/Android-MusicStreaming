package com.prm.profile;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.prm.domain.model.Song;
import com.prm.profile.utils.NotificationHelper;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AddSongBottomSheetFragment extends BottomSheetDialogFragment {

    private static final String TAG = "AddSongBottomSheet";
    
    private AddSongViewModel viewModel;
    
    
    // UI Components
    private EditText etSongTitle;
    private EditText etArtistId;
    private EditText etAlbumId;
    private EditText etDuration;
    private EditText etSongUrl;
    private EditText etImageUrl;
    private Button btnAddSong;
    private Button btnCancel;
    private ProgressBar progressBar;

    public static AddSongBottomSheetFragment newInstance() {
        return new AddSongBottomSheetFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_add_song, container, false);
        
        initViews(view);
        setupClickListeners();
        
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(AddSongViewModel.class);
        
        observeViewModel();
    }

    private void initViews(View view) {
        etSongTitle = view.findViewById(R.id.et_song_title);
        etArtistId = view.findViewById(R.id.et_artist_id);
        etAlbumId = view.findViewById(R.id.et_album_id);
        etDuration = view.findViewById(R.id.et_duration);
        etSongUrl = view.findViewById(R.id.et_song_url);
        etImageUrl = view.findViewById(R.id.et_image_url);
        btnAddSong = view.findViewById(R.id.btn_add_song);
        btnCancel = view.findViewById(R.id.btn_cancel);
        progressBar = view.findViewById(R.id.progress_bar);
    }

    private void setupClickListeners() {
        btnAddSong.setOnClickListener(v -> addSong());
        btnCancel.setOnClickListener(v -> dismiss());
    }

    private void observeViewModel() {
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            btnAddSong.setEnabled(!isLoading);
        });

        viewModel.getAddSongResult().observe(getViewLifecycleOwner(), result -> {
            android.util.Log.d("AddSongBottomSheet", "Add song result received: success=" + result.isSuccess());
            if (result.isSuccess()) {
                android.util.Log.d("AddSongBottomSheet", "Showing success toast");
                String artistId = etArtistId.getText().toString().trim();
                NotificationHelper.showSongAddedNotification(requireContext(), artistId);
                Toast.makeText(getContext(), "Song added successfully!", Toast.LENGTH_SHORT).show();
                dismiss();
            } else {
                String errorMessage = result.getErrorMessage();
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();

                // If authentication error, show retry option
                if (errorMessage.contains("Authentication required") || errorMessage.contains("Permission denied")) {
                    showAuthenticationDialog();
                }
            }
        });
    }

    private void addSong() {
        // Get input values
        String title = etSongTitle.getText().toString().trim();
        String artistId = etArtistId.getText().toString().trim();
        String albumId = etAlbumId.getText().toString().trim();
        String durationStr = etDuration.getText().toString().trim();
        String url = etSongUrl.getText().toString().trim();
        String imageUrl = etImageUrl.getText().toString().trim();

        // Validate inputs
        if (TextUtils.isEmpty(title)) {
            etSongTitle.setError("Title is required");
            return;
        }
        if (TextUtils.isEmpty(artistId)) {
            etArtistId.setError("Artist ID is required");
            return;
        }
        if (TextUtils.isEmpty(albumId)) {
            etAlbumId.setError("Album ID is required");
            return;
        }
        if (TextUtils.isEmpty(durationStr)) {
            etDuration.setError("Duration is required");
            return;
        }
        if (TextUtils.isEmpty(url)) {
            etSongUrl.setError("Song URL is required");
            return;
        }

        int duration;
        try {
            duration = Integer.parseInt(durationStr);
        } catch (NumberFormatException e) {
            etDuration.setError("Duration must be a number (in seconds)");
            return;
        }

        // Create song object (imageUrl is optional, can be null or empty)
        Song song = new Song(title, artistId, albumId, duration, url,
                           TextUtils.isEmpty(imageUrl) ? null : imageUrl);

        // Add song via ViewModel
        viewModel.addSong(song);
    }

    private void showAuthenticationDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Authentication Required")
                .setMessage("You need to be authenticated to add songs. Would you like to retry authentication?")
                .setPositiveButton("Retry", (dialog, which) -> {
                    viewModel.retryAuthentication();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    

}
