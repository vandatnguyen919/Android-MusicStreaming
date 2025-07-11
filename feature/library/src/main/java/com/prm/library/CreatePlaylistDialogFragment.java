package com.prm.library;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class CreatePlaylistDialogFragment extends DialogFragment {

    public interface CreatePlaylistDialogListener {
        void onPlaylistNameEntered(String playlistName);
    }

    private CreatePlaylistDialogListener listener;

    public void setCreatePlaylistDialogListener(CreatePlaylistDialogListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_create_playlist, null);

        EditText playlistNameEditText = view.findViewById(R.id.playlist_name_edit_text);
        Button createButton = view.findViewById(R.id.create_button);

        createButton.setOnClickListener(v -> {
            String playlistName = playlistNameEditText.getText().toString().trim();
            if (listener != null && !playlistName.isEmpty()) {
                listener.onPlaylistNameEntered(playlistName);
                dismiss();
            }
        });

        builder.setView(view);
        return builder.create();
    }
} 