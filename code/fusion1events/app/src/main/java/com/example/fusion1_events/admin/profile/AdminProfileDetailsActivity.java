package com.example.fusion1_events.admin.profile;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fusion1_events.R;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Activity that displays the full details of a single profile.
 * <p>
 * Retrieves the profile document from Firestore using its document ID and populates UI fields
 * with the name, email, role, and phone number.
 */
public class AdminProfileDetailsActivity extends AppCompatActivity {
    private TextView tvName, tvEmail, tvRole, tvPhone;
    private ImageButton buttonBack;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * Initializes the activity, binds UI components, and loads profile details.
     *
     * @param savedInstanceState Bundle containing activity's previously saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_profile_details);

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
     * Loads the  profile from Firestore by document ID and populates the UI.
     * <p>
     * If the profile's phone number is missing, displays "N/A" instead.
     *
     * @param id the Firestore document ID of the profile to load
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
                            String phone = profile.getNumber();
                            tvPhone.setText(phone != null && !phone.isEmpty() ? phone : "N/A");
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error loading profile", e));
    }
}
