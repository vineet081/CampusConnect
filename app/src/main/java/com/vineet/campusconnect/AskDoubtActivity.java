package com.vineet.campusconnect;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.vineet.campusconnect.models.Doubt;

import java.util.UUID;

public class AskDoubtActivity extends AppCompatActivity {

    // UI Elements
    TextInputEditText etTitle, etDescription;
    ChipGroup chipGroupTags;
    MaterialButton btnAttachImage, btnPostDoubt;
    ImageView ivImagePreview;

    // Firebase
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    FirebaseStorage storage;
    FirebaseUser currentUser;
    String currentUserName = "Anonymous";

    // Image Data
    Uri selectedImageUri = null;
    ActivityResultLauncher<String> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_doubt);

        // 1. Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        currentUser = mAuth.getCurrentUser();
        loadUserName(); // Get the user's real name

        // 2. Find UI Elements
        etTitle = findViewById(R.id.et_doubt_title);
        etDescription = findViewById(R.id.et_doubt_description);
        chipGroupTags = findViewById(R.id.chip_group_tags);
        btnAttachImage = findViewById(R.id.btn_attach_image);
        btnPostDoubt = findViewById(R.id.btn_post_doubt);
        ivImagePreview = findViewById(R.id.iv_doubt_image_preview);

        // 3. Setup Image Picker
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        selectedImageUri = uri;
                        ivImagePreview.setVisibility(View.VISIBLE);
                        ivImagePreview.setImageURI(uri);
                        btnAttachImage.setText("Change Image");
                    }
                });

        btnAttachImage.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));

        // 4. Post Doubt Click
        btnPostDoubt.setOnClickListener(v -> startPosting());
    }

    private void loadUserName() {
        if (currentUser != null) {
            db.collection("users").document(currentUser.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            currentUserName = documentSnapshot.getString("name");
                        }
                    });
        }
    }

    private void startPosting() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        // Validation
        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please add a title and description", Toast.LENGTH_SHORT).show();
            return;
        }
        int selectedChipId = chipGroupTags.getCheckedChipId();
        if (selectedChipId == View.NO_ID) {
            Toast.makeText(this, "Please select a subject tag", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the tag text
        Chip selectedChip = findViewById(selectedChipId);
        String tag = selectedChip.getText().toString();

        btnPostDoubt.setEnabled(false);
        btnPostDoubt.setText("Posting...");

        // Check if we need to upload an image first
        if (selectedImageUri != null) {
            uploadImageAndPost(title, description, tag);
        } else {
            postDoubtToFirestore(title, description, tag, null);
        }
    }

    private void uploadImageAndPost(String title, String description, String tag) {
        // Create a unique filename for the image
        String filename = UUID.randomUUID().toString();
        StorageReference ref = storage.getReference().child("doubt_images/" + filename);

        ref.putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Image uploaded! Now get the download URL
                    ref.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        postDoubtToFirestore(title, description, tag, imageUrl);
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AskDoubtActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                    btnPostDoubt.setEnabled(true);
                    btnPostDoubt.setText("Post Doubt");
                });
    }

    private void postDoubtToFirestore(String title, String description, String tag, String imageUrl) {
        Doubt newDoubt = new Doubt(title, description, tag, currentUser.getUid(), currentUserName, imageUrl);

        db.collection("doubts")
                .add(newDoubt)
                .addOnSuccessListener(documentReference -> {
                    // Save the Document ID back to the document itself (handy for later)
                    documentReference.update("doubtId", documentReference.getId());

                    Toast.makeText(AskDoubtActivity.this, "Doubt Posted!", Toast.LENGTH_SHORT).show();
                    finish(); // Close screen
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AskDoubtActivity.this, "Error posting doubt: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    btnPostDoubt.setEnabled(true);
                    btnPostDoubt.setText("Post Doubt");
                });
    }
}