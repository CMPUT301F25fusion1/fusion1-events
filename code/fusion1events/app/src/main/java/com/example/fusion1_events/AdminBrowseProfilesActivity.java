package com.example.fusion1_events;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminBrowseProfilesActivity extends AppCompatActivity implements ProfileAdapter.onProfileActionListener {
    private RecyclerView recyclerView;
    private ProfileAdapter adapter;
    private List<AdminProfile> profiles = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_browse_profiles);

        recyclerView = findViewById(R.id.recyclerProfiles);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProfileAdapter(profiles, this);
        recyclerView.setAdapter(adapter);

        NavBarHelper.setupNavBar(this, AdminBrowseProfilesActivity.class);

        loadProfiles();

        // back button
        Button backButton = findViewById(R.id.buttonBack);
        backButton.setOnClickListener(v -> finish());
    }

    private void loadProfiles() {
        db.collection("Profile")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    profiles.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        AdminProfile profile = doc.toObject(AdminProfile.class);
                        profile.setId(doc.getId());
                        profiles.add(profile);
                    }
                    adapter.notifyDataSetChanged();
                    Log.d("Firestore", "Profiles loaded successfully");
                })
                .addOnFailureListener(e ->
                        Log.e("Firestore", "Error loading profiles", e));
    }

    @Override
    public void onDeleteProfile(AdminProfile profile) {
        String profileId = profile.getId();
        if (profileId == null) return;

        db.collection("Profile").document(profileId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    profiles.remove(profile);
                    adapter.notifyDataSetChanged();
                    Log.d("Firestore", "Profile deleted successfully");
                })
                .addOnFailureListener(e ->
                        Log.e("Firestore", "Error deleting profile"));
    }

    @Override
    public void onViewProfile(AdminProfile profile) {
        Intent intent = new Intent(this, AdminProfileDetailsActivity.class);
        intent.putExtra("profile_id", profile.getId());
        startActivity(intent);
    }

}