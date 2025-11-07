package com.example.fusion1_events.admin;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fusion1_events.R;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Activity that displays the details of an admin profile.
 * <p>
 * Loads profile information from Firestore and shows name, email, role, and phone number.
 */
public class AdminProfileDetailsActivity extends AppCompatActivity {
    private TextView tvName, tvEmail, tvRole, tvPhone;
    private Button buttonBack;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_details);

        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvRole = findViewById(R.id.tvRole);
        tvPhone = findViewById(R.id.tvPhone);
        buttonBack = findViewById(R.id.buttonBack);

        buttonBack.setOnClickListener(v -> finish());

        String profileId = getIntent().getStringExtra("profile_id");
        if (profileId != null) loadProfile(profileId);
    }

    /**
     * Loads the admin profile from Firestore by ID and displays the information.
     *
     * @param id the Firestore document ID of the profile
     */
    private void loadProfile(String id) {
        db.collection("Profile").document(id)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        AdminProfile profile = documentSnapshot.toObject(AdminProfile.class);
                        if (profile != null) {
                            tvName.setText(profile.getName());
                            tvEmail.setText(profile.getEmail());
                            tvRole.setText(profile.getRole());
                            tvPhone.setText(profile.getNumber());
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error loading profile", e));
    }
}
