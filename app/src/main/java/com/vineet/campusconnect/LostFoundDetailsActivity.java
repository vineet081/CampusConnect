package com.vineet.campusconnect;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.res.ColorStateList;
import android.graphics.Color;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView; // Import CardView
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LostFoundDetailsActivity extends AppCompatActivity {

    private String documentId;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private String authorId;

    // UI Elements
    private ImageView ivImage;
    private MaterialCardView cardImage; // Added CardView
    private Chip chipStatus;
    private TextView tvTitle, tvAuthorDate, tvLocation, tvDescription;
    private Button btnContact, btnMarkReturned;
    // We removed the toolbar, so no need for that variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // --- CHECKPOINT 1 ---
        Log.d("DEBUG", "onCreate: Starting...");

        try {
            setContentView(R.layout.activity_lost_found_details);
        } catch (Exception e) {
            Log.e("DEBUG", "CRASH! Failed to set content view", e);
            Toast.makeText(this, "CRASH! Failed to load layout file.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // --- CHECKPOINT 2 ---
        Log.d("DEBUG", "Checkpoint 2: Layout loaded");
        Toast.makeText(this, "Checkpoint 2: Layout loaded", Toast.LENGTH_SHORT).show();

        // 1. Get Document ID
        documentId = getIntent().getStringExtra("DOCUMENT_ID");
        if (documentId == null || documentId.isEmpty()) {
            Log.e("DEBUG", "CRASH! documentId was null. Finishing.");
            Toast.makeText(this, "Error: Item not found (null ID).", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 2. Initialize Firebase
        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // 3. Find all UI views
        try {
            ivImage = findViewById(R.id.iv_details_image);
            cardImage = findViewById(R.id.card_details_image); // Find the card
            chipStatus = findViewById(R.id.chip_details_status);
            tvTitle = findViewById(R.id.tv_details_title);
            tvAuthorDate = findViewById(R.id.tv_details_author_date);
            tvLocation = findViewById(R.id.tv_details_location);
            tvDescription = findViewById(R.id.tv_details_description);
            btnContact = findViewById(R.id.btn_contact_owner);
            btnMarkReturned = findViewById(R.id.btn_mark_returned);

        } catch (Exception e) {
            Log.e("DEBUG", "CRASH! Failed to find views (findViewById)", e);
            Toast.makeText(this, "CRASH! Error finding views.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // --- CHECKPOINT 3 ---
        Log.d("DEBUG", "Checkpoint 3: All views found.");

        // 5. Load the data
        loadItemDetails();
    }

    private void loadItemDetails() {
        // --- CHECKPOINT 4 ---
        Log.d("DEBUG", "Checkpoint 4: Loading data from Firestore...");

        db.collection("lost_and_found").document(documentId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot doc = task.getResult();
                        if (!doc.exists()) {
                            Log.e("DEBUG", "CRASH! Document doesn't exist in Firebase.");
                            Toast.makeText(this, "Error: Item details not found in database.", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }

                        // --- CHECKPOINT 5 ---
                        Log.d("DEBUG", "Checkpoint 5: Data found! Setting UI...");

                        try {
                            // Set basic text data
                            tvTitle.setText(doc.getString("title"));
                            tvLocation.setText(doc.getString("location"));
                            tvDescription.setText(doc.getString("description"));

                            // Set author and date
                            String authorName = doc.getString("authorName");
                            long timestamp = doc.getLong("timestamp");
                            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                            tvAuthorDate.setText("Posted by " + authorName + " on " + sdf.format(new Date(timestamp)));

                            // Set image using Glide
                            String imageUrl = doc.getString("imageUrl");
                            if (imageUrl != null && !imageUrl.isEmpty()) {
                                cardImage.setVisibility(View.VISIBLE); // Show the card
                                Glide.with(this).load(imageUrl).centerCrop().into(ivImage);
                            } else {
                                cardImage.setVisibility(View.GONE); // Hide the card
                            }

                            // Set Status Chip
                            if ("FOUND".equals(doc.getString("status"))) {
                                chipStatus.setText("FOUND");
                                chipStatus.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
                            } else {
                                chipStatus.setText("LOST");
                                chipStatus.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#D32F2F")));
                            }

                            // --- Button Logic ---
                            authorId = doc.getString("authorId");
                            String contactInfo = doc.getString("contactInfo");
                            Boolean isReturned = doc.getBoolean("isReturned");
                            if (isReturned == null) isReturned = false;

                            // Contact Button
                            btnContact.setText("Contact: " + contactInfo);
                            btnContact.setOnClickListener(v -> {
                                Toast.makeText(this, "Contacting " + contactInfo, Toast.LENGTH_SHORT).show();
                            });

                            // "Mark as Returned" Button
                            if (currentUser != null && currentUser.getUid().equals(authorId) && !isReturned) {
                                btnMarkReturned.setVisibility(View.VISIBLE);
                                btnMarkReturned.setOnClickListener(v -> markAsReturned());
                            }

                            // If item is already returned
                            if (isReturned) {
                                btnContact.setEnabled(false);
                                btnContact.setText("Item has been returned");
                                btnMarkReturned.setVisibility(View.GONE);
                                chipStatus.setText("RETURNED");
                                chipStatus.setChipBackgroundColor(ColorStateList.valueOf(Color.GRAY));
                            }

                            // --- CHECKPOINT 6 ---
                            Log.d("DEBUG", "Checkpoint 6: UI Updated successfully!");

                        } catch (Exception e) {
                            Log.e("DEBUG", "CRASH! Failed to set UI. Check your data types in Firebase!", e);
                            Toast.makeText(this, "CRASH! Data mismatch. Check Firebase fields (e.g., 'timestamp').", Toast.LENGTH_LONG).show();
                        }

                    } else {
                        Log.e("DEBUG", "CRASH! Firestore task was not successful.", task.getException());
                        Toast.makeText(this, "Error loading item.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void markAsReturned() {
        btnMarkReturned.setEnabled(false);
        db.collection("lost_and_found").document(documentId)
                .update("isReturned", true)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Marked as returned!", Toast.LENGTH_SHORT).show();
                    loadItemDetails();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to update. Try again.", Toast.LENGTH_SHORT).show();
                    btnMarkReturned.setEnabled(true);
                });
    }
}