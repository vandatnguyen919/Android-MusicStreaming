package com.prm.profile.utils;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationHelper {
    
    private static final String CHANNEL_ID = "song_channel";
    private static final String CHANNEL_NAME = "Song Notifications";
    private static final int NOTIFICATION_ID = 1001;
    
    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Notifications for when new songs are added");
            
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
    
    public static void showSongAddedNotification(Context context, String artistId) {
        android.util.Log.d("NotificationHelper", "showSongAddedNotification called for artist: " + artistId);
        
        // Check if we have notification permission (for Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) 
                != PackageManager.PERMISSION_GRANTED) {
                android.util.Log.w("NotificationHelper", "POST_NOTIFICATIONS permission not granted");
                // Permission not granted, notification won't be shown
                return;
            }
        }
        
        android.util.Log.d("NotificationHelper", "Permission check passed, creating notification");
        
        // Create notification channel
        createNotificationChannel(context);
        
        // Build notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(com.prm.common.R.drawable.ic_music_note)
                .setContentTitle("New Song Added")
                .setContentText("A new song was added successfully by " + artistId)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);
        
        android.util.Log.d("NotificationHelper", "Notification built, showing now");
        
        // Show notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
        
        android.util.Log.d("NotificationHelper", "Notification shown with ID: " + NOTIFICATION_ID);
    }
    
    public static boolean hasNotificationPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) 
                == PackageManager.PERMISSION_GRANTED;
        }
        return true; // For versions below Android 13, notification permission is granted by default
    }
}
