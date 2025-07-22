package com.prm.musicstreaming;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.common.util.UnstableApi;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.prm.common.MiniPlayerViewModel;
import com.prm.common.Navigator;
import com.prm.domain.model.Song;
import com.prm.payment.result.PaymentSuccessFragment;

import java.util.Locale;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import vn.zalopay.sdk.ZaloPaySDK;

@UnstableApi
@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final long AD_INTERVAL = 60000; // 1 minute in milliseconds

    private AppBarConfiguration appBarConfiguration;
    private BottomNavigationView navView;
    private Toolbar toolbar;
    private View miniPlayer;
    private NavController navController;
    private MiniPlayerViewModel miniPlayerViewModel;
    private MainViewModel mainViewModel;

    // Ad related variables
    private InterstitialAd mInterstitialAd;
    private final Handler adHandler = new Handler();
    private Runnable adRunnable;

    @Inject
    Navigator navigator;

    // Mini player UI components
    private ImageView ivMiniPlayerCover;
    private TextView tvMiniPlayerTitle;
    private TextView tvMiniPlayerArtist;
    private ImageButton btnMiniPlayerPlayPause;
    private ProgressBar miniPlayerProgress;

    private final Handler progressHandler = new Handler();
    private Runnable progressRunnable;

    private boolean isNavigatingFromDestinationListener = false;
    private boolean isTopLevelDestination = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Force dark theme for this activity
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );
        setContentView(R.layout.activity_main);

        // Initialize ViewModels
        miniPlayerViewModel = new ViewModelProvider(this).get(MiniPlayerViewModel.class);
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        // Set up observers for ad control
        setupMainViewModelObservers();

        // Initialize Mobile Ads SDK (will be controlled by user registration status)
        initializeAds();

        // Subscribe to FCM topic for new songs
        FirebaseMessaging.getInstance().subscribeToTopic("new_songs")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("MainActivity", "Subscribed to new songs notifications");
                    } else {
                        Log.e("MainActivity", "Failed to subscribe to notifications", task.getException());
                    }
                });

        // Set up the toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize mini player components
        initializeMiniPlayer();

        // Set up the bottom navigation view
        navView = findViewById(R.id.nav_view);
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_search, R.id.navigation_library, R.id.navigation_membership_plan)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        // Set up observers for mini player
        setupMiniPlayerObservers();

        // Connect to music service
        miniPlayerViewModel.connect();

        // Start ad timer
        startAdTimer();

        // Add a listener to handle navigation from child fragment back to parent fragment
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            boolean showToolbar =
                    destination.getId() != R.id.navigation_track_view &&
                    destination.getId() != R.id.navigation_notification_permission &&
                    destination.getId() != R.id.navigation_membership_plan &&
                    destination.getId() != R.id.navigation_search_result &&
                    destination.getId() != R.id.navigation_login;

            boolean showBottomNav =
                    destination.getId() != R.id.navigation_track_view &&
                    destination.getId() != R.id.navigation_edit_profile &&
                    destination.getId() != R.id.navigation_notification_permission &&
                    destination.getId() != R.id.navigation_profile &&
                    destination.getId() != R.id.navigation_checkout &&
                    destination.getId() != R.id.navigation_payment_success &&
                    destination.getId() != R.id.navigation_search_result &&
                    destination.getId() != R.id.navigation_login;

            toolbar.setVisibility(showToolbar ? View.VISIBLE : View.GONE);
            navView.setVisibility(showBottomNav? View.VISIBLE : View.GONE);

            boolean showMiniPlayer =
                    destination.getId() == R.id.navigation_home ||
                    destination.getId() == R.id.navigation_search ||
                    destination.getId() == R.id.navigation_library ||
                    destination.getId() == R.id.navigation_membership_plan;

            Song currentSong = miniPlayerViewModel.getCurrentSong().getValue();
            boolean shouldShow = showMiniPlayer && currentSong != null;

            miniPlayer.setVisibility(shouldShow ? View.VISIBLE : View.GONE);

            // Determine if we're on a top-level destination
            isTopLevelDestination = appBarConfiguration.getTopLevelDestinations()
                    .contains(destination.getId());

            // Set profile icon for top-level destinations except search, back button for others
            if (destination.getId() == R.id.navigation_search) {
                toolbar.setNavigationIcon(null);
                invalidateOptionsMenu();
            } else if (isTopLevelDestination) {
                this.loadCircularAvatarFromUrl(String.valueOf(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()));
                toolbar.setNavigationOnClickListener(v -> navController.navigate(R.id.navigation_profile));
                invalidateOptionsMenu();
            } else {
                toolbar.setNavigationIcon(R.drawable.ic_back);
                toolbar.setNavigationOnClickListener(v -> onSupportNavigateUp());
                invalidateOptionsMenu();
            }

            // Existing destination changed logic
            if (destination.getId() == R.id.navigation_album && !isNavigatingFromDestinationListener) {
                isNavigatingFromDestinationListener = true;
                navView.setSelectedItemId(R.id.navigation_home);
                isNavigatingFromDestinationListener = false;
            } else if (destination.getId() == R.id.navigation_checkout && !isNavigatingFromDestinationListener) {
                isNavigatingFromDestinationListener = true;
                navView.setSelectedItemId(R.id.navigation_membership_plan);
                isNavigatingFromDestinationListener = false;
            } else if (destination.getId() == R.id.navigation_search_result && !isNavigatingFromDestinationListener) {
                isNavigatingFromDestinationListener = true;
                navView.setSelectedItemId(R.id.navigation_search);
                isNavigatingFromDestinationListener = false;
            }
        });

        // Add this listener to prevent navigation when we're just updating UI
        navView.setOnItemSelectedListener(item -> {
            if (isNavigatingFromDestinationListener) {
                return true;
            }
            return NavigationUI.onNavDestinationSelected(item, navController) || super.onOptionsItemSelected(item);
        });

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            navigator.clearAndNavigate(com.prm.common.R.string.route_home);
        }
    }

    private void setupMainViewModelObservers() {
        mainViewModel.shouldShowAds.observe(this, shouldShowAds -> {
            if (shouldShowAds) {
                // User is not registered, show ads
                startAdTimer();
            } else {
                // User is registered, turn off ads
                stopAdTimer();
                Log.d(TAG, "Ads disabled for registered user");
            }
        });
    }

    private void initializeAds() {
        // Check if ads should be shown before initializing
        Boolean shouldShowAds = mainViewModel.shouldShowAds.getValue();
        if (shouldShowAds == null || !shouldShowAds) {
            Log.d(TAG, "Ads initialization skipped - user is registered");
            return;
        }

        MobileAds.initialize(this, initializationStatus -> {
            Log.d(TAG, "MobileAds initialized");
            loadInterstitialAd();
        });
    }

    private void loadInterstitialAd() {
        // Double check if ads should still be shown
        Boolean shouldShowAds = mainViewModel.shouldShowAds.getValue();
        if (shouldShowAds == null || !shouldShowAds) {
            Log.d(TAG, "Ad loading cancelled - user is registered");
            return;
        }

        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this, getString(R.string.interstitial_ad_unit_id), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        mInterstitialAd = interstitialAd;
                        Log.d(TAG, "Interstitial ad loaded successfully");

                        interstitialAd.setFullScreenContentCallback(
                                new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        mInterstitialAd = null;
                                        Log.d(TAG, "The ad was dismissed.");
                                        // Load a new ad for the next time
                                        loadInterstitialAd();
                                    }

                                    @Override
                                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                        mInterstitialAd = null;
                                        Log.d(TAG, "The ad failed to show: " + adError.getMessage());
                                        // Load a new ad for the next time
                                        loadInterstitialAd();
                                    }

                                    @Override
                                    public void onAdShowedFullScreenContent() {
                                        Log.d(TAG, "The ad was shown.");
                                        boolean isPlaying = Boolean.TRUE.equals(miniPlayerViewModel.getIsPlaying().getValue());
                                        if (isPlaying) {
                                            miniPlayerViewModel.playPause();
                                        }
                                    }
                                });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        Log.i(TAG, "Failed to load interstitial ad: " + loadAdError.getMessage());
                        mInterstitialAd = null;

                        String error = String.format(
                                Locale.ENGLISH,
                                "domain: %s, code: %d, message: %s",
                                loadAdError.getDomain(),
                                loadAdError.getCode(),
                                loadAdError.getMessage());

                        Log.e(TAG, "onAdFailedToLoad() with error: " + error);

                        // Retry loading ad after a delay (e.g., 30 seconds)
                        adHandler.postDelayed(() -> loadInterstitialAd(), 30000);
                    }
                });
    }

    private void showInterstitialAd() {
        // Check if ads should be shown before displaying
        Boolean shouldShowAds = mainViewModel.shouldShowAds.getValue();
        if (shouldShowAds == null || !shouldShowAds) {
            Log.d(TAG, "Ad display cancelled - user is registered");
            return;
        }

        if (mInterstitialAd != null) {
            mInterstitialAd.show(this);
        } else {
            Log.d(TAG, "The interstitial ad wasn't ready yet.");
            // Try to load a new ad
            loadInterstitialAd();
        }
    }

    private void startAdTimer() {
        // Check if ads should be shown before starting timer
        Boolean shouldShowAds = mainViewModel.shouldShowAds.getValue();
        if (shouldShowAds == null || !shouldShowAds) {
            Log.d(TAG, "Ad timer start cancelled - user is registered");
            return;
        }

        if (adRunnable == null) {
            adRunnable = new Runnable() {
                @Override
                public void run() {
                    showInterstitialAd();
                    adHandler.postDelayed(this, AD_INTERVAL);
                }
            };
        }
        // Start the first ad after 1 minute
        adHandler.postDelayed(adRunnable, AD_INTERVAL);
    }

    private void stopAdTimer() {
        if (adRunnable != null) {
            adHandler.removeCallbacks(adRunnable);
        }
    }

    private void loadCircularAvatarFromUrl(String imageUrl) {
        int iconSize = (int) (30 * getResources().getDisplayMetrics().density); // 30dp

        Glide.with(this)
                .asBitmap()
                .load(imageUrl)
                .placeholder(R.drawable.ic_profile)
                .error(R.drawable.ic_profile)
                .override(iconSize, iconSize)
                .circleCrop()
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        toolbar.setNavigationIcon(new BitmapDrawable(getResources(), resource));
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        toolbar.setNavigationIcon(R.drawable.ic_profile);
                    }
                });
    }

    private void initializeMiniPlayer() {
        miniPlayer = findViewById(R.id.mini_player);
        ivMiniPlayerCover = miniPlayer.findViewById(R.id.iv_mini_player_cover);
        tvMiniPlayerTitle = miniPlayer.findViewById(R.id.tv_mini_player_title);
        tvMiniPlayerArtist = miniPlayer.findViewById(R.id.tv_mini_player_artist);
        btnMiniPlayerPlayPause = miniPlayer.findViewById(R.id.btn_mini_player_play_pause);
        miniPlayerProgress = miniPlayer.findViewById(R.id.mini_player_progress);

        btnMiniPlayerPlayPause.setOnClickListener(v -> miniPlayerViewModel.playPause());

        miniPlayer.setOnClickListener(v -> {
            NavOptions navOptions = new NavOptions.Builder()
                    .setPopUpTo(R.id.navigation_home, false)
                    .build();
            navController.navigate(R.id.navigation_track_view, null, navOptions);
        });
    }

    private void setupMiniPlayerObservers() {
        miniPlayerViewModel.getCurrentSong().observe(this, this::updateMiniPlayerSong);
        miniPlayerViewModel.getIsPlaying().observe(this, this::updatePlayPauseButton);

        // Observe progress for mini player
        miniPlayerViewModel.getCurrentPosition().observe(this, this::updateMiniPlayerProgress);
        miniPlayerViewModel.getDuration().observe(this, this::updateMiniPlayerDuration);

        miniPlayerViewModel.getIsPlaying().observe(this, isPlaying -> {
            if (isPlaying) {
                startProgressUpdates();
            } else {
                stopProgressUpdates();
            }
        });
    }

    private void updateMiniPlayerProgress(Long position) {
        if (position != null && miniPlayerProgress.getMax() > 0) {
            int progress = (int) (position / 1000);
            miniPlayerProgress.setProgress(progress);
        }
    }

    private void updateMiniPlayerDuration(Long duration) {
        if (duration != null && duration > 0) {
            int durationInSeconds = (int) (duration / 1000);
            miniPlayerProgress.setMax(durationInSeconds);
        }
    }

    private void updateMiniPlayerSong(Song song) {
        if (song != null) {
            tvMiniPlayerTitle.setText(song.getTitle());
            tvMiniPlayerArtist.setText(song.getArtistId()); // You might want to resolve artist name
            miniPlayer.setVisibility(View.VISIBLE);

            Glide.with(this)
                    .asBitmap()
                    .load(song.getImageUrl())
                    .placeholder(com.prm.common.R.drawable.ic_music_note)
                    .error(com.prm.common.R.drawable.ic_music_note)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            ivMiniPlayerCover.setImageBitmap(resource);
                            extractColorAndSetMiniPlayerGradient(resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            ivMiniPlayerCover.setImageDrawable(placeholder);
                        }
                    });
        } else {
            miniPlayer.setVisibility(View.GONE);
        }
    }

    private void extractColorAndSetMiniPlayerGradient(Bitmap bitmap) {
        Palette.from(bitmap).generate(palette -> {
            int dominantColor = palette.getDominantColor(0xFF424242);

            int endColor = darkenColor(dominantColor, 0.1f);

            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setColor(endColor);
            gradientDrawable.setCornerRadius(8 * getResources().getDisplayMetrics().density);

            miniPlayer.setBackground(gradientDrawable);
            miniPlayer.setClipToOutline(true);
        });
    }

    private int darkenColor(int color, float factor) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= (1 - factor);

        if (hsv[2] > 0.9f) {
            hsv[2] = 0.9f;
        }

        return Color.HSVToColor(hsv);
    }

    private void updatePlayPauseButton(Boolean isPlaying) {
        if (isPlaying != null && isPlaying) {
            btnMiniPlayerPlayPause.setImageResource(com.prm.common.R.drawable.ic_pause);
        } else {
            btnMiniPlayerPlayPause.setImageResource(com.prm.common.R.drawable.ic_play);
        }
    }

    private void startProgressUpdates() {
        if (progressRunnable == null) {
            progressRunnable = new Runnable() {
                @Override
                public void run() {
                    miniPlayerViewModel.updateProgress();
                    progressHandler.postDelayed(this, 1000); // Update every second
                }
            };
        }
        progressHandler.post(progressRunnable);
    }

    private void stopProgressUpdates() {
        if (progressRunnable != null) {
            progressHandler.removeCallbacks(progressRunnable);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        // Get the current fragment
        Fragment currentFragment = getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_main)
                .getChildFragmentManager()
                .getPrimaryNavigationFragment();

        // Handle special cases
        if (currentFragment instanceof PaymentSuccessFragment) {
            navigator.navigate(com.prm.common.R.string.route_plan_management);
            return true;
        }

        return NavigationUI.navigateUp(navController, appBarConfiguration)
               || super.onSupportNavigateUp();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        try {
            ZaloPaySDK.getInstance().onResult(intent);
        } catch (Exception e) {
            Log.e("MainActivity", "Error handling ZaloPay result", e);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Pause ad timer when activity is not visible
        stopAdTimer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Resume ad timer when activity becomes visible
        startAdTimer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopProgressUpdates();
        stopAdTimer();
        if (miniPlayerViewModel != null) {
            miniPlayerViewModel.disconnect();
        }
    }
}