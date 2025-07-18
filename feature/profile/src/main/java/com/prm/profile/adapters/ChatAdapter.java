package com.prm.profile.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.prm.domain.model.ChatMessage;
import com.prm.profile.R;

public class ChatAdapter extends ListAdapter<ChatMessage, ChatAdapter.ChatViewHolder> {

    public ChatAdapter() {
        super(new ChatDiffCallback());
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_message, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        private final TextView authorTextView;
        private final TextView messageTextView;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            authorTextView = itemView.findViewById(R.id.tvMessageAuthor);
            messageTextView = itemView.findViewById(R.id.tvMessageText);
        }

        public void bind(ChatMessage chatMessage) {
            authorTextView.setText(chatMessage.getAuthor() == ChatMessage.Author.USER ? "You" : "Chatbot");
            messageTextView.setText(chatMessage.getText());

            // Optional: Align messages
            // ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) itemView.getLayoutParams();
            // if (chatMessage.getAuthor() == ChatMessage.Author.USER) {
            //    ((LinearLayout) itemView).setGravity(Gravity.END);
            // } else {
            //    ((LinearLayout) itemView).setGravity(Gravity.START);
            // }
            // itemView.setLayoutParams(layoutParams);
        }
    }

    private static class ChatDiffCallback extends DiffUtil.ItemCallback<ChatMessage> {
        @Override
        public boolean areItemsTheSame(@NonNull ChatMessage oldItem, @NonNull ChatMessage newItem) {
            return oldItem.getTimestamp() == newItem.getTimestamp();
        }

        @Override
        public boolean areContentsTheSame(@NonNull ChatMessage oldItem, @NonNull ChatMessage newItem) {
            return oldItem.equals(newItem);
        }
    }
}
