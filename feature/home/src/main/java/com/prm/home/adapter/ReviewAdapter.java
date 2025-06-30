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
import com.prm.home.model.ReviewUiModel;

import java.util.ArrayList;
import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    
    private List<ReviewUiModel> items = new ArrayList<>();
    private OnItemClickListener listener;
    
    public interface OnItemClickListener {
        void onItemClick(ReviewUiModel item);
    }
    
    public void setItems(List<ReviewUiModel> items) {
        this.items = items != null ? items : new ArrayList<>();
        notifyDataSetChanged();
    }
    
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        ReviewUiModel item = items.get(position);
        holder.bind(item);
    }
    
    @Override
    public int getItemCount() {
        return items.size();
    }
    
    class ReviewViewHolder extends RecyclerView.ViewHolder {
        
        private final ShapeableImageView imageView;
        private final TextView titleTextView;
        
        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            
            imageView = itemView.findViewById(R.id.iv_review);
            titleTextView = itemView.findViewById(R.id.tv_review_title);
            
            itemView.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onItemClick(items.get(getAdapterPosition()));
                }
            });
        }
        
        public void bind(ReviewUiModel item) {
            titleTextView.setText(item.getTitle());

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
