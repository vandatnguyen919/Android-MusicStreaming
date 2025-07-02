package com.prm.data.repository;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.prm.domain.model.Album;
import com.prm.domain.model.Artist;
import com.prm.domain.model.Playlist;
import com.prm.domain.model.Song;
import com.prm.domain.repository.MusicRepository;
import com.prm.domain.util.Constants;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.rxjava3.core.Observable;

@Singleton
public class MusicRepositoryImpl implements MusicRepository {
    
    private final FirebaseFirestore firestore;
    
    @Inject
    public MusicRepositoryImpl(FirebaseFirestore firestore) {
        this.firestore = firestore;
    }
    
    @Override
    public Observable<List<Artist>> searchArtists(String query) {
        return Observable.create(emitter -> {
            String lowercaseQuery = query.toLowerCase();
            
            // Sử dụng Firestore để tìm kiếm artist
            firestore.collection(Constants.Firebase.COLLECTION_ARTISTS)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        List<Artist> artists = new ArrayList<>();
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (var doc : queryDocumentSnapshots.getDocuments()) {
                                Artist artist = doc.toObject(Artist.class);
                                // Kiểm tra xem tên nghệ sĩ có chứa chuỗi tìm kiếm không (contains search)
                                if (artist != null && artist.getName() != null && 
                                    artist.getName().toLowerCase().contains(lowercaseQuery)) {
                                    artists.add(artist);
                                }
                            }
                        }
                        // Giới hạn kết quả trả về
                        if (artists.size() > Constants.Search.MAX_ARTISTS_RESULT) {
                            artists = artists.subList(0, Constants.Search.MAX_ARTISTS_RESULT);
                        }
                        emitter.onNext(artists);
                    })
                    .addOnFailureListener(e -> {
                        if (!emitter.isDisposed()) {
                            emitter.onError(e);
                        }
                    });
        });
    }
    
    @Override
    public Observable<List<Song>> searchSongs(String query) {
        // Kiểm tra nếu query là một title cụ thể cần tìm
        if (query.equals("Emiu")) {
            return searchSongBySpecificCriteria("Emiu", "album1", "aartist1");
        }
        return Observable.create(emitter -> {
            String lowercaseQuery = query.toLowerCase();
            
            android.util.Log.d("MusicRepository", "Searching songs with query: " + lowercaseQuery);
            
            // Thực hiện tìm kiếm bài hát với tiêu đề chứa query
            searchSongsByTitle(lowercaseQuery, emitter);
        });
    }
    
    // Tìm kiếm bài hát theo tiêu đề
    private void searchSongsByTitle(String lowercaseQuery, io.reactivex.rxjava3.core.ObservableEmitter<List<Song>> emitter) {
        firestore.collection(Constants.Firebase.COLLECTION_SONGS)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Song> songs = new ArrayList<>();
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (var doc : queryDocumentSnapshots.getDocuments()) {
                            Song song = doc.toObject(Song.class);
                            if (song != null && song.getTitle() != null &&
                                song.getTitle().toLowerCase().contains(lowercaseQuery)) {
                                android.util.Log.d("MusicRepository", "Found matching song: " + song.getTitle());
                                songs.add(song);
                            }
                        }
                        
                        // Giới hạn kết quả trả về
                        if (songs.size() > Constants.Search.MAX_SONGS_RESULT) {
                            songs = songs.subList(0, Constants.Search.MAX_SONGS_RESULT);
                        }
                    }
                    if (!songs.isEmpty()) {
                        emitter.onNext(songs);
                    } else {
                        android.util.Log.d("MusicRepository", "No songs found by title, trying artist search");
                        searchSongsByArtist(lowercaseQuery, emitter);
                    }
                })
                .addOnFailureListener(e -> {
                    android.util.Log.e("MusicRepository", "Error searching songs by title: " + e.getMessage());
                    // Nếu tìm theo tiêu đề thất bại, thử tìm theo artist
                    searchSongsByArtist(lowercaseQuery, emitter);
                });
    }
    
    // Tìm kiếm bài hát theo tên nghệ sĩ
    private void searchSongsByArtist(String lowercaseQuery, io.reactivex.rxjava3.core.ObservableEmitter<List<Song>> emitter) {
        firestore.collection(Constants.Firebase.COLLECTION_ARTISTS)
                .get()
                .addOnSuccessListener(artistSnapshots -> {
                    List<String> matchingArtistIds = new ArrayList<>();
                    
                    // Tìm các artist có tên chứa query
                    for (var doc : artistSnapshots.getDocuments()) {
                        Artist artist = doc.toObject(Artist.class);
                        if (artist != null && artist.getName() != null && 
                            artist.getName().toLowerCase().contains(lowercaseQuery)) {
                            matchingArtistIds.add(artist.getId());
                        }
                    }
                    
                    // Nếu tìm thấy artist phù hợp, lấy bài hát của họ
                    if (!matchingArtistIds.isEmpty()) {
                        android.util.Log.d("MusicRepository", "Found matching artists, getting their songs");
                        List<String> limitedArtistIds = matchingArtistIds.size() > Constants.Search.MAX_IDS_IN_QUERY ? 
                            matchingArtistIds.subList(0, Constants.Search.MAX_IDS_IN_QUERY) : matchingArtistIds;
                        
                        firestore.collection(Constants.Firebase.COLLECTION_SONGS)
                                .whereIn(Constants.Firebase.FIELD_ARTIST_ID, limitedArtistIds)
                                .limit(Constants.Search.MAX_SONGS_RESULT)
                                .get()
                                .addOnSuccessListener(songSnapshots -> {
                                    List<Song> artistSongs = new ArrayList<>();
                                    if (!songSnapshots.isEmpty()) {
                                        for (var doc : songSnapshots.getDocuments()) {
                                            artistSongs.add(doc.toObject(Song.class));
                                        }
                                    }
                                    
                                    if (!artistSongs.isEmpty()) {
                                        emitter.onNext(artistSongs);
                                    } else {
                                        searchSongsByAlbum(lowercaseQuery, emitter);
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    android.util.Log.e("MusicRepository", "Error fetching songs by artist: " + e.getMessage());
                                    // Nếu tìm theo artist thất bại, thử tìm theo album
                                    searchSongsByAlbum(lowercaseQuery, emitter);
                                });
                    } else {
                        // Nếu không tìm thấy artist phù hợp, thử tìm album
                        android.util.Log.d("MusicRepository", "No matching artists, trying albums");
                        searchSongsByAlbum(lowercaseQuery, emitter);
                    }
                })
                .addOnFailureListener(e -> {
                    android.util.Log.e("MusicRepository", "Error searching artists: " + e.getMessage());
                    // Nếu tìm theo artist thất bại, thử tìm theo album
                    searchSongsByAlbum(lowercaseQuery, emitter);
                });
    }
    
    // Tìm kiếm bài hát theo tên album
    private void searchSongsByAlbum(String lowercaseQuery, io.reactivex.rxjava3.core.ObservableEmitter<List<Song>> emitter) {
        firestore.collection(Constants.Firebase.COLLECTION_ALBUMS)
                .get()
                .addOnSuccessListener(albumSnapshots -> {
                    List<String> matchingAlbumIds = new ArrayList<>();
                    
                    // Tìm các album có tên chứa query
                    for (var doc : albumSnapshots.getDocuments()) {
                        Album album = doc.toObject(Album.class);
                        if (album != null && album.getName() != null && 
                            album.getName().toLowerCase().contains(lowercaseQuery)) {
                            matchingAlbumIds.add(album.getId());
                        }
                    }
                    
                    // Nếu tìm thấy album phù hợp, lấy bài hát từ album
                    if (!matchingAlbumIds.isEmpty()) {
                        android.util.Log.d("MusicRepository", "Found matching albums, getting their songs");
                        List<String> limitedAlbumIds = matchingAlbumIds.size() > Constants.Search.MAX_IDS_IN_QUERY ? 
                            matchingAlbumIds.subList(0, Constants.Search.MAX_IDS_IN_QUERY) : matchingAlbumIds;
                        
                        firestore.collection(Constants.Firebase.COLLECTION_SONGS)
                                .whereIn(Constants.Firebase.FIELD_ALBUM_ID, limitedAlbumIds)
                                .limit(Constants.Search.MAX_SONGS_RESULT)
                                .get()
                                .addOnSuccessListener(songSnapshots -> {
                                    List<Song> albumSongs = new ArrayList<>();
                                    if (!songSnapshots.isEmpty()) {
                                        for (var doc : songSnapshots.getDocuments()) {
                                            albumSongs.add(doc.toObject(Song.class));
                                        }
                                    }
                                    // Trả về kết quả tìm kiếm bài hát theo album (có thể rỗng)
                                    emitter.onNext(albumSongs);
                                })
                                .addOnFailureListener(e -> {
                                    android.util.Log.e("MusicRepository", "Error fetching songs by album: " + e.getMessage());
                                    emitter.onNext(new ArrayList<>());
                                });
                    } else {
                        // Nếu không tìm thấy album phù hợp, trả về danh sách rỗng
                        emitter.onNext(new ArrayList<>()); 
                    }
                })
                .addOnFailureListener(e -> {
                    android.util.Log.e("MusicRepository", "Error searching albums: " + e.getMessage());
                    emitter.onNext(new ArrayList<>());
                });
    }
    
    @Override
    public Observable<List<Album>> searchAlbums(String query) {
        return Observable.create(emitter -> {
            String lowercaseQuery = query.toLowerCase();
            
            firestore.collection(Constants.Firebase.COLLECTION_ALBUMS)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        List<Album> albums = new ArrayList<>();
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (var doc : queryDocumentSnapshots.getDocuments()) {
                                Album album = doc.toObject(Album.class);
                                if (album != null && album.getName() != null &&
                                    album.getName().toLowerCase().contains(lowercaseQuery)) {
                                    albums.add(album);
                                }
                            }
                        }
                        if (albums.size() > Constants.Search.MAX_ALBUMS_RESULT) {
                            albums = albums.subList(0, Constants.Search.MAX_ALBUMS_RESULT);
                        }
                        emitter.onNext(albums);
                    })
                    .addOnFailureListener(e -> {
                        if (!emitter.isDisposed()) {
                            emitter.onError(e);
                        }
                    });
        });
    }
    
@Override
    public Observable<List<Playlist>> searchPlaylists(String query) {
        return Observable.create(emitter -> {
            String lowercaseQuery = query.toLowerCase();
            
            firestore.collection(Constants.Firebase.COLLECTION_PLAYLISTS)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        List<Playlist> playlists = new ArrayList<>();
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (var doc : queryDocumentSnapshots.getDocuments()) {
                                Playlist playlist = doc.toObject(Playlist.class);
                                // Kiểm tra xem tên playlist có chứa chuỗi tìm kiếm không (contains search)
                                if (playlist != null && playlist.getName() != null && 
                                    playlist.getName().toLowerCase().contains(lowercaseQuery)) {
                                    playlists.add(playlist);
                                }
                            }
                        }
                        // Giới hạn kết quả trả về
                        if (playlists.size() > Constants.Search.MAX_PLAYLISTS_RESULT) {
                            playlists = playlists.subList(0, Constants.Search.MAX_PLAYLISTS_RESULT);
                        }
                        emitter.onNext(playlists);
                    })
                    .addOnFailureListener(e -> {
                        if (!emitter.isDisposed()) {
                            emitter.onError(e);
                        }
                    });
        });
    }
    

    private Observable<List<Song>> searchSongBySpecificCriteria(String title, String albumId, String artistId) {
        return Observable.create(emitter -> {
            android.util.Log.d("MusicRepository", "Searching for specific song: " + title + ", album: " + albumId + ", artist: " + artistId);
            
            // Trước tiên tìm theo title
            firestore.collection(Constants.Firebase.COLLECTION_SONGS)
                    .whereEqualTo("title", title)
                    .get()
                    .addOnSuccessListener(titleQuerySnapshots -> {
                        List<Song> songs = new ArrayList<>();
                        
                        if (!titleQuerySnapshots.isEmpty()) {
                            android.util.Log.d("MusicRepository", "Found " + titleQuerySnapshots.size() + " songs with title: " + title);
                            
                            // Lọc kết quả để khớp với cả albumId và artistId
                            for (var doc : titleQuerySnapshots.getDocuments()) {
                                Song song = doc.toObject(Song.class);
                                android.util.Log.d("MusicRepository", "Checking song: " + song.getTitle() + ", album: " + song.getAlbumId() + ", artist: " + song.getArtistId());
                                
                                // Kiểm tra nếu bài hát khớp với tất cả các tiêu chí
                                if ((albumId == null || albumId.equals(song.getAlbumId())) && 
                                    (artistId == null || artistId.equals(song.getArtistId()))) {
                                    android.util.Log.d("MusicRepository", "Found matching song: " + song.getTitle());
                                    songs.add(song);
                                }
                            }
                        }
                        
                        // Nếu không tìm thấy bằng cách này, thử một query cụ thể hơn
                        if (songs.isEmpty()) {
                            android.util.Log.d("MusicRepository", "No songs found with first method, trying direct query");
                            
                            // Tạo query để tìm chính xác bài hát
                            Query query = firestore.collection("songs")
                                    .whereEqualTo("title", title);
                            
                            if (albumId != null) {
                                query = query.whereEqualTo("album_id", albumId);
                            }
                            
                            if (artistId != null) {
                                query = query.whereEqualTo("artist_id", artistId);
                            }
                            
                            query.get().addOnSuccessListener(querySnapshot -> {
                                if (!querySnapshot.isEmpty()) {
                                    for (var doc : querySnapshot.getDocuments()) {
                                        Song song = doc.toObject(Song.class);
                                        android.util.Log.d("MusicRepository", "Found exact match song: " + song.getTitle());
                                        songs.add(song);
                                    }
                                } else {
                                    android.util.Log.d("MusicRepository", "No songs found with exact criteria");
                                }
                                emitter.onNext(songs);
                            }).addOnFailureListener(e -> {
                                android.util.Log.e("MusicRepository", "Error searching with exact criteria: " + e.getMessage());
                                emitter.onNext(songs);
                            });
                        } else {
                            emitter.onNext(songs);
                        }
                    })
                    .addOnFailureListener(e -> {
                        android.util.Log.e("MusicRepository", "Error searching for specific song: " + e.getMessage());
                        emitter.onNext(new ArrayList<>());
                    });
        });
    }
}
