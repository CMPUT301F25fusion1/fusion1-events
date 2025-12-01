package com.example.fusion1_events;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fusion1_events.admin.AdminHomeActivity;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.installations.FirebaseInstallations;

/**
 *File: LoadAppActivity.java
 * Role:<br>
 * - Loads the device_id for the current device and checks if a profile is linked to it.<br>
 * - Displays the app logo for a few seconds as a loading/intro screen.<br>
 * - If a profile exists, starts the appropriate home screen activity based on the user's role.<br>
 * - If no profile is found, starts the SignUpActivity to allow the user to create an account.<br>
 * <br>
 * Issues:<br>
 * - Assumes the device is online; no handling for Firebase access failures when offline.<br>
 */
public class LoadAppActivity extends AppCompatActivity {

    private CollectionReference profileRef;
    /**
     * Called when the activity is first created.
     * Initializes the splash-screen layout, retrieves the device ID,
     * and checks Firestore for an associated profile after a short delay.
     *
     * @param savedInstanceState Previously saved state, or null if none.
     */

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
                        profileExists(profile);

                    } else {
                        profileDoesNotExist();
                    }
                });


            });
        }, 2000);
    }

    /**
     * Handles the case where a profile document exists for this device.
     * Redirects the user to the appropriate home screen depending on their role:
     * ENTANT, ORGANIZER, or ADMIN.
     *
     * @param profile The Firestore document representing the user's profile.
     */
    public void profileExists(DocumentSnapshot profile){

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

    }

    /**
     * Handles the case where no profile exists for this device.
     * Redirects the user to the SignUpActivity so they can create an account.
     */
    public void profileDoesNotExist(){
        startActivity(new android.content.Intent(this, SignUpActivity.class));
        finish();
    }
}
