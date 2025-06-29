package com.prm.common.util;

import com.prm.domain.model.Song;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class providing sample songs for testing the media controller
 */
public class SampleSongs {

    /**
     * Get a single sample song for testing
     */
    public static Song getSampleSong() {
        return new Song(
            "sample_1",
            "Sample Song",
            "Sample Artist",
            "sample_album",
            180, // 3 minutes
            "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"
        );
    }

    /**
     * Get a list of sample songs for playlist testing
     */
    public static List<Song> getSamplePlaylist() {
        List<Song> playlist = new ArrayList<>();

        playlist.add(new Song(
            "sample_1",
            "SoundHelix Song 1",
            "SoundHelix",
            "sample_album",
            180,
            "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"
        ));

        playlist.add(new Song(
            "sample_2",
            "SoundHelix Song 2",
            "SoundHelix",
            "sample_album",
            200,
            "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3"
        ));

        playlist.add(new Song(
            "sample_3",
            "SoundHelix Song 3",
            "SoundHelix",
            "sample_album",
            220,
            "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3"
        ));

        playlist.add(new Song(
            "sample_4",
            "SoundHelix Song 4",
            "SoundHelix",
            "sample_album",
            195,
            "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3"
        ));

        return playlist;
    }

    /**
     * Get alternative sample songs with different audio sources
     */
    public static List<Song> getAlternativeSamplePlaylist() {
        List<Song> playlist = new ArrayList<>();

        // Using archive.org public domain audio files
        playlist.add(new Song(
            "classical_1",
            "Bach - Brandenburg Concerto No. 3",
            "Johann Sebastian Bach",
            "classical_album",
            360,
            "https://archive.org/download/jsbach_brandenburg_3/01_-_Allegro.mp3"
        ));

        playlist.add(new Song(
            "classical_2",
            "Mozart - Eine kleine Nachtmusik",
            "Wolfgang Amadeus Mozart",
            "classical_album",
            340,
            "https://archive.org/download/MozartEineKleineNachtmusik/Mozart_-_Eine_kleine_Nachtmusik.mp3"
        ));

        return playlist;
    }

    /**
     * Get a simple test song with a short audio file
     */
    public static Song getShortTestSong() {
        return new Song(
            "test_short",
            "Short Test Song",
            "Test Artist",
            "test_album",
            30, // 30 seconds
            "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"
        );
    }
}
