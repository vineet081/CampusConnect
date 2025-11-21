package com.vineet.campusconnect.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.vineet.campusconnect.LostFoundDetailsActivity;
import com.vineet.campusconnect.R;
import com.vineet.campusconnect.adapters.LostFoundAdapter;
import com.vineet.campusconnect.models.LostFoundItem;

public class LostFoundListFragment extends Fragment {

    private FirebaseFirestore db;
    private LostFoundAdapter adapter;
    private String fragmentStatusType; // Will be "LOST" or "FOUND"

    // This is a "factory" method to create a new fragment
    public static LostFoundListFragment newInstance(String statusType) {
        LostFoundListFragment fragment = new LostFoundListFragment();
        Bundle args = new Bundle();
        args.putString("STATUS_TYPE", statusType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the status ("LOST" or "FOUND") that was passed to us
        if (getArguments() != null) {
            fragmentStatusType = getArguments().getString("STATUS_TYPE");
        }
        db = FirebaseFirestore.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lost_found_list, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_lost_found);

        setupRecyclerView(recyclerView);
        return view;
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        // FIX 1: Add setItemAnimator(null)
        recyclerView.setItemAnimator(null);

        // This is the simple query that doesn't need an index
        Query query = db.collection("lost_and_found")
                .whereEqualTo("status", fragmentStatusType);

        FirestoreRecyclerOptions<LostFoundItem> options = new FirestoreRecyclerOptions.Builder<LostFoundItem>()
                .setQuery(query, LostFoundItem.class)
                .build();

        adapter = new LostFoundAdapter(options, getContext());

        adapter.setOnItemClickListener(documentId -> {
            Toast.makeText(getContext(), "Opening ID: " + documentId, Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(getContext(), LostFoundDetailsActivity.class);
            intent.putExtra("DOCUMENT_ID", documentId);
            startActivity(intent);
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null) adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) adapter.stopListening();
    }
}