package com.example.fusion1_events;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.installations.FirebaseInstallations;

public class LoadAppActivity extends AppCompatActivity {

    private CollectionReference profileRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_load_screen);

        profileRef = DatabaseReferences.getProfileDatabase();

        new android.os.Handler().postDelayed(() -> {

            FirebaseInstallations.getInstance().getId().addOnSuccessListener(deviceId -> {

                profileRef.document(deviceId).get().addOnSuccessListener(profile -> {

                    if (profile.exists()) {
                        String role = profile.getString("role");

                        if (role.equals("ENTRANT")) {
                            startActivity(new android.content.Intent(this, EntrantHomeActivity.class));
                            finish();
                        }
                        if (role.equals("ORGANIZER")) {
                            startActivity(new android.content.Intent(this, OrganizerHomeActivity.class));
                            finish();
                        }
                        if (role.equals("ADMIN")) {
                            startActivity(new android.content.Intent(this, AdminHomeActivity.class));
                            finish();
                        }
                    } else {
                        startActivity(new android.content.Intent(this, SignUpActivity.class));
                        finish();
                    }
                });


            });
        }, 2000);
    }
}
