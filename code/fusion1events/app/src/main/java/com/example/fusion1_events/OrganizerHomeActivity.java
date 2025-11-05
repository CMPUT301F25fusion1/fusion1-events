package com.example.fusion1_events;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.installations.FirebaseInstallations;

public class OrganizerHomeActivity extends AppCompatActivity {

    private CollectionReference profileRef;

    private Button profileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_organizer_home);

        profileRef = DatabaseReferences.getProfileDatabase();

        FirebaseInstallations.getInstance().getId().addOnSuccessListener(deviceId -> {

            profileRef.document(deviceId).get().addOnSuccessListener(profile -> {

                if (profile.exists()) {
                    String name = profile.getString("name");

                    TextView welcomeMessage = findViewById(R.id.welcomeText);

                    welcomeMessage.setText("Welcome "+ name);

                }
            });

        });

        profileButton = findViewById(R.id.buttonProfileOrganizerHome);
        profileButton.setOnClickListener(v -> goProfileScreen());
    }

    private void goProfileScreen(){
        startActivity(new android.content.Intent(this, ProfileViewActivity.class));
        finish();
    }
}
