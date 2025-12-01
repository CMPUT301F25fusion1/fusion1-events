package com.example.fusion1_events.admin.image;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fusion1_events.R;
import com.example.fusion1_events.admin.NavBarHelper;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity that allows an admin to browse and delete all images stored in Firestore.
 * <p>
 * Features:
 * <ul>
 *     <li>Displays images in a RecyclerView.</li>
 *     <li>Shows real-time updates using Firestore snapshot listeners.</li>
 *     <li>Includes a back button to return to the previous screen.</li>
 * </ul>
 */
public class AdminBrowseImagesActivity extends AppCompatActivity implements AdminImageAdapter.onImageActionListener {
    public static final String TAG = "FirestoreDebugBrowseImages";
    private RecyclerView recyclerView;
    private AdminImageAdapter adapter;
    private List<AdminImage> images = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * Initializes the activity.
     * <p>
     * Sets up the RecyclerView, adapter, navigation bar, back button.
     * Loads images from Firestore, and listens for real-time updates.
     *
     * @param savedInstanceState Bundle containing activity's previously saved state
     */
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
                Log.d(TAG, "Images loaded successfully");
            }
        });
    }

    /**
     * Loads all images from Firestore and populates the RecyclerView.
     * <p>
     * Clears the existing list, converts Firestore documents into {@link AdminImage} objects,
     * and refreshes the adapter.
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
                    Log.d(TAG, "Images loaded successfully");
                })
                .addOnFailureListener(e ->
                        Log.e(TAG, "Error loading images", e));
    }

    /**
     * Deletes the specified image from Firestore and removes it from the RecyclerView.
     * <p>
     * Triggered from the AdminImageAdapter when the admin requests to delete an image.
     *
     * @param image The AdminImage object to delete.
     */
    @Override
    public void onDeleteImage(AdminImage image) {
        db.collection("Events").document(image.getEventId())
                .update("imageUrl", null)
                .addOnSuccessListener(aVoid -> {
                    images.remove(image);
                    adapter.notifyDataSetChanged();
                    String successMessage = "Image deleted successfully";
                    Log.d(TAG, successMessage);
                    Toast.makeText(this, successMessage, Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    String failureMessage = "Error deleting image";
                    Log.d(TAG, failureMessage);
                    Toast.makeText(this, failureMessage, Toast.LENGTH_SHORT).show();
                });
    }
}
