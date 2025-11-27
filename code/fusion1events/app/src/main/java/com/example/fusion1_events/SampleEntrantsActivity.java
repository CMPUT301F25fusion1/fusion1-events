package com.example.fusion1_events;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Role:
 * - Lets organizer choose how many entrants to sample for an event.
 * - Handles all Firestore reads/writes for sampling.
 * Testability:
 * - Core logic (sampling, validation) isolated into helper methods for unit tests.
 */

public class SampleEntrantsActivity extends AppCompatActivity {

    private static final String TAG = "SampleEntrantsActivity";

    private FirebaseFirestore db;
    private String eventId;

    private EditText sampleNumberInput;
    private Button sampleButton, homeButton;
    private TextView titleText;

    private List<String> waitingList = new ArrayList<>();
    private List<String> invitedList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_entrants);

        db = FirebaseFirestore.getInstance();
        eventId = getIntent().getStringExtra("eventId");

        sampleNumberInput = findViewById(R.id.sampleNumberInput);
        sampleButton = findViewById(R.id.sampleButton);
        homeButton = findViewById(R.id.buttonHome);
        titleText = findViewById(R.id.textTitle);

        titleText.setText("Select Entrants");

        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, OrganizerHomeActivity.class);
            startActivity(intent);
            finish();
        });

        sampleButton.setOnClickListener(v -> {
            String input = sampleNumberInput.getText().toString().trim();
            if (input.isEmpty()) {
                Toast.makeText(this, "Enter a sample size", Toast.LENGTH_SHORT).show();
                return;
            }

            int sampleSize = Integer.parseInt(input);
            loadAndSampleEntrants(sampleSize);
        });
    }

    /**
     * Fetch waiting list and perform sampling.
     */
    private void loadAndSampleEntrants(int sampleSize) {
        if (eventId == null || eventId.isEmpty()) {
            Toast.makeText(this, "Invalid event ID", Toast.LENGTH_SHORT).show();
            return;
        }

        DocumentReference eventRef = db.collection("Events").document(eventId);
        eventRef.get().addOnSuccessListener(doc -> {
            if (!doc.exists()) {
                Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show();
                return;
            }

            Object waitingObj = doc.get("waitingList");
            if (waitingObj instanceof List) {
                waitingList = (List<String>) waitingObj;
            } else {
                waitingList = new ArrayList<>();
            }

            Object finalObj = doc.get("invitedList");
            if (finalObj instanceof List) {
                invitedList = (List<String>) finalObj;
            } else {
                invitedList = new ArrayList<>();
            }

            List<String> sampledEntrants = sampleEntrants(waitingList, sampleSize);
            UpdateInvitedListInFirestore(eventRef, sampledEntrants);
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error fetching event", e);
            Toast.makeText(this, "Error loading event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Core sampling logic — easy to test.
     */
    public List<String> sampleEntrants(List<String> waitingList, int sampleSize) {
        List<String> result = new ArrayList<>();

        if (waitingList == null || waitingList.isEmpty()) {
            Log.d(TAG, "No entrants to sample from");
            return result;
        }

        if (sampleSize <= 0) {
            Log.d(TAG, "Invalid sample size: " + sampleSize);
            return result;
        }

        if (sampleSize >= waitingList.size()) {
            Log.d(TAG, "Sample size >= waiting list size → everyone selected");
            result.addAll(waitingList);
            return result;
        }

        List<String> shuffled = new ArrayList<>(waitingList);
        Collections.shuffle(shuffled);
        result.addAll(shuffled.subList(0, sampleSize));
        return result;
    }

    /**
     * Updates Firestore with sampled entrants.
     */
    private void UpdateInvitedListInFirestore(DocumentReference eventRef, List<String> sampled) {
        if (sampled.isEmpty()) {
            Toast.makeText(this, "No entrants sampled.", Toast.LENGTH_SHORT).show();
            return;
        }

        eventRef.update("invitedList", sampled)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Sampled " + sampled.size() + " entrants", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to update sampled list", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error updating invitedList", e);
                });
    }
}
