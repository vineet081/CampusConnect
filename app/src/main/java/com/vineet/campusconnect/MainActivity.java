package com.vineet.campusconnect;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.vineet.campusconnect.fragments.HomeFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public DrawerLayout drawerLayout;
    private NavController navController;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Initialize Views
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        BottomNavigationView bottomNavView = findViewById(R.id.bottom_nav_view);

        // 2. Setup Bottom Nav
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            NavigationUI.setupWithNavController(bottomNavView, navController);
        }

        // 3. Setup Drawer
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        // Handle Navigation
        if (id == R.id.nav_profile_edit) {
            if (navController != null) navController.navigate(R.id.nav_profile);
        } else if (id == R.id.nav_settings) {
            Toast.makeText(this, "Settings coming soon!", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        // Handle Mode Switching
        else if (id == R.id.nav_toggle_peer) {
            // Switch to Peer Mode
            updateHomeFragmentMode("peer");
            // Swap visibility of buttons
            toggleMenuVisibility(false);
        } else if (id == R.id.nav_toggle_utility) {
            // Switch to Utility Mode
            updateHomeFragmentMode("utility");
            // Swap visibility of buttons
            toggleMenuVisibility(true);
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    // Helper to toggle the menu items
    private void toggleMenuVisibility(boolean showPeerOption) {
        Menu menu = navigationView.getMenu();
        menu.findItem(R.id.nav_toggle_peer).setVisible(showPeerOption);
        menu.findItem(R.id.nav_toggle_utility).setVisible(!showPeerOption);
    }

    // Helper to talk to HomeFragment
    private void updateHomeFragmentMode(String mode) {
        // First, make sure we are actually on the Home screen!
        if (navController.getCurrentDestination().getId() != R.id.nav_home) {
            navController.navigate(R.id.nav_home);
        }

        // Now find the fragment and call the method
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            Fragment currentFragment = navHostFragment.getChildFragmentManager().getPrimaryNavigationFragment();
            if (currentFragment instanceof HomeFragment) {
                ((HomeFragment) currentFragment).switchToMode(mode);
            }
        }
    }

    public void openDrawer() {
        if (drawerLayout != null) {
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }
}