package com.prm.library;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prm.domain.model.Playlist;

import java.util.ArrayList;
import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {

    private List<Playlist> playlists = new ArrayList<>();

    public void setPlaylists(List<Playlist> playlists) {
        this.playlists = playlists;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_playlist, parent, false);
        return new PlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
        Playlist playlist = playlists.get(position);
        holder.bind(playlist);
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    static class PlaylistViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPlaylistCover;
        TextView tvPlaylistTitle;
        TextView tvPlaylistSongCount;

        public PlaylistViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPlaylistCover = itemView.findViewById(R.id.iv_playlist_cover);
            tvPlaylistTitle = itemView.findViewById(R.id.tv_playlist_title);
            tvPlaylistSongCount = itemView.findViewById(R.id.tv_playlist_song_count);
        }

        public void bind(Playlist playlist) {
            tvPlaylistTitle.setText(playlist.getName());
            // You might want to update the cover image based on playlist.getCoverImageUrl() if available
            // For now, using a placeholder
            ivPlaylistCover.setImageResource(com.prm.common.R.drawable.ic_music_note);
            // Update song count (assuming you have a way to get song count from Playlist object)
            int songCount = (playlist.getSongIds() != null) ? playlist.getSongIds().size() : 0;
            tvPlaylistSongCount.setText("Playlist • " + songCount + " songs");
        }
    }
} 