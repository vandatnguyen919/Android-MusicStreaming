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
import com.prm.domain.model.Playlist;
import com.prm.search.R;

public class PlaylistAdapter extends ListAdapter<Playlist, PlaylistAdapter.PlaylistViewHolder> {

    private final OnPlaylistClickListener listener;

    public interface OnPlaylistClickListener {
        void onPlaylistClick(Playlist playlist);
    }

    public PlaylistAdapter(OnPlaylistClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<Playlist> DIFF_CALLBACK = new DiffUtil.ItemCallback<Playlist>() {
        @Override
        public boolean areItemsTheSame(@NonNull Playlist oldItem, @NonNull Playlist newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Playlist oldItem, @NonNull Playlist newItem) {
            return oldItem.getName().equals(newItem.getName()) &&
                    oldItem.getUserId().equals(newItem.getUserId()) &&
                    ((oldItem.getCoverImageUrl() == null && newItem.getCoverImageUrl() == null) ||
                    (oldItem.getCoverImageUrl() != null && oldItem.getCoverImageUrl().equals(newItem.getCoverImageUrl())));
        }
    };

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_playlist, parent, false);
        return new PlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
        Playlist playlist = getItem(position);
        holder.bind(playlist, listener);
    }

    static class PlaylistViewHolder extends RecyclerView.ViewHolder {
        private final ImageView playlistImage;
        private final TextView playlistName;
        private final TextView playlistInfo;

        public PlaylistViewHolder(@NonNull View itemView) {
            super(itemView);
            playlistImage = itemView.findViewById(R.id.iv_playlist);
            playlistName = itemView.findViewById(R.id.tv_playlist_name);
            playlistInfo = itemView.findViewById(R.id.tv_playlist_info);
        }

        public void bind(Playlist playlist, OnPlaylistClickListener listener) {
            playlistName.setText(playlist.getName());
            // Ở đây chúng ta sử dụng userId vì không có tên người tạo
            // Trong thực tế, bạn nên lấy tên người dùng từ một repository hoặc cache
            playlistInfo.setText("Playlist • User " + playlist.getUserId());

            if (playlist.getCoverImageUrl() != null && !playlist.getCoverImageUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(playlist.getCoverImageUrl())
                        .placeholder(R.drawable.artist_placeholder)
                        .error(R.drawable.artist_placeholder)
                        .into(playlistImage);
            } else {
                playlistImage.setImageResource(R.drawable.artist_placeholder);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onPlaylistClick(playlist);
                }
            });
        }
    }
}
