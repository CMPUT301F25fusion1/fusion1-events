package com.example.fusion1_events;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Browse_Class extends AppCompatActivity {

    private RecyclerView recyclerViewProfiles;
    private EditText searchProfiles;
    private ProfileAdapter profileAdapter;
    private List<Profile> allProfiles = new ArrayList<>();

    private CollectionReference entrantsRef;
    private CollectionReference organizersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_browse);

        recyclerViewProfiles = findViewById(R.id.recyclerViewProfiles);
        searchProfiles = findViewById(R.id.searchProfiles);

        recyclerViewProfiles.setLayoutManager(new LinearLayoutManager(this));
        profileAdapter = new ProfileAdapter(this, new ArrayList<>());
        recyclerViewProfiles.setAdapter(profileAdapter);

        entrantsRef = DatabaseReferences.getEntrantsDatabase();
        organizersRef = DatabaseReferences.getOrganizersDatabase();

        loadProfilesFromFirebase();

        // Search functionality
        searchProfiles.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProfiles(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadProfilesFromFirebase() {
        allProfiles.clear();

        // Load Entrants
        entrantsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                addProfilesFromSnapshot(task.getResult(), "ENTRANT");
            } else {
                Toast.makeText(this, "Failed to load Entrants", Toast.LENGTH_SHORT).show();
            }
        });

        // Load Organizers
        organizersRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                addProfilesFromSnapshot(task.getResult(), "ORGANIZER");
            } else {
                Toast.makeText(this, "Failed to load Organizers", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addProfilesFromSnapshot(QuerySnapshot snapshot, String defaultRole) {
        for (DocumentSnapshot doc : snapshot.getDocuments()) {
            String name = doc.getString("name");
            String email = doc.getString("email");
            String phone = doc.getString("number");
            String role = doc.getString("role") != null ? doc.getString("role") : defaultRole;
            String deviceId = doc.getString("device_id");

            Profile profile = new Profile(name, email, phone, role, deviceId);
            allProfiles.add(profile);
        }

        profileAdapter.updateProfiles(new ArrayList<>(allProfiles));
    }

    private void filterProfiles(String query) {
        List<Profile> filteredList = new ArrayList<>();
        for (Profile profile : allProfiles) {
            if (profile.getName().toLowerCase().contains(query.toLowerCase()) ||
                    profile.getRole().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(profile);
            }
        }
        profileAdapter.updateProfiles(filteredList);
    }
}
