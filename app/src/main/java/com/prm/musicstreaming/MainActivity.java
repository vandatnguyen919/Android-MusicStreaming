package com.prm.musicstreaming;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private BottomNavigationView navView;
    private Toolbar toolbar;
    private NavController navController;

    private boolean isNavigatingFromDestinationListener = false;
    private boolean isTopLevelDestination = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().setStatusBarColor(getResources().getColor(R.color.app_background, null));
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set up the toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set up the bottom navigation view
        navView = findViewById(R.id.nav_view);
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_search, R.id.navigation_library)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        // Add a listener to handle navigation from child fragment back to parent fragment
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            // Check if current destination is login fragment
            if (destination.getId() == R.id.navigation_login) {
                // Hide toolbar and bottom navigation when on login screen
                toolbar.setVisibility(View.GONE);
                navView.setVisibility(View.GONE);
            } else {
                // Show toolbar and bottom navigation for all other fragments
                toolbar.setVisibility(View.VISIBLE);
                navView.setVisibility(View.VISIBLE);

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
                    toolbar.setNavigationOnClickListener(v -> navController.navigateUp());
                    invalidateOptionsMenu();
                }

                // Existing destination changed logic
                if (destination.getId() == R.id.navigation_album && !isNavigatingFromDestinationListener) {
                    isNavigatingFromDestinationListener = true;
                    navView.setSelectedItemId(R.id.navigation_home);
                    isNavigatingFromDestinationListener = false;
                }
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

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, appBarConfiguration)
               || super.onSupportNavigateUp();
    }
}