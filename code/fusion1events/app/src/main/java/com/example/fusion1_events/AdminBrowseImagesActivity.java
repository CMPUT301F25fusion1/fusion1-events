package com.example.fusion1_events;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminBrowseImagesActivity extends AppCompatActivity implements ImageAdapter.onImageActionListener {
    private RecyclerView recyclerView;
    private ImageAdapter adapter;
    private List<Image> images = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_browse_images);

        recyclerView = findViewById(R.id.recyclerImages);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new ImageAdapter(images, this);
        recyclerView.setAdapter(adapter);

        NavBarHelper.setupNavBar(this, AdminBrowseImagesActivity.class);

        loadImages();

        // back button
        Button backButton = findViewById(R.id.buttonBack);
        backButton.setOnClickListener(v -> finish());
    }

    private void loadImages() {
        db.collection("Images")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    images.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Image image = doc.toObject(Image.class);
                        image.setId(doc.getId());
                        images.add(image);
                    }
                    adapter.notifyDataSetChanged();
                    Log.d("Firestore", "Images loaded successfully");
                })
                .addOnFailureListener(e ->
                        Log.e("Firestore", "Error loading images", e));
    }

    @Override
    public void onDeleteImage(Image image) {
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
