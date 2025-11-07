package com.example.fusion1_events;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Activity that displays detailed information about a single event to an admin.
 * <p>
 * Shows the event image, title, description, registration start and end dates,
 * and the number of attendees. Admins can also delete the event from this screen.
 */
public class AdminEventDetailsActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private ImageView ivEventImage;
    private TextView tvEventTitle, tvDescription, tvRegStart, tvRegEnd, tvAttendees;
    private Button buttonBack, buttonDelete;

    private String eventId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_event_details);

        ivEventImage = findViewById(R.id.ivEventImage);
        tvEventTitle = findViewById(R.id.tvEventTitle);
        tvDescription = findViewById(R.id.tvDescription);
        tvRegStart = findViewById(R.id.tvRegStart);
        tvRegEnd = findViewById(R.id.tvRegEnd);
        tvAttendees = findViewById(R.id.tvAttendees);
        buttonBack = findViewById(R.id.buttonBack);
        buttonDelete = findViewById(R.id.buttonDeleteEvent);

        buttonBack.setOnClickListener(v -> finish());

        // Get event ID passed via Intent
        eventId = getIntent().getStringExtra("eventId");
        if (eventId == null || eventId.isEmpty()) {
            Log.e("Firestore", "Missing event ID");
            finish();
            return;
        }

        loadEventDetails();

        // Delete button
        buttonDelete.setOnClickListener(v -> onDeleteEvent());
    }

    /**
     * Loads event details from Firestore based on eventId.
     */
    private void loadEventDetails() {
        db.collection("Events").document(eventId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        displayEvent(documentSnapshot);
                    } else {
                        Log.e("Firestore", "Event not found");
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error loading event", e);
                    finish();
                });
    }

    /**
     * Displays event details in the UI.
     *
     * @param doc the Firestore document containing the event data
     */
    private void displayEvent(DocumentSnapshot doc) {
        String title = doc.getString("title");
        String desc = doc.getString("description");
        Timestamp start = doc.getTimestamp("registration_start");
        Timestamp end = doc.getTimestamp("registration_end");
        Long attendees = doc.getLong("attendees");
        String imageUrl = doc.getString("imageUrl");

        tvEventTitle.setText(title);
        tvDescription.setText(desc != null ? desc : "No description provided.");

        SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());
        tvRegStart.setText(start != null ? sdf.format(start.toDate()) : "N/A");
        tvRegEnd.setText(end != null ? sdf.format(end.toDate()) : "N/A");
        tvAttendees.setText(attendees != null ? String.valueOf(attendees) : "0");

        // TODO: image
    }

    /**
     * Deletes the current event from Firestore and finishes the activity.
     */
    private void onDeleteEvent() {
        db.collection("Events").document(eventId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Event deleted successfully");
                    finish();
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error deleting event", e));
    }
}