package com.prm.home.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.android.material.imageview.ShapeableImageView;
import com.prm.home.R;
import com.prm.home.model.EditorPickUiModel;

import java.util.ArrayList;
import java.util.List;

public class EditorPicksAdapter extends RecyclerView.Adapter<EditorPicksAdapter.EditorPickViewHolder> {
    
    private List<EditorPickUiModel> items = new ArrayList<>();
    private OnItemClickListener listener;
    
    public interface OnItemClickListener {
        void onItemClick(EditorPickUiModel item);
    }
    
    public void setItems(List<EditorPickUiModel> items) {
        this.items = items != null ? items : new ArrayList<>();
        notifyDataSetChanged();
    }
    
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public EditorPickViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_editor_pick, parent, false);
        return new EditorPickViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull EditorPickViewHolder holder, int position) {
        EditorPickUiModel item = items.get(position);
        holder.bind(item);
    }
    
    @Override
    public int getItemCount() {
        return items.size();
    }
    
    class EditorPickViewHolder extends RecyclerView.ViewHolder {
        
        private final ShapeableImageView imageView;
        private final TextView titleTextView;
        private final TextView subtitleTextView;
        
        public EditorPickViewHolder(@NonNull View itemView) {
            super(itemView);
            
            imageView = itemView.findViewById(R.id.iv_editor_pick);
            titleTextView = itemView.findViewById(R.id.tv_editor_pick_title);
            subtitleTextView = itemView.findViewById(R.id.tv_editor_pick_subtitle);
            
            itemView.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onItemClick(items.get(getAdapterPosition()));
                }
            });
        }
        
        public void bind(EditorPickUiModel item) {
            titleTextView.setText(item.getTitle());
            subtitleTextView.setText(item.getSubtitle());

            // Load image using Glide
            String imageUrl = item.getImageUrl();
            Glide.with(itemView.getContext())
                    .load(imageUrl.isEmpty() ? null : imageUrl)
                    .placeholder(R.drawable.placeholder_song)
                    .error(R.drawable.error_image)
                    .transform(new CenterCrop(), new RoundedCorners(12))
                    .into(imageView);
        }
    }
}
