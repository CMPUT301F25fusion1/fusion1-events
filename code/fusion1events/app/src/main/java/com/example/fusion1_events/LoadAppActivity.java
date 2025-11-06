package com.example.fusion1_events;

import android.content.Intent;
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
                        String device_id = profile.getString("device_id");

                        if (role.equals("ENTRANT")) {
                            Intent intent = new Intent(this, EntrantHomeActivity.class);
                            intent.putExtra("device_id", device_id);
                            startActivity(intent);
                            finish();
                        }

                        if (role.equals("ORGANIZER")) {
                            Intent intent = new Intent(this, OrganizerHomeActivity.class);
                            intent.putExtra("device_id", device_id);
                            startActivity(intent);
                            finish();
                        }

                        if (role.equals("ADMIN")) {
                            Intent intent = new Intent(this, AdminHomeActivity.class);
                            intent.putExtra("device_id", device_id);
                            startActivity(intent);
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
