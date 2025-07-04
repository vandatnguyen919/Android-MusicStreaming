package com.prm.musicstreaming;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.common.util.UnstableApi;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.prm.common.Navigator;
import com.prm.common.util.SampleSongs;
import com.prm.domain.model.Song;
import com.prm.common.MiniPlayerViewModel;
import com.prm.payment.result.PaymentSuccessFragment;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import vn.zalopay.sdk.ZaloPaySDK;

@UnstableApi
@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private BottomNavigationView navView;
    private Toolbar toolbar;
    private View miniPlayer;
    private NavController navController;
    private MiniPlayerViewModel miniPlayerViewModel;

    @Inject
    Navigator navigator;

    // Mini player UI components
    private ImageView ivMiniPlayerCover;
    private TextView tvMiniPlayerTitle;
    private TextView tvMiniPlayerArtist;
    private ImageButton btnMiniPlayerPlayPause;

    private final Handler progressHandler = new Handler();
    private Runnable progressRunnable;

    private boolean isNavigatingFromDestinationListener = false;
    private boolean isTopLevelDestination = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Force dark theme for this activity
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        // Check authentication status
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            // User not authenticated, redirect to AuthActivity
            navigator.navigateToAuth(this);
            return;
        }

        EdgeToEdge.enable(this);
        getWindow().setStatusBarColor(getResources().getColor(R.color.app_background, null));
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize ViewModel
        miniPlayerViewModel = new ViewModelProvider(this).get(MiniPlayerViewModel.class);

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

        // Add a listener to handle navigation from child fragment back to parent fragment
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            boolean showToolbar =
                    destination.getId() != R.id.navigation_membership_plan &&
                    destination.getId() != R.id.navigation_search_result &&
                    destination.getId() != R.id.navigation_login;

            boolean showBottomNav =
                    destination.getId() != R.id.navigation_payment_success &&
                    destination.getId() != R.id.navigation_search_result &&
                    destination.getId() != R.id.navigation_login;

            boolean showMiniPlayer =
                    destination.getId() == R.id.navigation_home ||
                    destination.getId() == R.id.navigation_search ||
                    destination.getId() == R.id.navigation_library ||
                    destination.getId() == R.id.navigation_membership_plan;

            toolbar.setVisibility(showToolbar ? View.VISIBLE : View.GONE);
            navView.setVisibility(showBottomNav? View.VISIBLE : View.GONE);
            miniPlayer.setVisibility(showMiniPlayer? View.VISIBLE : View.GONE);

            // Determine if we're on a top-level destination
            isTopLevelDestination = appBarConfiguration.getTopLevelDestinations()
                    .contains(destination.getId());

            // Set profile icon for top-level destinations except search, back button for others
            if (destination.getId() == R.id.navigation_search) {
                toolbar.setNavigationIcon(null);
                invalidateOptionsMenu();
            } else if (isTopLevelDestination) {
                toolbar.setNavigationIcon(R.drawable.ic_profile);
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
    }

    private void initializeMiniPlayer() {
        miniPlayer = findViewById(R.id.mini_player);
        ivMiniPlayerCover = miniPlayer.findViewById(R.id.iv_mini_player_cover);
        tvMiniPlayerTitle = miniPlayer.findViewById(R.id.tv_mini_player_title);
        tvMiniPlayerArtist = miniPlayer.findViewById(R.id.tv_mini_player_artist);
        btnMiniPlayerPlayPause = miniPlayer.findViewById(R.id.btn_mini_player_play_pause);

        btnMiniPlayerPlayPause.setOnClickListener(v -> miniPlayerViewModel.playPause());

        miniPlayer.setOnClickListener(v -> {
            NavOptions navOptions = new NavOptions.Builder()
                    .setPopUpTo(R.id.navigation_home, false)
                    .build();
            navController.navigate(R.id.navigation_album, null, navOptions);
        });
    }

    private void setupMiniPlayerObservers() {
        miniPlayerViewModel.getCurrentSong().observe(this, this::updateMiniPlayerSong);
        miniPlayerViewModel.getIsPlaying().observe(this, this::updatePlayPauseButton);

        // Start progress updates when playing
        miniPlayerViewModel.getIsPlaying().observe(this, isPlaying -> {
            if (isPlaying) {
                startProgressUpdates();
            } else {
                stopProgressUpdates();
            }
        });
    }

    private void updateMiniPlayerSong(Song song) {
        if (song != null) {
            tvMiniPlayerTitle.setText(song.getTitle());
            tvMiniPlayerArtist.setText(song.getArtistId()); // You might want to resolve artist name
            miniPlayer.setVisibility(View.VISIBLE);

            // Load album cover using your preferred image loading library (Glide, Picasso, etc.)
            // For now, using a placeholder
            ivMiniPlayerCover.setImageResource(com.prm.common.R.drawable.ic_music_note);
        } else {
            miniPlayer.setVisibility(View.GONE);
        }
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
        ZaloPaySDK.getInstance().onResult(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopProgressUpdates();
        if (miniPlayerViewModel != null) {
            miniPlayerViewModel.disconnect();
        }
    }
}