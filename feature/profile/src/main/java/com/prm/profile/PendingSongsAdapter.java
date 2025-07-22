package com.prm.profile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.prm.domain.model.Song;

import java.util.List;

public class PendingSongsAdapter extends RecyclerView.Adapter<PendingSongsAdapter.PendingSongViewHolder> {

    private List<Song> songs;
    private OnSongActionListener listener;

    public interface OnSongActionListener {
        void onApprove(Song song);
        void onDeny(Song song);
    }

    public PendingSongsAdapter(List<Song> songs, OnSongActionListener listener) {
        this.songs = songs;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PendingSongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pending_song, parent, false);
        return new PendingSongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PendingSongViewHolder holder, int position) {
        Song song = songs.get(position);
        holder.bind(song, listener);
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public void updateSongs(List<Song> newSongs) {
        this.songs = newSongs;
        notifyDataSetChanged();
    }

    static class PendingSongViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivSongImage;
        private TextView tvSongTitle;
        private TextView tvArtistId;
        private TextView tvDuration;
        private Button btnApprove;
        private Button btnDeny;

        public PendingSongViewHolder(@NonNull View itemView) {
            super(itemView);
            ivSongImage = itemView.findViewById(R.id.iv_song_image);
            tvSongTitle = itemView.findViewById(R.id.tv_song_title);
            tvArtistId = itemView.findViewById(R.id.tv_artist_id);
            tvDuration = itemView.findViewById(R.id.tv_duration);
            btnApprove = itemView.findViewById(R.id.btn_approve);
            btnDeny = itemView.findViewById(R.id.btn_deny);
        }

        public void bind(Song song, OnSongActionListener listener) {
            tvSongTitle.setText(song.getTitle());
            tvArtistId.setText("Artist: " + song.getArtistId());
            tvDuration.setText(formatDuration(song.getDuration()));

            // Load image using Glide
            if (song.getImageUrl() != null && !song.getImageUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(song.getImageUrl())
                        .placeholder(R.drawable.ic_music_note)
                        .error(R.drawable.ic_music_note)
                        .into(ivSongImage);
            } else {
                ivSongImage.setImageResource(R.drawable.ic_music_note);
            }

            btnApprove.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onApprove(song);
                }
            });

            btnDeny.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeny(song);
                }
            });
        }

        private String formatDuration(int durationInSeconds) {
            int minutes = durationInSeconds / 60;
            int seconds = durationInSeconds % 60;
            return String.format("%d:%02d", minutes, seconds);
        }
    }
}
