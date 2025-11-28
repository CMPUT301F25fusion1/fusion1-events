package com.example.fusion1_events.admin;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fusion1_events.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity that allows an admin to browse all images stored in Firestore.
 */
public class AdminBrowseImagesActivity extends AppCompatActivity implements AdminImageAdapter.onImageActionListener {
    private RecyclerView recyclerView;
    private AdminImageAdapter adapter;
    private List<Admin> images = new ArrayList<>();
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
        Button backButton = findViewById(R.id.buttonBack);
        backButton.setOnClickListener(v -> finish());
    }

    /**
     * Loads all images from the Firestore "Images" collection and populates the RecyclerView.
     * <p>
     * Clears the existing list, converts Firestore documents to Image objects, and notifies
     * the adapter to refresh the view.
     */

    private void loadImages() {
        db.collection("Images")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    images.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Admin image = doc.toObject(Admin.class);
                        image.setId(doc.getId());
                        images.add(image);
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
    public void onDeleteImage(Admin image) {
        String id = image.getId();
        if (id == null) return;

        db.collection("Images").document(id)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    images.remove(image);
                    adapter.notifyDataSetChanged();
                    Log.d("Firestore", "Image deleted successfully");
                })
                .addOnFailureListener(e ->
                        Log.e("Firestore", "Error deleting image", e));
    }
}
