package com.example.fusion1_events;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.installations.FirebaseInstallations;

/*
* File: LoadAppActivity.java
*
* Role:
* - To load the device_id for the device and check if there is any profile linked to it.
* - The loading screen shows the logo of the app for few seconds for visual effect.
* - If there is a profile linked to it, start a Home Screen Activity as per their role.
* - If there isn't a profile linked to it, start the SignUpActivity prompting
*       the user to create a account
*
* Issues:
* - The app assumes a connection to the firebase, i.e. no exception thrown if device is offline.
*
 */
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
                        profileExists(profile);

                    } else {
                        profileDoesNotExist();
                    }
                });


            });
        }, 2000);
    }

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

    public void profileDoesNotExist(){
        startActivity(new android.content.Intent(this, SignUpActivity.class));
        finish();
    }
}
