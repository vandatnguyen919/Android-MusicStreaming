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
import com.prm.album.model.RadioSongUiModel;

import java.util.ArrayList;
import java.util.List;

public class RadioSongAdapter extends RecyclerView.Adapter<RadioSongAdapter.RadioSongViewHolder> {

    private List<RadioSongUiModel> radioSongs = new ArrayList<>();
    private OnRadioSongClickListener listener;

    public interface OnRadioSongClickListener {
        void onRadioSongClick(RadioSongUiModel song);
        void onRadioSongMoreClick(RadioSongUiModel song);
    }

    public void setRadioSongs(List<RadioSongUiModel> radioSongs) {
        this.radioSongs = radioSongs;
        notifyDataSetChanged();
    }

    public void setOnRadioSongClickListener(OnRadioSongClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public RadioSongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_radio_song, parent, false);
        return new RadioSongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RadioSongViewHolder holder, int position) {
        RadioSongUiModel song = radioSongs.get(position);
        holder.bind(song);
    }

    @Override
    public int getItemCount() {
        return radioSongs.size();
    }

    class RadioSongViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivPlayIndicator;
        private final TextView tvSongTitle;
        private final TextView tvSongArtist;
        private final ImageButton btnSongMore;

        public RadioSongViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPlayIndicator = itemView.findViewById(R.id.iv_play_indicator);
            tvSongTitle = itemView.findViewById(R.id.tv_song_title);
            tvSongArtist = itemView.findViewById(R.id.tv_song_artist);
            btnSongMore = itemView.findViewById(R.id.btn_song_more);
        }

        public void bind(RadioSongUiModel song) {
            tvSongTitle.setText(song.getTitle());
            tvSongArtist.setText(song.getArtist());

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
                    listener.onRadioSongClick(song);
                }
            });

            btnSongMore.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRadioSongMoreClick(song);
                }
            });
        }
    }
}
