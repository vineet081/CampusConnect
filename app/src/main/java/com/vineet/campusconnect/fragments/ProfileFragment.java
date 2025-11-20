package com.vineet.campusconnect.fragments;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.vineet.campusconnect.LoginActivity;
import com.vineet.campusconnect.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ProfileFragment extends Fragment {

    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;

    // UI Elements
    private TextView tvName, tvEmail, tvBranch;
    private Button btnLogout;
    private Toolbar toolbar; // We'll add this to the layout

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // 1. Find UI elements
        tvName = view.findViewById(R.id.tv_profile_name);
        tvEmail = view.findViewById(R.id.tv_profile_email);
        tvBranch = view.findViewById(R.id.tv_profile_branch);
        btnLogout = view.findViewById(R.id.btn_logout);

        // Note: We are not using a toolbar inside the fragment
        // The main activity's bottom bar will be visible.

        // 2. Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        // 3. Load user data
        if (currentUser != null) {
            loadUserProfileData();
        } else {
            goToLogin();
        }

        // 4. Set click listener for Logout Button
        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Toast.makeText(getContext(), "Logged out", Toast.LENGTH_SHORT).show();
            goToLogin();
        });

        return view;
    }

    private void loadUserProfileData() {
        String uid = currentUser.getUid();

        tvEmail.setText(currentUser.getEmail());

        db.collection("users").document(uid)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            tvName.setText(document.getString("name"));
                            tvBranch.setText(document.getString("branch"));
                        } else {
                            Log.d("Profile", "No user data found in Firestore");
                        }
                    } else {
                        Log.w("Profile", "Error getting document: ", task.getException());
                    }
                });
    }

    private void goToLogin() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();
    }
}