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
import com.prm.domain.model.Artist;
import com.prm.search.R;

public class ArtistAdapter extends ListAdapter<Artist, ArtistAdapter.ArtistViewHolder> {

    private final OnArtistClickListener listener;

    public interface OnArtistClickListener {
        void onArtistClick(Artist artist);
    }

    public ArtistAdapter(OnArtistClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<Artist> DIFF_CALLBACK = new DiffUtil.ItemCallback<Artist>() {
        @Override
        public boolean areItemsTheSame(@NonNull Artist oldItem, @NonNull Artist newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Artist oldItem, @NonNull Artist newItem) {
            return oldItem.getName().equals(newItem.getName()) &&
                    oldItem.getProfileImageUrl().equals(newItem.getProfileImageUrl());
        }
    };

    @NonNull
    @Override
    public ArtistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_artist, parent, false);
        return new ArtistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistViewHolder holder, int position) {
        Artist artist = getItem(position);
        holder.bind(artist, listener);
    }

    static class ArtistViewHolder extends RecyclerView.ViewHolder {
        private final ImageView artistImage;
        private final TextView artistName;
        private final TextView artistType;

        public ArtistViewHolder(@NonNull View itemView) {
            super(itemView);
            artistImage = itemView.findViewById(R.id.iv_artist);
            artistName = itemView.findViewById(R.id.tv_artist_name);
            artistType = itemView.findViewById(R.id.tv_artist_type);
        }

        public void bind(Artist artist, OnArtistClickListener listener) {
            artistName.setText(artist.getName());
            artistType.setText("Artist");

            // Sử dụng Glide để load ảnh
            if (artist.getProfileImageUrl() != null && !artist.getProfileImageUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(artist.getProfileImageUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .placeholder(R.drawable.artist_placeholder)
                        .error(R.drawable.artist_placeholder)
                        .into(artistImage);
            } else {
                artistImage.setImageResource(R.drawable.artist_placeholder);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onArtistClick(artist);
                }
            });
        }
    }
}
