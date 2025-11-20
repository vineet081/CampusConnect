package com.vineet.campusconnect.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.vineet.campusconnect.R;
import com.vineet.campusconnect.adapters.LinkAdapter;
import com.vineet.campusconnect.models.LinkItem;

public class QuickLinksFragment extends Fragment {

    private RecyclerView recyclerView;
    private LinkAdapter adapter;
    private FirebaseFirestore db;
    private String currentCategory;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quick_links, container, false);

        // 1. Get the category passed from the previous fragment
        if (getArguments() != null) {
            currentCategory = getArguments().getString("CATEGORY_NAME");
        }
        if (currentCategory == null) currentCategory = "All";

        // 2. Setup Toolbar
        Toolbar toolbar = view.findViewById(R.id.toolbar_quick_links);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(currentCategory);
        toolbar.setNavigationOnClickListener(v -> getParentFragmentManager().popBackStack());

        // 3. Initialize Firebase & RecyclerView
        db = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.recycler_view_links);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // 4. Setup initial query
        setupRecyclerView(getBaseQuery());

        // 5. Setup Search View
        SearchView searchView = view.findViewById(R.id.search_view_links);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                firebaseSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                firebaseSearch(newText);
                return false;
            }
        });

        return view;
    }

    private Query getBaseQuery() {
        if (currentCategory.equals("All")) {
            return db.collection("useful_links").orderBy("title");
        } else {
            return db.collection("useful_links")
                    .whereEqualTo("category", currentCategory)
                    .orderBy("title");
        }
    }

    private void firebaseSearch(String searchText) {
        Query firebaseSearchQuery;
        if (searchText.isEmpty()) {
            firebaseSearchQuery = getBaseQuery();
        } else {
            firebaseSearchQuery = db.collection("useful_links")
                    .orderBy("title")
                    .startAt(searchText)
                    .endAt(searchText + "\uf8ff");
        }
        setupRecyclerView(firebaseSearchQuery);
    }

    private void setupRecyclerView(Query query) {
        if (adapter != null) {
            adapter.stopListening();
        }
        FirestoreRecyclerOptions<LinkItem> options = new FirestoreRecyclerOptions.Builder<LinkItem>()
                .setQuery(query, LinkItem.class)
                .build();

        adapter = new LinkAdapter(options, getContext());
        recyclerView.setAdapter(adapter);
        adapter.startListening();
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