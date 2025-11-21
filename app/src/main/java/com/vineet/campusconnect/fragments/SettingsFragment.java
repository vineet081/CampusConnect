package com.vineet.campusconnect.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.vineet.campusconnect.R;

public class SettingsFragment extends Fragment {

    private SwitchMaterial switchDarkMode, switchNotifications;
    private LinearLayout layoutChangePass, layoutFeedback;
    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        sharedPreferences = requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);

        switchDarkMode = view.findViewById(R.id.switch_dark_mode);
        switchNotifications = view.findViewById(R.id.switch_notifications);
        layoutChangePass = view.findViewById(R.id.layout_change_password);
        layoutFeedback = view.findViewById(R.id.layout_feedback);

        // --- FIX: Handle Dark Mode Switch Safely ---
        boolean isDarkMode = sharedPreferences.getBoolean("dark_mode", false);

        // 1. Remove listener before setting state to prevent infinite loop
        switchDarkMode.setOnCheckedChangeListener(null);

        // 2. Set the visual state
        switchDarkMode.setChecked(isDarkMode);

        // 3. Re-attach listener for USER clicks only
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Save preference
            sharedPreferences.edit().putBoolean("dark_mode", isChecked).apply();

            // Apply Theme (This recreates the activity)
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });
        // -------------------------------------------

        // Setup Notifications Switch
        boolean isNotifEnabled = sharedPreferences.getBoolean("notifications", true);
        switchNotifications.setOnCheckedChangeListener(null); // Good practice here too
        switchNotifications.setChecked(isNotifEnabled);
        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("notifications", isChecked).apply();
            String status = isChecked ? "Enabled" : "Disabled";
            Toast.makeText(getContext(), "Task Reminders " + status, Toast.LENGTH_SHORT).show();
        });

        // Change Password Click
        layoutChangePass.setOnClickListener(v -> {
            String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            if (email != null) {
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                        .addOnSuccessListener(aVoid ->
                                Toast.makeText(getContext(), "Reset link sent to " + email, Toast.LENGTH_LONG).show())
                        .addOnFailureListener(e ->
                                Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });

        // Feedback Click
        layoutFeedback.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:support@campusconnect.com"));
            intent.putExtra(Intent.EXTRA_SUBJECT, "CampusConnect Feedback");
            try {
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(getContext(), "No email app found", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}