package com.vineet.campusconnect;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.vineet.campusconnect.adapters.LinkAdapter;
import com.vineet.campusconnect.models.LinkItem;

public class QuickLinksActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LinkAdapter adapter;
    private FirebaseFirestore db;
    private String currentCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_links);

        // 1. Get the category
        currentCategory = getIntent().getStringExtra("CATEGORY_NAME");
        if (currentCategory == null) currentCategory = "All";

        // 2. Setup Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar_quick_links);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(currentCategory);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        // 3. Initialize Firebase & RecyclerView
        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.recycler_view_links);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 4. Setup initial query (load all for this category)
        setupRecyclerView(getBaseQuery());

        // 5. Setup Search View
        SearchView searchView = findViewById(R.id.search_view_links);
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
    }

    // Helper to get the base query for the current category
    private Query getBaseQuery() {
        if (currentCategory.equals("All")) {
            return db.collection("useful_links").orderBy("title");
        } else {
            return db.collection("useful_links")
                    .whereEqualTo("category", currentCategory)
                    .orderBy("title");
        }
    }

    // Search Logic
    private void firebaseSearch(String searchText) {
        Query firebaseSearchQuery;

        // Firestore search is tricky. This is a standard way to do "starts with" search.
        // e.g., searching "Ex" will find "Exam"
        if (searchText.isEmpty()) {
            firebaseSearchQuery = getBaseQuery();
        } else {
            // NOTE: Firestore cannot easily filter by category AND search text at the same time
            // without an advanced index. For simplicity, we'll search ALL links by title.
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

        adapter = new LinkAdapter(options, this);
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null) adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) adapter.stopListening();
    }
}