package com.vineet.campusconnect;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.vineet.campusconnect.adapters.FacultyAdapter;
import com.vineet.campusconnect.models.Faculty;

public class FacultyDirectoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FacultyAdapter adapter;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_directory);

        Toolbar toolbar = findViewById(R.id.toolbar_faculty);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.recycler_view_faculty);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        setupRecyclerView(""); // Initial load

        SearchView searchView = findViewById(R.id.search_view_faculty);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                setupRecyclerView(query);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                setupRecyclerView(newText);
                return false;
            }
        });
    }

    private void setupRecyclerView(String search) {
        Query query;
        if (search.isEmpty()) {
            query = db.collection("faculty_directory").orderBy("name");
        } else {
            // Simple prefix search
            query = db.collection("faculty_directory")
                    .orderBy("name")
                    .startAt(search)
                    .endAt(search + "\uf8ff");
        }

        FirestoreRecyclerOptions<Faculty> options = new FirestoreRecyclerOptions.Builder<Faculty>()
                .setQuery(query, Faculty.class)
                .build();

        if (adapter != null) adapter.stopListening();

        adapter = new FacultyAdapter(options, this);
        adapter.setOnItemClickListener(faculty -> {
            Intent intent = new Intent(FacultyDirectoryActivity.this, FacultyDetailsActivity.class);
            intent.putExtra("NAME", faculty.getName());
            intent.putExtra("DESIGNATION", faculty.getDesignation());
            intent.putExtra("EMAIL", faculty.getEmail());
            intent.putExtra("CABIN", faculty.getCabinNumber());
            intent.putExtra("IMAGE", faculty.getImageUrl());
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    protected void onStart() { super.onStart(); if(adapter!=null) adapter.startListening(); }
    @Override
    protected void onStop() { super.onStop(); if(adapter!=null) adapter.stopListening(); }
}