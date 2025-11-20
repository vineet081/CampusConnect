package com.vineet.campusconnect.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.vineet.campusconnect.R;

public class CanteenFragment extends Fragment {

    private FirebaseFirestore db;
    private TextView mainCanteenTextView;
    private TextView foodCourtTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // 1. Inflate the new fragment layout
        View view = inflater.inflate(R.layout.fragment_canteen, container, false);

        // 2. Find the TextViews *inside* the fragment's view
        mainCanteenTextView = view.findViewById(R.id.tv_main_canteen_menu);
        foodCourtTextView = view.findViewById(R.id.tv_food_court_menu);

        // 3. Initialize Firebase
        db = FirebaseFirestore.getInstance();

        // 4. Call the function to fetch the menu data
        fetchCanteenMenu();

        return view;
    }

    private void fetchCanteenMenu() {
        db.collection("admin_content").document("canteen")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Set the text
                            mainCanteenTextView.setText(document.getString("mainCanteenMenu"));
                            foodCourtTextView.setText(document.getString("foodCourtMenu"));

                        } else {
                            Log.d("Firestore", "No such document");
                            mainCanteenTextView.setText("Menu not found.");
                            foodCourtTextView.setText("Menu not found.");
                        }
                    } else {
                        Log.e("Firestore", "Error getting document: ", task.getException());
                        mainCanteenTextView.setText("Failed to load menu.");
                        foodCourtTextView.setText("Failed to load menu.");
                    }
                });
    }
}