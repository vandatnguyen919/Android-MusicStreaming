package com.prm.common.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.media.MediaBrowserServiceCompat;
import androidx.media.session.MediaButtonReceiver;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerNotificationManager;
import androidx.media3.ui.PlayerNotificationManager.MediaDescriptionAdapter;

import com.prm.common.R;
import com.prm.domain.model.Song;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MusicService extends MediaBrowserServiceCompat {

    private static final String MEDIA_ROOT_ID = "media_root_id";
    private static final String NOTIFICATION_CHANNEL_ID = "music_channel";
    private static final int NOTIFICATION_ID = 1;
    private static final String MAIN_ACTIVITY_CLASS_NAME = "com.prm.musicstreaming.MainActivity";

    private MediaSessionCompat mediaSession;
    private PlaybackStateCompat.Builder stateBuilder;
    private ExoPlayer exoPlayer;
    private PlayerNotificationManager playerNotificationManager;

    private Song currentSong;
    private List<Song> playlist = new ArrayList<>();
    private int currentIndex = 0;
    private boolean isPlaying = false;

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    private final IBinder binder = new MusicBinder();

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();
        initializeSession();
        initializePlayer();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    "Music Player",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Music playback controls");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private void initializeSession() {
        mediaSession = new MediaSessionCompat(this, getClass().getSimpleName());
        mediaSession.setCallback(new MediaSessionCallback());

        stateBuilder = new PlaybackStateCompat.Builder()
                .setActions(PlaybackStateCompat.ACTION_PLAY |
                        PlaybackStateCompat.ACTION_PAUSE |
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT |
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                        PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mediaSession.setPlaybackState(stateBuilder.build());
        setSessionToken(mediaSession.getSessionToken());
    }

    private void initializePlayer() {
        exoPlayer = new ExoPlayer.Builder(this).build();
        exoPlayer.addListener(new PlayerEventListener());
        
        // Initialize PlayerNotificationManager
        playerNotificationManager = new PlayerNotificationManager.Builder(this, NOTIFICATION_ID, NOTIFICATION_CHANNEL_ID)
            .setChannelNameResourceId(R.string.notification_channel_name)
            .setChannelDescriptionResourceId(R.string.notification_channel_description)
            .setMediaDescriptionAdapter(new MediaDescriptionAdapter() {
                @Override
                public CharSequence getCurrentContentTitle(Player player) {
                    return currentSong != null ? currentSong.getTitle() : "Music Player";
                }

                @Nullable
                @Override
                public CharSequence getCurrentContentText(Player player) {
                    return currentSong != null ? currentSong.getArtistId() : "";
                }

                @Nullable
                @Override
                public CharSequence getCurrentSubText(Player player) {
                    return null;
                }

                @Nullable
                @Override
                public Bitmap getCurrentLargeIcon(Player player, PlayerNotificationManager.BitmapCallback callback) {
                    return null;
                }

                @Nullable
                @Override
                public PendingIntent createCurrentContentIntent(Player player) {
                    Intent intent;
                    try {
                        intent = new Intent(MusicService.this, Class.forName(MAIN_ACTIVITY_CLASS_NAME));
                    } catch (ClassNotFoundException e) {
                        intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    }
                    return PendingIntent.getActivity(MusicService.this, 0, intent,
                            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                }
            })
            .setNotificationListener(new PlayerNotificationManager.NotificationListener() {
                @Override
                public void onNotificationCancelled(int notificationId, boolean dismissedByUser) {
                    stopForeground(true);
                }

                @Override
                public void onNotificationPosted(int notificationId, Notification notification, boolean ongoing) {
                    if (ongoing) {
                        startForeground(notificationId, notification);
                    } else {
                        stopForeground(false);
                    }
                }
            })
            .build();

        playerNotificationManager.setPlayer(exoPlayer);
        playerNotificationManager.setPriority(NotificationCompat.PRIORITY_HIGH);
        playerNotificationManager.setUseNextAction(true);
        playerNotificationManager.setUsePreviousAction(true);
        playerNotificationManager.setUsePlayPauseActions(true);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (SERVICE_INTERFACE.equals(intent.getAction())) {
            return super.onBind(intent);
        }
        return binder;
    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        return new BrowserRoot(MEDIA_ROOT_ID, null);
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        List<MediaBrowserCompat.MediaItem> mediaItems = new ArrayList<>();

        for (Song song : playlist) {
            MediaDescriptionCompat description = new MediaDescriptionCompat.Builder()
                    .setMediaId(song.getId())
                    .setTitle(song.getTitle())
                    .setMediaUri(Uri.parse(song.getUrl()))
                    .build();

            mediaItems.add(new MediaBrowserCompat.MediaItem(description,
                    MediaBrowserCompat.MediaItem.FLAG_PLAYABLE));
        }

        result.sendResult(mediaItems);
    }

    public void playMusic(Song song) {
        currentSong = song;
        String url = song.getUrl();
        
        // Debug log to see the actual URL
        android.util.Log.d("MusicService", "Playing song: " + song.getTitle());
        android.util.Log.d("MusicService", "Song URL: " + url);
        
        // Kiểm tra và sửa URL nếu cần
        if (url == null || url.isEmpty()) {
            android.util.Log.e("MusicService", "Song URL is null or empty!");
            return;
        }
        
        // Đối với các URL từ Firebase Storage, đảm bảo chúng có định dạng đúng
        if (url.contains("firebasestorage.googleapis.com") && !url.startsWith("https://")) {
            url = "https://" + url;
            android.util.Log.d("MusicService", "Corrected URL: " + url);
        }
        
        MediaItem mediaItem = MediaItem.fromUri(Uri.parse(url));
        exoPlayer.setMediaItem(mediaItem);
        exoPlayer.prepare();
        exoPlayer.play();

        updateMetadata();
        updatePlaybackState(PlaybackStateCompat.STATE_PLAYING);
        
        // The notification will be automatically shown by PlayerNotificationManager
    }

    public void playPlaylist(List<Song> songs, int startIndex) {
        playlist = new ArrayList<>(songs);
        currentIndex = startIndex;

        if (!playlist.isEmpty() && currentIndex < playlist.size()) {
            playMusic(playlist.get(currentIndex));
        }
    }

    public void pauseMusic() {
        exoPlayer.pause();
        isPlaying = false;
        updatePlaybackState(PlaybackStateCompat.STATE_PAUSED);
        stopForeground(false);
    }

    public void resumeMusic() {
        exoPlayer.play();
        isPlaying = true;
        updatePlaybackState(PlaybackStateCompat.STATE_PLAYING);
        startForeground(NOTIFICATION_ID, createNotification());
    }

    public void skipToNext() {
        if (currentIndex < playlist.size() - 1) {
            currentIndex++;
            playMusic(playlist.get(currentIndex));
        }
    }

    public void skipToPrevious() {
        if (currentIndex > 0) {
            currentIndex--;
            playMusic(playlist.get(currentIndex));
        }
    }

    public boolean isPlaying() {
        return exoPlayer.isPlaying();
    }

    public Song getCurrentSong() {
        return currentSong;
    }

    public long getCurrentPosition() {
        return exoPlayer.getCurrentPosition();
    }

    public long getDuration() {
        return exoPlayer.getDuration();
    }

    public void seekTo(long position) {
        exoPlayer.seekTo(position);
    }

    private void updateMetadata() {
        if (currentSong == null) return;

        MediaMetadataCompat metadata = new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, currentSong.getTitle())
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, currentSong.getArtistId())
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, currentSong.getDuration() * 1000L)
                .build();

        mediaSession.setMetadata(metadata);
    }

    private void updatePlaybackState(int state) {
        long position = exoPlayer.getCurrentPosition();
        stateBuilder.setState(state, position, 1.0f);
        mediaSession.setPlaybackState(stateBuilder.build());
    }

    private Notification createNotification() {
        Intent intent;
        try {
            intent = new Intent(this, Class.forName(MAIN_ACTIVITY_CLASS_NAME));
        } catch (ClassNotFoundException e) {
            intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_music_note)
                .setContentTitle(currentSong != null ? currentSong.getTitle() : "Music Player")
                .setContentText(currentSong != null ? currentSong.getArtistId() : "")
                .setContentIntent(pendingIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSession.getSessionToken())
                        .setShowActionsInCompactView(0, 1, 2))
                .addAction(R.drawable.ic_skip_previous, "Previous",
                        MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS))
                .addAction(isPlaying ? R.drawable.ic_pause : R.drawable.ic_play,
                        isPlaying ? "Pause" : "Play",
                        MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_PLAY_PAUSE))
                .addAction(R.drawable.ic_skip_next, "Next",
                        MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_SKIP_TO_NEXT));

        return builder.build();
    }

    private class MediaSessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            resumeMusic();
        }

        @Override
        public void onPause() {
            pauseMusic();
        }

        @Override
        public void onSkipToNext() {
            skipToNext();
        }

        @Override
        public void onSkipToPrevious() {
            skipToPrevious();
        }

        @Override
        public void onSeekTo(long pos) {
            seekTo(pos);
        }
    }

    private class PlayerEventListener implements Player.Listener {
        @Override
        public void onPlaybackStateChanged(int playbackState) {
            switch (playbackState) {
                case Player.STATE_READY:
                    if (exoPlayer.getPlayWhenReady()) {
                        isPlaying = true;
                        updatePlaybackState(PlaybackStateCompat.STATE_PLAYING);
                    } else {
                        isPlaying = false;
                        updatePlaybackState(PlaybackStateCompat.STATE_PAUSED);
                    }
                    break;
                case Player.STATE_ENDED:
                    skipToNext();
                    break;
                case Player.STATE_BUFFERING:
                    break;
                case Player.STATE_IDLE:
                    break;
            }
        }

        @Override
        public void onIsPlayingChanged(boolean isPlaying) {
            MusicService.this.isPlaying = isPlaying;
        }
    }

    @Override
    public void onDestroy() {
        if (playerNotificationManager != null) {
            playerNotificationManager.setPlayer(null);
        }
        if (exoPlayer != null) {
            exoPlayer.release();
            exoPlayer = null;
        }
        if (mediaSession != null) {
            mediaSession.release();
            mediaSession = null;
        }
        super.onDestroy();
    }
}