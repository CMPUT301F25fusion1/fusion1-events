package com.example.fusion1_events.admin;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fusion1_events.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Activity that allows an admin to browse all images stored in Firestore.
 */
public class AdminBrowseImagesActivity extends AppCompatActivity implements AdminImageAdapter.onImageActionListener {
    private RecyclerView recyclerView;
    private AdminImageAdapter adapter;
    private List<AdminImage> images = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_browse_images);

        recyclerView = findViewById(R.id.recyclerImages);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new AdminImageAdapter(images, this);
        recyclerView.setAdapter(adapter);

        NavBarHelper.setupNavBar(this, AdminBrowseImagesActivity.class);

        loadImages();

        // back button
        ImageButton backButton = findViewById(R.id.buttonBack);
        backButton.setOnClickListener(v -> finish());

        // Listen for real-time updates to the "Events" collection concerning images.
        db.collection("Events").addSnapshotListener((docs, e) -> {
            if (docs != null) {
                images.clear();

                for (DocumentSnapshot doc : docs.getDocuments()) {
                    String imageUrl = doc.getString("imageUrl");
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        String eventTitle = doc.getString("title");
                        AdminImage image = new AdminImage(doc.getId(), imageUrl, eventTitle);
                        image.setRegistration_end(doc.getTimestamp("registration_end"));
                        images.add(image);
                    }
                }

                adapter.notifyDataSetChanged();
                Log.d("FirestoreDebug", "Images loaded successfully");
            }
        });
    }

    /**
     * Loads all images from the Firestore "Images" collection and populates the RecyclerView.
     * <p>
     * Clears the existing list, converts Firestore documents to Image objects, and notifies
     * the adapter to refresh the view.
     */

    private void loadImages() {
        db.collection("Events")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    images.clear();

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String imageUrl = doc.getString("imageUrl");
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            String eventTitle = doc.getString("title"); // or whatever field you have
                            images.add(new AdminImage(doc.getId(), imageUrl, eventTitle));
                        }
                    }
                    adapter.notifyDataSetChanged();
                    Log.d("Firestore", "Images loaded successfully");
                })
                .addOnFailureListener(e ->
                        Log.e("Firestore", "Error loading images", e));
    }

    /**
     * Deletes the specified image from Firestore and removes it from the RecyclerView.
     * <p>
     * Triggered from the ImageAdapter when the admin requests to delete an image.
     *
     * @param image The Image object to delete.
     */
    @Override
    public void onDeleteImage(AdminImage image) {
        db.collection("Events").document(image.getEventId())
                .update("imageUrl", null)
                .addOnSuccessListener(aVoid -> {
                    images.remove(image);
                    adapter.notifyDataSetChanged();
                    String successMessage = "Image deleted successfully";
                    Log.d("FirestoreDebug", successMessage);
                    Toast.makeText(this, successMessage, Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    String failureMessage = "Error deleting image";
                    Log.d("FirestoreDebug", failureMessage);
                    Toast.makeText(this, failureMessage, Toast.LENGTH_SHORT).show();
                });
    }
}
