package com.vineet.campusconnect;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;

public class LinkCategoriesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_links_categories);

        // Find all the category cards
        MaterialCardView cardAcademics = findViewById(R.id.card_cat_academics);
        MaterialCardView cardResources = findViewById(R.id.card_cat_resources);
        MaterialCardView cardEvents = findViewById(R.id.card_cat_events);
        MaterialCardView cardServices = findViewById(R.id.card_cat_services);
        MaterialCardView cardFaculty = findViewById(R.id.card_cat_faculty);

        // Set click listeners for each card
        // We pass the EXACT category name that you will use in Firestore
        cardAcademics.setOnClickListener(v -> openCategory("Academics"));
        cardResources.setOnClickListener(v -> openCategory("Resources"));
        cardEvents.setOnClickListener(v -> openCategory("Events"));
        cardServices.setOnClickListener(v -> openCategory("Services"));
        cardFaculty.setOnClickListener(v -> openCategory("Faculty"));
    }

    // Helper method to open the list page with the selected category
    private void openCategory(String categoryName) {
        Intent intent = new Intent(LinkCategoriesActivity.this, QuickLinksActivity.class);
        // We pass the category name as an "extra" piece of data
        intent.putExtra("CATEGORY_NAME", categoryName);
        startActivity(intent);
    }
}