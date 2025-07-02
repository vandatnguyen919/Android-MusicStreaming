package com.prm.search.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.prm.domain.model.Song;
import com.prm.search.R;

public class SongAdapter extends ListAdapter<Song, SongAdapter.SongViewHolder> {

    private final OnSongClickListener listener;

    public interface OnSongClickListener {
        void onSongClick(Song song);
        void onSongOptionsClick(Song song, View view);
    }

    public SongAdapter(OnSongClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<Song> DIFF_CALLBACK = new DiffUtil.ItemCallback<Song>() {
        @Override
        public boolean areItemsTheSame(@NonNull Song oldItem, @NonNull Song newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Song oldItem, @NonNull Song newItem) {
            return oldItem.getTitle().equals(newItem.getTitle()) &&
                    oldItem.getArtistId().equals(newItem.getArtistId()) &&
                    ((oldItem.getImageUrl() == null && newItem.getImageUrl() == null) ||
                    (oldItem.getImageUrl() != null && oldItem.getImageUrl().equals(newItem.getImageUrl())));
        }
    };

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_song, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song song = getItem(position);
        holder.bind(song, listener);
    }

    static class SongViewHolder extends RecyclerView.ViewHolder {
        private final ImageView songImage;
        private final TextView songTitle;
        private final TextView songArtist;
        private final ImageView moreOptions;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            songImage = itemView.findViewById(R.id.iv_song);
            songTitle = itemView.findViewById(R.id.tv_song_title);
            songArtist = itemView.findViewById(R.id.tv_song_artist);
            moreOptions = itemView.findViewById(R.id.iv_more_options);
        }

        public void bind(Song song, OnSongClickListener listener) {
            songTitle.setText(song.getTitle());
            songArtist.setText("Song by artist " + song.getArtistId());

            // Sử dụng Glide để load ảnh
            if (song.getImageUrl() != null && !song.getImageUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(song.getImageUrl())
                        .apply(new RequestOptions().centerCrop())
                        .placeholder(R.drawable.artist_placeholder)
                        .error(R.drawable.artist_placeholder)
                        .into(songImage);
            } else {
                songImage.setImageResource(R.drawable.artist_placeholder);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onSongClick(song);
                }
            });

            moreOptions.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onSongOptionsClick(song, v);
                }
            });
        }
    }
}
