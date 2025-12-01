package com.example.fusion1_events.admin.profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fusion1_events.R;
import com.example.fusion1_events.admin.NavBarHelper;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Activity that allows an admin to browse, view, and delete user profiles.
 * <p>
 * Displays all profiles stored in Firestore using a RecyclerView with a custom
 * AdminProfileAdapter.
 * Supports deleting profiles and viewing detailed profile information.
 */
public class AdminBrowseProfilesActivity extends AppCompatActivity implements AdminProfileAdapter.onProfileActionListener {
    private RecyclerView recyclerView;
    private AdminProfileAdapter adapter;
    private List<AdminProfile> profiles = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * Initializes the activity.
     * <p>
     * Sets up the RecyclerView, adapter, navigation bar, back button.
     * Loads profiles from Firestore, and listens for real-time updates.
     *
     * @param savedInstanceState Bundle containing activity's previously saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_browse_profiles);

        recyclerView = findViewById(R.id.recyclerProfiles);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminProfileAdapter(profiles, this);
        recyclerView.setAdapter(adapter);

        NavBarHelper.setupNavBar(this, AdminBrowseProfilesActivity.class);

        loadProfiles();

        // back button
        ImageButton backButton = findViewById(R.id.buttonBack);
        backButton.setOnClickListener(v -> finish());

        // Listen for real-time updates to the "Profiles" collection.
        db.collection("Profile").addSnapshotListener((doc, e) -> {
            if (doc != null) {
                profiles.clear();
                List<AdminProfile> newProfiles = doc.getDocuments()
                        .stream()
                        .map(this::docToProfile)
                        .collect(Collectors.toList());

                profiles.addAll(newProfiles);
                Log.d("FirestoreDebug", newProfiles.toString());
                adapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * Loads all profiles from the Firestore "Profile" collection.
     * <p>
     * Clears the current list, converts each document to an AdminProfile object,
     * and updates the RecyclerView adapter.
     */
    private void loadProfiles() {
        db.collection("Profile")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    profiles.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        AdminProfile profile = docToProfile(doc);
                        profiles.add(profile);
                    }
                    adapter.notifyDataSetChanged();
                    Log.d("Firestore", "Profiles loaded successfully");
                })
                .addOnFailureListener(e ->
                {
                    String failureMessage = "Error loading profiles";
                    Log.e("Firestore", failureMessage, e);
                    Toast.makeText(this, failureMessage, Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Deletes the given profile from Firestore and removes it from the RecyclerView.
     *
     * @param profile the profile to delete
     */
    @Override
    public void onDeleteProfile(AdminProfile profile) {
        String profileId = profile.getId();
        if (profileId == null) return;

        db.collection("Profile").document(profileId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    profiles.remove(profile);
                    adapter.notifyDataSetChanged();
                    String successMessage = "Profile deleted successfully";
                    Log.d("Firestore", successMessage);
                    Toast.makeText(this, successMessage, Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e ->
                {
                    String failureMessage = "Error deleting profile";
                    Log.e("Firestore", failureMessage);
                    Toast.makeText(this, failureMessage, Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Opens the detailed view for the given profile.
     *
     * @param profile the profile to view in detail
     */
    @Override
    public void onViewProfile(AdminProfile profile) {
        Intent intent = new Intent(this, AdminProfileDetailsActivity.class);
        intent.putExtra("profile_id", profile.getId());
        startActivity(intent);
    }

    /**
     * Converts a Firestore DocumentSnapshot into an AdminProfile object.
     * <p>
     * Assigns the Firestore document's ID to the AdminProfile.
     *
     * @param doc the Firestore document containing profile data
     * @return an AdminProfile object with populated fields and Firestore ID
     */
    private @NonNull AdminProfile docToProfile(@NonNull DocumentSnapshot doc) {
        AdminProfile profile = doc.toObject(AdminProfile.class);
        profile.setId(doc.getId());
        return profile;
    }

}