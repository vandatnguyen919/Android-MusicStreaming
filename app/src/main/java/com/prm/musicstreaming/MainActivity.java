package com.prm.musicstreaming;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

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
    private NavController navController;

    private boolean isNavigatingFromDestinationListener = false;
    private boolean isTopLevelDestination = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set up the bottom navigation view
        BottomNavigationView navView = findViewById(R.id.nav_view);
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_search, R.id.navigation_library)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        // Add a listener to handle navigation from child fragment back to parent fragment
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            // Determine if we're on a top-level destination
            isTopLevelDestination = appBarConfiguration.getTopLevelDestinations()
                    .contains(destination.getId());

            // Set profile icon for top-level destinations, back button for others
            if (isTopLevelDestination) {
                toolbar.setNavigationIcon(R.drawable.ic_profile);
                toolbar.setNavigationOnClickListener(v -> navController.navigate(R.id.navigation_profile));
            } else {
                toolbar.setNavigationIcon(R.drawable.ic_back); // Or let system handle it
                toolbar.setNavigationOnClickListener(v -> navController.navigateUp());
            }

            // Existing destination changed logic
            if (destination.getId() == R.id.navigation_album && !isNavigatingFromDestinationListener) {
                isNavigatingFromDestinationListener = true;
                navView.setSelectedItemId(R.id.navigation_home);
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

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, appBarConfiguration)
               || super.onSupportNavigateUp();
    }
}