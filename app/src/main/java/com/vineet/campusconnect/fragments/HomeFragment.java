package com.vineet.campusconnect.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.vineet.campusconnect.*;

public class HomeFragment extends Fragment {

    private ChipGroup chipGroupMainFilter;
    private ImageButton btnMenu;
    private TextView tvDynamicTitle;
    private FirebaseUser currentUser;

    private MaterialCardView cardCanteen, cardTask, cardLinks, cardEvent;
    private MaterialCardView cardDoubt, cardGroup, cardLostFound;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // 1. Find Views
        chipGroupMainFilter = view.findViewById(R.id.chip_group_main_filter);
        btnMenu = view.findViewById(R.id.btn_menu);
        tvDynamicTitle = view.findViewById(R.id.tv_dynamic_title);

        cardCanteen = view.findViewById(R.id.card_canteen);
        cardTask = view.findViewById(R.id.card_task);
        cardLinks = view.findViewById(R.id.card_links);
        cardEvent = view.findViewById(R.id.card_event);
        cardDoubt = view.findViewById(R.id.card_doubt);
        cardGroup = view.findViewById(R.id.card_group);
        cardLostFound = view.findViewById(R.id.card_lost_found);

        // 2. Load User Name
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) loadUserName();
        else tvDynamicTitle.setText("Hey User, ease your uni stuff");

        // 3. Setup Filter Toggle
        chipGroupMainFilter.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chip_filter_utility) filterCards("utility");
            else if (checkedId == R.id.chip_filter_peer) filterCards("peer");
        });
        // Default to Utility
        if (chipGroupMainFilter.getCheckedChipId() == View.NO_ID) {
            chipGroupMainFilter.check(R.id.chip_filter_utility);
        } else {
            // If fragment is recreated (e.g. back press), restore state
            if (chipGroupMainFilter.getCheckedChipId() == R.id.chip_filter_utility) filterCards("utility");
            else filterCards("peer");
        }

        // 4. Menu Button
        btnMenu.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).openDrawer();
            }
        });

        // 5. Setup Card Clicks
        cardCanteen.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.nav_canteen));
        cardTask.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.nav_task_manager));
        cardLinks.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.nav_link_categories));

        cardDoubt.setOnClickListener(v -> startActivity(new Intent(getActivity(), DoubtFeedActivity.class)));
        cardGroup.setOnClickListener(v -> startActivity(new Intent(getActivity(), GroupFeedActivity.class)));
        cardLostFound.setOnClickListener(v -> startActivity(new Intent(getActivity(), LostAndFoundActivity.class)));
        cardEvent.setOnClickListener(v -> Toast.makeText(getContext(), "Event Tracker coming soon!", Toast.LENGTH_SHORT).show());

        return view;
    }

    // --- NEW PUBLIC METHOD FOR MAINACTIVITY TO CALL ---
    public void switchToMode(String mode) {
        if (mode.equals("peer")) {
            chipGroupMainFilter.check(R.id.chip_filter_peer);
        } else {
            chipGroupMainFilter.check(R.id.chip_filter_utility);
        }
    }

    private void loadUserName() {
        FirebaseFirestore.getInstance().collection("users").document(currentUser.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        if (name != null && !name.isEmpty()) {
                            String firstName = name.split(" ")[0];
                            tvDynamicTitle.setText("Hey " + firstName + ", ease your uni stuff");
                        } else {
                            tvDynamicTitle.setText("Hey, ease your uni stuff");
                        }
                    }
                });
    }

    private void filterCards(String tag) {
        int utilityVis = tag.equals("utility") ? View.VISIBLE : View.GONE;
        int peerVis = tag.equals("peer") ? View.VISIBLE : View.GONE;

        cardCanteen.setVisibility(utilityVis);
        cardTask.setVisibility(utilityVis);
        cardLinks.setVisibility(utilityVis);
        cardEvent.setVisibility(utilityVis);

        cardDoubt.setVisibility(peerVis);
        cardGroup.setVisibility(peerVis);
        cardLostFound.setVisibility(peerVis);
    }
}