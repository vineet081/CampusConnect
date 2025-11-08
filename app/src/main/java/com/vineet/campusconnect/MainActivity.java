package com.vineet.campusconnect;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.card.MaterialCardView;

public class MainActivity extends AppCompatActivity {

    // UI elements
    MaterialButtonToggleGroup toggleGroup;
    GridLayout gridUtility, gridPeer;
    ImageButton profileButton;
    TextView welcomeTitle;
    MaterialCardView cardCanteen, cardEvent, cardTask, cardLinks;
    MaterialCardView cardDoubt, cardGroup, cardLostFound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // --- 1. Find all UI elements ---
        toggleGroup = findViewById(R.id.toggle_group);
        gridUtility = findViewById(R.id.grid_utility);
        gridPeer = findViewById(R.id.grid_peer);
        profileButton = findViewById(R.id.btn_profile);
        welcomeTitle = findViewById(R.id.tv_welcome_title);

        cardCanteen = findViewById(R.id.card_canteen);
        cardEvent = findViewById(R.id.card_event);
        cardTask = findViewById(R.id.card_task);
        cardLinks = findViewById(R.id.card_links);

        cardDoubt = findViewById(R.id.card_doubt);
        cardGroup = findViewById(R.id.card_group);
        cardLostFound = findViewById(R.id.card_lost_found);

        // --- 2. Set up Toggle Button (Utility vs Peer) ---
        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.btn_toggle_utility) {
                    gridUtility.setVisibility(View.VISIBLE);
                    gridPeer.setVisibility(View.GONE);
                } else if (checkedId == R.id.btn_toggle_peer) {
                    gridUtility.setVisibility(View.GONE);
                    gridPeer.setVisibility(View.VISIBLE);
                }
            }
        });

        // --- 3. Set up Click Listeners (Navigation) ---

        // Profile -> ProfileActivity
        profileButton.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
        });

        // Canteen -> CanteenActivity
        cardCanteen.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, CanteenActivity.class));
        });

        // Task Manager -> TaskManagerActivity (THIS WAS MISSING!)
        cardTask.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, TaskManagerActivity.class));
        });

        // Group Finder -> GroupFeedActivity
        cardGroup.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, GroupFeedActivity.class));
        });


        // --- Links Card ---
        // --- Links Card ---
        cardLinks.setOnClickListener(v -> {
            // CHANGED: Now opens LinkCategoriesActivity instead of QuickLinksActivity
            startActivity(new Intent(MainActivity.this, LinkCategoriesActivity.class));
        });

        // --- 4. "Coming Soon" Placeholders for unfinished features ---
        cardEvent.setOnClickListener(v -> showComingSoonToast("Event Tracker"));

        cardDoubt.setOnClickListener(v -> showComingSoonToast("Doubt Forum"));
        cardLostFound.setOnClickListener(v -> showComingSoonToast("Lost & Found"));
    }

    private void showComingSoonToast(String featureName) {
        Toast.makeText(MainActivity.this, featureName + " coming soon!", Toast.LENGTH_SHORT).show();
    }
}