package com.example.fusion1_events;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.installations.FirebaseInstallations;

public class SampleEntrantsActivity extends AppCompatActivity {

    private static final String TAG = "SampleEntrantsActivity";
    private FirebaseFirestore db;
    private String eventId;
    private String organizerId;
    private Button drawButton, homeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_entrants);

        db = FirebaseFirestore.getInstance();
        eventId = getIntent().getStringExtra("eventId");
        FirebaseInstallations.getInstance().getId().addOnSuccessListener(id -> {
            organizerId = id;
        });

        drawButton = findViewById(R.id.drawButton);
        homeButton = findViewById(R.id.buttonHome);


        drawButton.setOnClickListener(v -> {
            DrawHelper.runDraw(eventId, organizerId, db, this);

        });

        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, OrganizerHomeActivity.class);
            startActivity(intent);
            finish();
        });
    }}

