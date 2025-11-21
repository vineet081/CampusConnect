package com.vineet.campusconnect;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button; // Import Button
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class WelcomeActivity extends AppCompatActivity {

    private static final long SPLASH_DISPLAY_LENGTH = 1500; // 1.5 seconds delay

    private FirebaseAuth mAuth;
    private ImageView logoImage;
    private Button continueButton; // Declare the button here

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // 1. Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // 2. Find the views
        continueButton = findViewById(R.id.continue_button); // Find the button
        logoImage = findViewById(R.id.logo_image);

        // 3. Set the click listener (Manual login path)
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // 4. Implement the Fading Animation
        logoImage.animate()
                .alpha(1.0f)
                .setDuration(1500)
                .start();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            // FIX: Hide the button instantly if the user is logged in
            if (continueButton != null) {
                continueButton.setVisibility(View.GONE);
            }

            // 4. Start the auto-login delay
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                Toast.makeText(WelcomeActivity.this, "Welcome back!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }, SPLASH_DISPLAY_LENGTH);
        }
    }
}