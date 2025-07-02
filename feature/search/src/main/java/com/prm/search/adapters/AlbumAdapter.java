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
import com.prm.domain.model.Album;
import com.prm.search.R;

public class AlbumAdapter extends ListAdapter<Album, AlbumAdapter.AlbumViewHolder> {

    private final OnAlbumClickListener listener;

    public interface OnAlbumClickListener {
        void onAlbumClick(Album album);
    }

    public AlbumAdapter(OnAlbumClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<Album> DIFF_CALLBACK = new DiffUtil.ItemCallback<Album>() {
        @Override
        public boolean areItemsTheSame(@NonNull Album oldItem, @NonNull Album newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Album oldItem, @NonNull Album newItem) {
            return oldItem.getName().equals(newItem.getName()) 
                    && oldItem.getArtistId().equals(newItem.getArtistId())
                    && oldItem.getCoverImageUrl().equals(newItem.getCoverImageUrl());
        }
    };

    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_album, parent, false);
        return new AlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumViewHolder holder, int position) {
        Album album = getItem(position);
        holder.bind(album, listener);
    }

    static class AlbumViewHolder extends RecyclerView.ViewHolder {
        private final ImageView albumImage;
        private final TextView albumName;
        private final TextView albumArtist;

        public AlbumViewHolder(@NonNull View itemView) {
            super(itemView);
            albumImage = itemView.findViewById(R.id.iv_album);
            albumName = itemView.findViewById(R.id.tv_album_name);
            albumArtist = itemView.findViewById(R.id.tv_album_artist);
        }

        public void bind(Album album, OnAlbumClickListener listener) {
            albumName.setText(album.getName());

            albumArtist.setText("Album");

            Glide.with(itemView.getContext())
                    .load(album.getCoverImageUrl())
                    .placeholder(R.drawable.artist_placeholder)
                    .error(R.drawable.artist_placeholder)
                    .into(albumImage);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAlbumClick(album);
                }
            });
        }
    }
}
