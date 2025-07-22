package com.prm.domain.model;

import java.util.UUID;

public class ChatMessage {
    private final String id;
    private final String text;
    private final Author author;
    private final long timestamp;

    public enum Author {
        USER, BOT
    }

    // Constructor for messages from UI (auto-ID and timestamp)
    public ChatMessage(String text, Author author) {
        this(UUID.randomUUID().toString(), text, author, System.currentTimeMillis());
    }

    // Full constructor
    public ChatMessage(String id, String text, Author author, long timestamp) {
        this.id = id;
        this.text = text;
        this.author = author;
        this.timestamp = timestamp;
    }

    public String getId() { return id; }
    public String getText() { return text; }
    public Author getAuthor() { return author; }
    public long getTimestamp() { return timestamp; }
}

