package com.prm.profile;

public class AddSongResult {
    private final boolean success;
    private final String data;
    private final String errorMessage;

    private AddSongResult(boolean success, String data, String errorMessage) {
        this.success = success;
        this.data = data;
        this.errorMessage = errorMessage;
    }

    public static AddSongResult success(String songId) {
        return new AddSongResult(true, songId, null);
    }

    public static AddSongResult error(String errorMessage) {
        return new AddSongResult(false, null, errorMessage);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getData() {
        return data;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getSongId() {
        return data;
    }
}
