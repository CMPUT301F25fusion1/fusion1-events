package com.example.fusion1_events;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.installations.FirebaseInstallations;

/**
 * Activity that allows organizers to trigger a draw that selects entrants
 * from an event's waiting list. This screen contains a button to run the draw
 * and another button to return to the organizer home page.
 * draw logic to {@link DrawHelper#runDraw(String, String, FirebaseFirestore, android.content.Context)}.
 */
public class SampleEntrantsActivity extends AppCompatActivity {

    private static final String TAG = "SampleEntrantsActivity";
    private FirebaseFirestore db;
    private String eventId;
    private String organizerId;
    private Button drawButton, homeButton;

    /**
     * Initializes the activity, retrieves the event ID, and sets up button listeners
     * for running the draw and navigating home.
     * @param savedInstanceState The previously saved instance state, if any.
     */
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


