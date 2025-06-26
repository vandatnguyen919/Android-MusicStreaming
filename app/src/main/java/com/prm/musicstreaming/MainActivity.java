package com.prm.musicstreaming;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
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
import com.prm.login.LoginFragment;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity implements LoginFragment.LoginListener {

    private AppBarConfiguration appBarConfiguration;
    private NavController navController;
    private BottomNavigationView navView;
    private View navHostFragment;
    private View fragmentContainer;
    private Toolbar toolbar;

    private boolean isNavigatingFromDestinationListener = false;
    private boolean isUserLoggedIn = false; // This would be determined by your auth system

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

        // Initialize views
        toolbar = findViewById(R.id.toolbar);
        navView = findViewById(R.id.nav_view);
        navHostFragment = findViewById(R.id.nav_host_fragment_activity_main);
        fragmentContainer = findViewById(R.id.fragment_container);

        // Set up the toolbar
        setSupportActionBar(toolbar);

        if (!isUserLoggedIn) {
            // User not logged in - show login screen and hide nav elements
            showLoginScreen();
        } else {
            // User is logged in - show main navigation
            showMainNavigation();
        }
    }

    private void showLoginScreen() {
        // Hide navigation elements
        navView.setVisibility(View.GONE);
        toolbar.setVisibility(View.GONE);
        navHostFragment.setVisibility(View.GONE);
        fragmentContainer.setVisibility(View.VISIBLE);

        // Load the LoginFragment as the first screen
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, LoginFragment.newInstance())
                .commit();
    }

    private void showMainNavigation() {
        // Show navigation elements
        navView.setVisibility(View.VISIBLE);
        toolbar.setVisibility(View.VISIBLE);
        navHostFragment.setVisibility(View.VISIBLE);
        fragmentContainer.setVisibility(View.GONE);

        // Set up the bottom navigation view
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_search, R.id.navigation_library)
                .build();
        
        try {
            navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
            NavigationUI.setupWithNavController(navView, navController);

            // Add a listener to handle navigation from child fragment back to parent fragment
            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to be called when user successfully logs in
    @Override
    public void onLoginSuccess() {
        isUserLoggedIn = true;
        showMainNavigation();
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (navController != null) {
            return NavigationUI.navigateUp(navController, appBarConfiguration)
                   || super.onSupportNavigateUp();
        }
        return super.onSupportNavigateUp();
    }
}