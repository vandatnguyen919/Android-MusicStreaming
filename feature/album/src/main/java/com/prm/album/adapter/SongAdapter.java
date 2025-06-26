package com.prm.album.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prm.album.R;
import com.prm.album.model.SongUiModel;

import java.util.ArrayList;
import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {
    
    private List<SongUiModel> songs = new ArrayList<>();
    private OnSongClickListener listener;
    
    public interface OnSongClickListener {
        void onSongClick(SongUiModel song);
        void onSongMoreClick(SongUiModel song);
    }
    
    public void setSongs(List<SongUiModel> songs) {
        this.songs = songs != null ? songs : new ArrayList<>();
        notifyDataSetChanged();
    }
    
    public void setOnSongClickListener(OnSongClickListener listener) {
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_song, parent, false);
        return new SongViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        SongUiModel song = songs.get(position);
        holder.bind(song);
    }
    
    @Override
    public int getItemCount() {
        return songs.size();
    }
    
    class SongViewHolder extends RecyclerView.ViewHolder {
        
        private final ImageView ivPlayIndicator;
        private final TextView tvSongTitle;
        private final TextView tvSongArtist;
        private final TextView tvSongDuration;
        private final ImageButton btnSongMore;
        
        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            
            ivPlayIndicator = itemView.findViewById(R.id.iv_play_indicator);
            tvSongTitle = itemView.findViewById(R.id.tv_song_title);
            tvSongArtist = itemView.findViewById(R.id.tv_song_artist);
            tvSongDuration = itemView.findViewById(R.id.tv_song_duration);
            btnSongMore = itemView.findViewById(R.id.btn_song_more);
        }
        
        public void bind(SongUiModel song) {
            tvSongTitle.setText(song.getTitle());
            tvSongArtist.setText(song.getArtistName());
            tvSongDuration.setText(song.getFormattedDuration());
            
            // Show/hide play indicator based on playing state
            if (song.isPlaying()) {
                ivPlayIndicator.setVisibility(View.VISIBLE);
                ivPlayIndicator.setImageResource(R.drawable.ic_pause);
            } else {
                ivPlayIndicator.setVisibility(View.GONE);
            }
            
            // Set click listeners
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onSongClick(song);
                }
            });
            
            btnSongMore.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onSongMoreClick(song);
                }
            });
        }
    }
}
