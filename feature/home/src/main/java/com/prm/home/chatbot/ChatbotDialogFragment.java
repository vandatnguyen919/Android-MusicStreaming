package com.prm.home.chatbot;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.prm.home.R;
import com.prm.home.adapter.ChatAdapter;

import dagger.hilt.android.AndroidEntryPoint;


@AndroidEntryPoint
public class ChatbotDialogFragment extends BottomSheetDialogFragment {

    public static final String TAG = "ChatbotDialog";

    private ChatbotViewModel viewModel;
    private ChatAdapter chatAdapter;
    private RecyclerView rvChatMessages;
    private EditText etUserInput;
    private Button btnSendMessage;

    // Removed custom constructor
    public ChatbotDialogFragment() {
    }

    public static ChatbotDialogFragment newInstance() {
        return new ChatbotDialogFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ChatbotViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_chatbot, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvChatMessages = view.findViewById(R.id.rvChatMessages);
        etUserInput = view.findViewById(R.id.etUserInput);
        btnSendMessage = view.findViewById(R.id.btnSendMessage);

        setupRecyclerView();
        setupObservers();

        btnSendMessage.setOnClickListener(v -> {
            String message = etUserInput.getText().toString().trim();
            if (!message.isEmpty()) {
                viewModel.sendMessage(message);
                etUserInput.setText("");
            }
        });
    }

    private void setupRecyclerView() {
        chatAdapter = new ChatAdapter();
        rvChatMessages.setAdapter(chatAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        rvChatMessages.setLayoutManager(layoutManager);
    }

    private void setupObservers() {
        viewModel.getChatMessages().observe(getViewLifecycleOwner(), messages -> {
            chatAdapter.submitList(messages);
            if (messages != null && !messages.isEmpty()) {
                rvChatMessages.smoothScrollToPosition(messages.size() - 1);
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            btnSendMessage.setEnabled(!isLoading);
            etUserInput.setEnabled(!isLoading);
        });

        viewModel.getError().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                viewModel.clearError();
            }
        });
    }
}

