package com.vineet.campusconnect;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.vineet.campusconnect.adapters.DoubtAdapter;
import com.vineet.campusconnect.models.Doubt;

public class DoubtFeedActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DoubtAdapter adapter;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doubt_feed);

        // 1. Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_doubt_feed);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        // 2. Initialize Firebase & View
        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.recycler_view_doubts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 3. Setup FAB
        ExtendedFloatingActionButton fab = findViewById(R.id.fab_ask_doubt);
        // In DoubtFeedActivity.java
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(DoubtFeedActivity.this, AskDoubtActivity.class);
            startActivity(intent);
        });
        // 4. Setup RecyclerView (Query: All doubts, newest first)
        Query query = db.collection("doubts")
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Doubt> options = new FirestoreRecyclerOptions.Builder<Doubt>()
                .setQuery(query, Doubt.class)
                .build();

        adapter = new DoubtAdapter(options);

        // Handle clicks on doubts
        // In DoubtFeedActivity.java onCreate()
        adapter.setOnDoubtClickListener(doubtId -> {
            Intent intent = new Intent(DoubtFeedActivity.this, DoubtDetailsActivity.class);
            intent.putExtra("DOUBT_ID", doubtId);
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);
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