package com.example.fusion1_events;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.installations.FirebaseInstallations;


public class AdminHomeActivity extends AppCompatActivity {

    private CollectionReference profileRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_home);


        profileRef = DatabaseReferences.getProfileDatabase();

        TextView welcomeMessage = findViewById(R.id.welcomeText);
        //Button buttonBrowseProfiles = findViewById(R.id.buttonBrowseProfiles);
        FirebaseInstallations.getInstance().getId().addOnSuccessListener(deviceId -> {
            profileRef.document(deviceId).get().addOnSuccessListener(profile -> {

                if (profile.exists()) {
                    String name = profile.getString("name");
                    welcomeMessage.setText("Welcome "+ name);
                }
            });
        });
    }
}
