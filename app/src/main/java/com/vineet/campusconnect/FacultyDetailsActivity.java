package com.vineet.campusconnect;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;

public class FacultyDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // --- CHECKPOINT 1 ---
        Toast.makeText(this, "Checkpoint 1: Activity Started", Toast.LENGTH_SHORT).show();

        try {
            setContentView(R.layout.activity_faculty_details);
        } catch (Exception e) {
            Toast.makeText(this, "CRASH: Layout file error!", Toast.LENGTH_LONG).show();
            Log.e("FacultyDebug", "Layout Error", e);
            finish();
            return;
        }

        // --- CHECKPOINT 2 ---
        Toast.makeText(this, "Checkpoint 2: Layout Loaded", Toast.LENGTH_SHORT).show();

        try {
            // Get Data
            String name = getIntent().getStringExtra("NAME");
            String designation = getIntent().getStringExtra("DESIGNATION");
            String email = getIntent().getStringExtra("EMAIL");
            String cabin = getIntent().getStringExtra("CABIN");
            String imageUrl = getIntent().getStringExtra("IMAGE");

            // Find Views
            TextView tvName = findViewById(R.id.tv_detail_name);
            TextView tvDesig = findViewById(R.id.tv_detail_designation);
            TextView tvEmail = findViewById(R.id.tv_detail_email);
            TextView tvCabin = findViewById(R.id.tv_detail_cabin);
            ImageView ivImage = findViewById(R.id.iv_detail_image);
            MaterialButton btnContact = findViewById(R.id.btn_contact_faculty);

            // Check for null views (ID mismatch)
            if (tvName == null || btnContact == null) {
                Toast.makeText(this, "CRASH: Could not find Views! Check XML IDs.", Toast.LENGTH_LONG).show();
                return;
            }

            // Set Data
            tvName.setText(name);
            tvDesig.setText(designation);
            tvEmail.setText(email);
            tvCabin.setText(cabin);

            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(this).load(imageUrl).into(ivImage);
            }

            // Click Listener
            btnContact.setOnClickListener(v -> {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Email", email);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(this, "Email copied to clipboard!", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:" + email));
                try {
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            Toast.makeText(this, "CRASH: Error setting data: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("FacultyDebug", "Logic Error", e);
            finish();
            return;
        }

        // --- CHECKPOINT 3 ---
        Toast.makeText(this, "Checkpoint 3: Success!", Toast.LENGTH_SHORT).show();
    }
}