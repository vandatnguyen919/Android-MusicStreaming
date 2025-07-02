package com.prm.common.util;

import com.prm.domain.model.Song;

import java.util.ArrayList;
import java.util.List;


public class SampleSongs {


    public static Song getSampleSong() {
        return new Song(
            "Sample Song",
            "sample_artist_1",
            "sample_album",
            180, // 3 minutes
            "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
            "https://picsum.photos/300/300?random=1"
        );
    }


    public static List<Song> getSamplePlaylist() {
        List<Song> playlist = new ArrayList<>();

        playlist.add(new Song(
            "SoundHelix Song 1",
            "soundhelix_artist",
            "sample_album",
            180,
            "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
            "https://picsum.photos/300/300?random=2"
        ));

        playlist.add(new Song(
            "SoundHelix Song 2",
            "soundhelix_artist",
            "sample_album",
            200,
            "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
            "https://picsum.photos/300/300?random=3"
        ));

        playlist.add(new Song(
            "SoundHelix Song 3",
            "soundhelix_artist",
            "sample_album",
            220,
            "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3",
            "https://picsum.photos/300/300?random=4"
        ));

        playlist.add(new Song(
            "SoundHelix Song 4",
            "soundhelix_artist",
            "sample_album",
            195,
            "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3",
            "https://picsum.photos/300/300?random=5"
        ));

        return playlist;
    }


    public static List<Song> getAlternativeSamplePlaylist() {
        List<Song> playlist = new ArrayList<>();

        // Using archive.org public domain audio files
        playlist.add(new Song(
            "Bach - Brandenburg Concerto No. 3",
            "bach_artist",
            "classical_album",
            360,
            "https://archive.org/download/jsbach_brandenburg_3/01_-_Allegro.mp3",
            "https://picsum.photos/300/300?random=6"
        ));

        playlist.add(new Song(
            "Mozart - Eine kleine Nachtmusik",
            "mozart_artist",
            "classical_album",
            340,
            "https://archive.org/download/MozartEineKleineNachtmusik/Mozart_-_Eine_kleine_Nachtmusik.mp3",
            "https://picsum.photos/300/300?random=7"
        ));

        return playlist;
    }


    public static Song getShortTestSong() {
        return new Song(
            "Short Test Song",
            "test_artist",
            "test_album",
            30, // 30 seconds
            "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
            "https://picsum.photos/300/300?random=8"
        );
    }
}
