package com.prm.domain.util;


public class Constants {
    

    public static class Search {

        public static final int MAX_ARTISTS_RESULT = 10;

        public static final int MAX_SONGS_RESULT = 20;
        public static final int MAX_ALBUMS_RESULT = 10;
        public static final int MAX_PLAYLISTS_RESULT = 10;
        public static final int MAX_IDS_IN_QUERY = 10;
        public static final int SEARCH_DEBOUNCE_TIME_MS = 300;
    }

    public static class Firebase {
        public static final String COLLECTION_ARTISTS = "artists";
        public static final String COLLECTION_SONGS = "songs";
        public static final String COLLECTION_ALBUMS = "albums";
        public static final String COLLECTION_PLAYLISTS = "playlists";
        
        public static final String FIELD_ARTIST_ID = "artist_id";
        public static final String FIELD_ALBUM_ID = "album_id";
    }
}
