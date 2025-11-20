package com.vineet.campusconnect.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.google.android.material.card.MaterialCardView;
import com.vineet.campusconnect.R;

public class LinkCategoriesFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_quick_links_categories, container, false);

        MaterialCardView cardAcademics = view.findViewById(R.id.card_cat_academics);
        MaterialCardView cardResources = view.findViewById(R.id.card_cat_resources);
        MaterialCardView cardEvents = view.findViewById(R.id.card_cat_events);
        MaterialCardView cardServices = view.findViewById(R.id.card_cat_services);
        MaterialCardView cardFaculty = view.findViewById(R.id.card_cat_faculty);

        cardAcademics.setOnClickListener(v -> openCategory("Academics", v));
        cardResources.setOnClickListener(v -> openCategory("Resources", v));
        cardEvents.setOnClickListener(v -> openCategory("Events", v));
        cardServices.setOnClickListener(v -> openCategory("Services", v));
        cardFaculty.setOnClickListener(v -> openCategory("Faculty", v));

        return view;
    }

    // UPDATED: This method now navigates correctly
    private void openCategory(String categoryName, View view) {
        // INTERCEPT FACULTY CLICK
        if (categoryName.equals("Faculty")) {
            Intent intent = new Intent(getActivity(), com.vineet.campusconnect.FacultyDirectoryActivity.class);
            startActivity(intent);
            return; // Stop here, don't do the normal navigation
        }

        // Normal behavior for other categories
        Bundle args = new Bundle();
        args.putString("CATEGORY_NAME", categoryName);
        Navigation.findNavController(view).navigate(R.id.action_categories_to_list, args);
    }
}