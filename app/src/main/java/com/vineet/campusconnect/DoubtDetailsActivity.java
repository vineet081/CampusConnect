package com.vineet.campusconnect;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.vineet.campusconnect.adapters.CommentAdapter;
import com.vineet.campusconnect.models.Comment;

import java.util.UUID;

public class DoubtDetailsActivity extends AppCompatActivity {

    private String doubtId;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private CommentAdapter adapter;

    // UI Elements
    private TextView tvTitle, tvAuthor, tvDescription, tvMembers;
    private Chip chipTag, chipSolved;
    private MaterialCardView cardImage;
    private ImageView ivImage;
    private RecyclerView commentsRecyclerView;
    private EditText etComment;
    private RelativeLayout layoutReplyPreview;
    private ImageView ivReplyPreview;

    // Reply Image Data
    private Uri replyImageUri = null;
    private ActivityResultLauncher<String> replyImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doubt_details);

        doubtId = getIntent().getStringExtra("DOUBT_ID");
        if (doubtId == null) { finish(); return; }

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

        initializeViews();
        loadDoubtDetails();
        setupCommentsRecyclerView();
        setupReplyImagePicker();
    }

    private void initializeViews() {
        tvTitle = findViewById(R.id.tv_details_title);
        tvAuthor = findViewById(R.id.tv_details_author);
        tvDescription = findViewById(R.id.tv_details_description);
        chipTag = findViewById(R.id.chip_details_tag);
        chipSolved = findViewById(R.id.chip_details_solved);
        cardImage = findViewById(R.id.card_doubt_image);
        ivImage = findViewById(R.id.iv_doubt_image);
        commentsRecyclerView = findViewById(R.id.recycler_view_comments);
        etComment = findViewById(R.id.et_comment);

        // Reply Image Views
        layoutReplyPreview = findViewById(R.id.layout_reply_image_preview);
        ivReplyPreview = findViewById(R.id.iv_reply_image_preview);
        ImageButton btnCloseReply = findViewById(R.id.btn_close_reply_image);
        ImageButton btnAttachReply = findViewById(R.id.btn_attach_reply_image);
        ImageButton btnSend = findViewById(R.id.btn_send_comment);

        btnAttachReply.setOnClickListener(v -> replyImageLauncher.launch("image/*"));
        btnCloseReply.setOnClickListener(v -> clearReplyImage());
        btnSend.setOnClickListener(v -> postComment());
    }

    private void loadDoubtDetails() {
        db.collection("doubts").document(doubtId).get().addOnSuccessListener(document -> {
            if (document.exists()) {
                tvTitle.setText(document.getString("title"));
                tvDescription.setText(document.getString("description"));
                chipTag.setText(document.getString("tag"));
                tvAuthor.setText("Asked by " + document.getString("authorName"));

                if (Boolean.TRUE.equals(document.getBoolean("isSolved"))) {
                    chipSolved.setVisibility(View.VISIBLE);
                }

                // Load Image using Glide if it exists
                String imageUrl = document.getString("imageUrl");
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    cardImage.setVisibility(View.VISIBLE);
                    Glide.with(this).load(imageUrl).into(ivImage);
                }
            }
        });
    }

    private void setupCommentsRecyclerView() {
        // Comments are a SUBCOLLECTION of the specific doubt
        Query query = db.collection("doubts").document(doubtId)
                .collection("comments")
                .orderBy("timestamp", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Comment> options = new FirestoreRecyclerOptions.Builder<Comment>()
                .setQuery(query, Comment.class)
                .build();

        adapter = new CommentAdapter(options);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentsRecyclerView.setAdapter(adapter);
    }

    // --- Reply Logic ---

    private void setupReplyImagePicker() {
        replyImageLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                replyImageUri = uri;
                layoutReplyPreview.setVisibility(View.VISIBLE);
                ivReplyPreview.setImageURI(uri);
            }
        });
    }

    private void clearReplyImage() {
        replyImageUri = null;
        layoutReplyPreview.setVisibility(View.GONE);
    }

    private void postComment() {
        String text = etComment.getText().toString().trim();
        if (TextUtils.isEmpty(text) && replyImageUri == null) return;

        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "Please login to answer", Toast.LENGTH_SHORT).show();
            return;
        }

        etComment.setText(""); // Clear input immediately
        Toast.makeText(this, "Posting answer...", Toast.LENGTH_SHORT).show();

        if (replyImageUri != null) {
            uploadReplyImage(text);
        } else {
            saveCommentToFirestore(text, null);
        }
    }

    private void uploadReplyImage(String text) {
        String filename = UUID.randomUUID().toString();
        StorageReference ref = storage.getReference().child("comment_images/" + filename);
        ref.putFile(replyImageUri).addOnSuccessListener(taskSnapshot -> {
            ref.getDownloadUrl().addOnSuccessListener(uri -> {
                saveCommentToFirestore(text, uri.toString());
                clearReplyImage();
            });
        });
    }

    private void saveCommentToFirestore(String text, String imageUrl) {
        // Note: You might need to update Comment.java to include 'imageUrl' if you want to support it in comments too!
        // For now, we will just append the URL to the text if an image exists, for simplicity.
        String finalText = text;
        if (imageUrl != null) {
            finalText += "\n\n[Image Attachment]: " + imageUrl;
        }

        Comment comment = new Comment(finalText, mAuth.getCurrentUser().getUid(), "Anonymous User");
        // OPTIONAL: Fetch real user name here if you want, like we did in AskDoubtActivity

        db.collection("doubts").document(doubtId)
                .collection("comments")
                .add(comment)
                .addOnSuccessListener(docRef -> {
                    // Increment the answer count on the main doubt document
                    db.collection("doubts").document(doubtId)
                            .update("answerCount", FieldValue.increment(1));
                });
    }

    @Override
    protected void onStart() { super.onStart(); adapter.startListening(); }
    @Override
    protected void onStop() { super.onStop(); adapter.stopListening(); }
}