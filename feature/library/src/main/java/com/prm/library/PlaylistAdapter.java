package com.prm.library;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ImageButton;

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
        holder.btnDeletePlaylist.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onDeletePlaylist(playlist);
            }
        });
        holder.itemView.setOnClickListener(v -> {
            if (actionListener != null && actionListener instanceof OnPlaylistActionListener) {
                ((OnPlaylistActionListener) actionListener).onPlaylistClicked(playlist);
            }
        });
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    static class PlaylistViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPlaylistCover;
        TextView tvPlaylistTitle;
        TextView tvPlaylistSongCount;
        ImageButton btnDeletePlaylist;

        public PlaylistViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPlaylistCover = itemView.findViewById(R.id.iv_playlist_cover);
            tvPlaylistTitle = itemView.findViewById(R.id.tv_playlist_title);
            tvPlaylistSongCount = itemView.findViewById(R.id.tv_playlist_song_count);
            btnDeletePlaylist = itemView.findViewById(R.id.btnDeletePlaylist);
        }

        public void bind(Playlist playlist) {
            tvPlaylistTitle.setText(playlist.getName());
            ivPlaylistCover.setImageResource(com.prm.common.R.drawable.ic_music_note);
            // Update song count (assuming you have a way to get song count from Playlist object)
            int songCount = (playlist.getSongIds() != null) ? playlist.getSongIds().size() : 0;
            tvPlaylistSongCount.setText("Playlist • " + songCount + " songs");
        }
    }

    public interface OnPlaylistActionListener {
        void onDeletePlaylist(Playlist playlist);
        void onPlaylistClicked(Playlist playlist);
    }

    private OnPlaylistActionListener actionListener;

    public void setOnPlaylistActionListener(OnPlaylistActionListener listener) {
        this.actionListener = listener;
    }    
} 