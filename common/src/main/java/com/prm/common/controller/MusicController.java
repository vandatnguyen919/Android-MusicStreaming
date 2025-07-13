package com.prm.common.controller;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.media3.common.util.UnstableApi;

import com.prm.domain.model.Song;
import com.prm.common.service.MusicService;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;

@Singleton
public class MusicController {

    private final Context context;
    private MusicService musicService;
    private boolean isBound = false;
    private MediaBrowserCompat mediaBrowser;
    private MediaControllerCompat mediaController;

    // LiveData for observing playback state
    private final MutableLiveData<Boolean> isPlayingLiveData = new MutableLiveData<>(false);
    private final MutableLiveData<Song> currentSongLiveData = new MutableLiveData<>();
    private final MutableLiveData<Long> currentPositionLiveData = new MutableLiveData<>(0L);
    private final MutableLiveData<Long> durationLiveData = new MutableLiveData<>(0L);

    @Inject
    public MusicController(@ApplicationContext Context context) {
        this.context = context;
        initializeMediaBrowser();
    }

    private void initializeMediaBrowser() {
        mediaBrowser = new MediaBrowserCompat(
            context,
            new ComponentName(context, MusicService.class),
            connectionCallback,
            null
        );
    }

    private final MediaBrowserCompat.ConnectionCallback connectionCallback =
        new MediaBrowserCompat.ConnectionCallback() {
            @Override
            public void onConnected() {
                isConnecting = false;
                try {
                    mediaController = new MediaControllerCompat(context, mediaBrowser.getSessionToken());
                    mediaController.registerCallback(controllerCallback);

                    // Update initial state
                    updatePlaybackState(mediaController.getPlaybackState());
                    updateMetadata(mediaController.getMetadata());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onConnectionFailed() {
                isConnecting = false;
                // Handle connection failure
            }
        };

    private final MediaControllerCompat.Callback controllerCallback =
        new MediaControllerCompat.Callback() {
            @Override
            public void onPlaybackStateChanged(PlaybackStateCompat state) {
                updatePlaybackState(state);
            }

            @Override
            public void onMetadataChanged(MediaMetadataCompat metadata) {
                updateMetadata(metadata);
            }
        };

    private void updatePlaybackState(PlaybackStateCompat state) {
        if (state == null) return;

        boolean playing = state.getState() == PlaybackStateCompat.STATE_PLAYING;
        isPlayingLiveData.postValue(playing);
        currentPositionLiveData.postValue(state.getPosition());
    }

    private void updateMetadata(MediaMetadataCompat metadata) {
        if (metadata == null) return;

        durationLiveData.postValue(metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION));
    }

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            musicService = binder.getService();
            isBound = true;

            // Update current state
            updateCurrentState();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicService = null;
            isBound = false;
        }
    };

    private boolean isConnecting = false;

    public void connect() {
        if (!mediaBrowser.isConnected() && !isConnecting) {
            isConnecting = true;
            mediaBrowser.connect();
        }

        if (!isBound) {
            Intent intent = new Intent(context, MusicService.class);
            context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    public void disconnect() {
        if (mediaBrowser.isConnected()) {
            mediaBrowser.disconnect();
        }

        if (isBound) {
            context.unbindService(serviceConnection);
            isBound = false;
        }

        if (mediaController != null) {
            mediaController.unregisterCallback(controllerCallback);
        }
    }

    public void playMusic(Song song) {
        if (isBound && musicService != null) {
            musicService.playMusic(song);
            currentSongLiveData.postValue(song);
            startService();
        }
    }

    public void playPlaylist(List<Song> songs, int startIndex) {
        if (isBound && musicService != null) {
            musicService.playPlaylist(songs, startIndex);
            if (!songs.isEmpty() && startIndex < songs.size()) {
                currentSongLiveData.postValue(songs.get(startIndex));
            }
            startService();
        }
    }

    public void playPause() {
        if (mediaController != null) {
            if (isPlaying()) {
                mediaController.getTransportControls().pause();
            } else {
                mediaController.getTransportControls().play();
            }
        }
    }

    public void pause() {
        if (mediaController != null) {
            mediaController.getTransportControls().pause();
        }
    }

    public void resume() {
        if (mediaController != null) {
            mediaController.getTransportControls().play();
        }
    }

    public void skipToNext() {
        if (mediaController != null) {
            mediaController.getTransportControls().skipToNext();
        }
    }

    public void skipToPrevious() {
        if (mediaController != null) {
            mediaController.getTransportControls().skipToPrevious();
        }
    }

    public void seekTo(long position) {
        if (mediaController != null) {
            mediaController.getTransportControls().seekTo(position);
        }
    }

    public boolean isPlaying() {
        if (isBound && musicService != null) {
            return musicService.isPlaying();
        }
        return Boolean.TRUE.equals(isPlayingLiveData.getValue());
    }

    public Song getCurrentSong() {
        if (isBound && musicService != null) {
            return musicService.getCurrentSong();
        }
        return currentSongLiveData.getValue();
    }

    public long getCurrentPosition() {
        if (isBound && musicService != null) {
            return musicService.getCurrentPosition();
        }
        return currentPositionLiveData.getValue() != null ? currentPositionLiveData.getValue() : 0L;
    }

    public long getDuration() {
        if (isBound && musicService != null) {
            return musicService.getDuration();
        }
        return durationLiveData.getValue() != null ? durationLiveData.getValue() : 0L;
    }

    // LiveData getters for UI observation
    public LiveData<Boolean> getIsPlayingLiveData() {
        return isPlayingLiveData;
    }

    public LiveData<Song> getCurrentSongLiveData() {
        return currentSongLiveData;
    }

    public LiveData<Long> getCurrentPositionLiveData() {
        return currentPositionLiveData;
    }

    public LiveData<Long> getDurationLiveData() {
        return durationLiveData;
    }

    private void startService() {
        Intent intent = new Intent(context, MusicService.class);
        context.startService(intent);
    }

    private void updateCurrentState() {
        if (isBound && musicService != null) {
            Song currentSong = musicService.getCurrentSong();
            boolean playing = musicService.isPlaying();
            long position = musicService.getCurrentPosition();
            long duration = musicService.getDuration();

            currentSongLiveData.postValue(currentSong);
            isPlayingLiveData.postValue(playing);
            currentPositionLiveData.postValue(position);
            durationLiveData.postValue(duration);
        }
    }

    public void updateProgress() {
        if (isBound && musicService != null && musicService.isPlaying()) {
            long position = musicService.getCurrentPosition();
            currentPositionLiveData.postValue(position);
        }
    }
}
