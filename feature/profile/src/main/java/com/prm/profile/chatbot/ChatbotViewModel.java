package com.prm.profile.chatbot;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

// Standalone Google AI Client SDK
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.ai.client.generativeai.type.GenerationConfig;
import com.google.ai.client.generativeai.type.TextPart;

import com.google.common.util.concurrent.ListenableFuture;
import com.prm.domain.model.ChatMessage;
import com.prm.domain.model.Song;
import com.prm.domain.model.Playlist;
import com.prm.domain.repository.SongRepository;
import com.prm.domain.repository.PlaylistRepository;
import com.prm.profile.BuildConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.concurrent.ExecutionException; // Added for .get() exception handling

// RxJava Imports
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

// DI Imports
import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ChatbotViewModel extends AndroidViewModel {

    private static final String TAG = "ChatbotViewModel";

    private final MutableLiveData<List<ChatMessage>> chatMessagesLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>(null);

    private final GenerativeModelFutures generativeModelClient;
    private final List<Content> internalChatHistory = new ArrayList<>(); // To maintain conversation history
    private final ExecutorService backgroundExecutor = Executors.newFixedThreadPool(2);


    // Injected Repositories
    private final SongRepository songRepository;
    private final PlaylistRepository playlistRepository;

    @Inject
    public ChatbotViewModel(@NonNull Application application,
                            SongRepository songRepository,
                            PlaylistRepository playlistRepository) {
        super(application);
        this.songRepository = songRepository;
        this.playlistRepository = playlistRepository;

        // --- API Key Handling ---
        String apiKey;
        if (BuildConfig.GEMINI_API_KEY != null &&
                !BuildConfig.GEMINI_API_KEY.isEmpty() &&
                !"[YOUR-API-KEY]".equals(BuildConfig.GEMINI_API_KEY) &&
                !"YOUR_ACTUAL_API_KEY_HERE".equals(BuildConfig.GEMINI_API_KEY)) {
            apiKey = BuildConfig.GEMINI_API_KEY;
        } else {
            apiKey = "";
            Log.e(TAG, "CRITICAL: Gemini API Key is not configured correctly in BuildConfig. Chatbot will likely fail.");
        }
        // --- End API Key Handling ---

        GenerativeModel realGenerativeModel = new GenerativeModel(
                "gemini-1.5-flash-latest",
                apiKey
        );

        GenerationConfig.Builder configBuilder = new GenerationConfig.Builder();


        this.generativeModelClient = GenerativeModelFutures.from(
                realGenerativeModel
//                configBuilder.build()
        );
        // --- End Gemini Model Initialization ---

        initializeChatSeed();
    }

    private void initializeChatSeed() {
        Content systemInstruction = new Content.Builder()
//                .setRole("user")
                .addText("You are a friendly and helpful music assistant for our app. Your name is 'TunePal'. " +
                        "Your goal is to provide personalized music recommendations (songs, albums, artists), " +
                        "help users manage their playlists, and answer general questions about music or app features. " +
                        "Keep your responses concise and engaging. You can use emojis. " +
                        "When asked for recommendations, use any provided user music preferences. " +
                        "If you don't have specific user data, you can ask them about their favorite genres or artists.")
                .build();
        internalChatHistory.add(systemInstruction);

        // The model's first response, establishing its persona.
        Content modelGreeting = new Content.Builder()
//                .setRole("model")
                .addText("Hi there! I'm TunePal 🎵 Your personal music guide. " +
                        "What can I help you discover or organize today? Ask me anything about music!")
                .build();
        internalChatHistory.add(modelGreeting);

        String greetingText = "";
        if (!modelGreeting.getParts().isEmpty()) {
            com.google.ai.client.generativeai.type.Part firstPart = modelGreeting.getParts().get(0);
            if (firstPart instanceof com.google.ai.client.generativeai.type.TextPart) {
                greetingText = ((com.google.ai.client.generativeai.type.TextPart) firstPart).getText();
            }
        }
        addMessageToLiveData(new ChatMessage(greetingText, ChatMessage.Author.BOT));
    }


    public LiveData<List<ChatMessage>> getChatMessages() {
        return chatMessagesLiveData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoadingLiveData;
    }

    public LiveData<String> getError() {
        return errorLiveData;
    }

    public void sendMessage(String userMessageText) {
        if (userMessageText == null || userMessageText.trim().isEmpty()) {
            return;
        }

        ChatMessage uiUserMessage = new ChatMessage(userMessageText, ChatMessage.Author.USER);
        addMessageToLiveData(uiUserMessage);
        isLoadingLiveData.setValue(true);
        errorLiveData.setValue(null);

        backgroundExecutor.execute(() -> {
            String promptPrefix = "";
            if (userMessageText.toLowerCase().contains("recommend") ||
                    userMessageText.toLowerCase().contains("suggest") ||
                    userMessageText.toLowerCase().contains("music for me")) {

                List<Song> favSongsList = new ArrayList<>();
                try {
                    favSongsList = songRepository.getCurrentUserFavoriteSongs(5)
                            .subscribeOn(Schedulers.io())
                            .blockingGet(); // Blocking call for synchronous retrieval
                } catch (Exception e) {
                    Log.e(TAG, "Error fetching favorite songs: " + e.getMessage());
                }

                if (favSongsList != null && !favSongsList.isEmpty()) {
                    String favSongsString = favSongsList.stream()
                            .map(Song::getTitle)
                            .collect(Collectors.joining(", "));
                    promptPrefix += "The user likes songs such as: " + favSongsString + ". ";
                } else {
                    promptPrefix += "The user hasn't specified favorite songs yet. ";
                }

                List<Playlist> playlistsList = new ArrayList<>();
                try {
                    playlistsList = playlistRepository.getCurrentUserPlaylists(3)
                            .subscribeOn(Schedulers.io())
                            .blockingFirst(); // Blocking call for synchronous retrieval
                } catch (Exception e) {
                    Log.e(TAG, "Error fetching playlists: " + e.getMessage());
                }

                if (playlistsList != null && !playlistsList.isEmpty()) {
                    String playlistsString = playlistsList.stream()
                            .map(Playlist::getName)
                            .collect(Collectors.joining(", "));
                    promptPrefix += "They have playlists like: " + playlistsString + ". ";
                } else {
                    promptPrefix += "They don't have any playlists specified yet. ";
                }
            }
            // --- End Personalized Data Enhancement ---

            String fullPromptForApi = promptPrefix + userMessageText;
            Log.d(TAG, "Full prompt for API: " + fullPromptForApi);


            Content userContentForApi = new Content.Builder()
//                    .setRole("user")
                    .addText(fullPromptForApi)
                    .build();

            List<Content> currentApiConversation = new ArrayList<>(internalChatHistory);
            currentApiConversation.add(userContentForApi);

            ListenableFuture<GenerateContentResponse> responseFuture = 
                generativeModelClient.generateContent(currentApiConversation.toArray(new Content[0]));

            // Directly retrieve the result from the ListenableFuture
            try {
                GenerateContentResponse result = responseFuture.get(); // Blocking call on background thread
                isLoadingLiveData.postValue(false);
                String botResponseText = null;
                if (result != null && result.getText() != null && !result.getText().isEmpty()) {
                    botResponseText = result.getText();
                    Log.i(TAG, "Chatbot raw response: " + botResponseText);

                    internalChatHistory.add(new Content.Builder().addText(userMessageText).build());
                    internalChatHistory.add(new Content.Builder().addText(botResponseText).build());

                    addMessageToLiveData(new ChatMessage(botResponseText, ChatMessage.Author.BOT));

                } else {
                    Log.w(TAG, "Gemini response or text was null or empty. Response: " + result);
                    handleApiError("I couldn't come up with a response for that. Try asking differently?");
                }
            } catch (ExecutionException | InterruptedException e) {
                isLoadingLiveData.postValue(false);
                Log.e(TAG, "Error communicating with chatbot API", e);
                handleApiError("Sorry, I'm having trouble connecting. Please try again later. (" + e.getMessage() + ")");
            }
        });
    }

    private void addMessageToLiveData(ChatMessage message) {
        List<ChatMessage> currentList = chatMessagesLiveData.getValue();
        List<ChatMessage> newList = (currentList == null) ? new ArrayList<>() : new ArrayList<>(currentList);
        newList.add(message);
        chatMessagesLiveData.postValue(newList);
    }

    private void handleApiError(String userFriendlyMessage) {
        errorLiveData.postValue(userFriendlyMessage);
        addMessageToLiveData(new ChatMessage(userFriendlyMessage, ChatMessage.Author.BOT));
    }

    public void clearError() {
        errorLiveData.setValue(null);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (backgroundExecutor != null && !backgroundExecutor.isShutdown()) {
            backgroundExecutor.shutdownNow();
        }
        Log.d(TAG, "ChatbotViewModel cleared and executor shutdown.");
    }
}
