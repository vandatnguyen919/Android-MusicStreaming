package com.prm.library;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prm.domain.model.Song;
import com.prm.library.R;

import java.util.ArrayList;
import java.util.List;

public class SongInPlaylistAdapter extends RecyclerView.Adapter<SongInPlaylistAdapter.SongViewHolder> {
    public interface OnSongActionListener {
        void onRemoveSong(Song song);
        void onSongClicked(Song song);
    }

    private List<Song> songs = new ArrayList<>();
    private OnSongActionListener actionListener;

    public void setSongs(List<Song> songs) {
        this.songs = songs;
        notifyDataSetChanged();
    }

    public void setOnSongActionListener(OnSongActionListener listener) {
        this.actionListener = listener;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_song_in_playlist, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song song = songs.get(position);
        holder.tvSongTitle.setText(song.getTitle());
        holder.tvSongArtist.setText(song.getArtist());
        holder.btnRemoveSong.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onRemoveSong(song);
            }
        });
        holder.itemView.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onSongClicked(song);
            }
        });
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    static class SongViewHolder extends RecyclerView.ViewHolder {
        TextView tvSongTitle, tvSongArtist;
        ImageButton btnRemoveSong;
        SongViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSongTitle = itemView.findViewById(R.id.tvSongTitle);
            tvSongArtist = itemView.findViewById(R.id.tvSongArtist);
            btnRemoveSong = itemView.findViewById(R.id.btnRemoveSong);
        }
    }
} 