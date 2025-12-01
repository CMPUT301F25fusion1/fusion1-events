package com.example.fusion1_events.admin.event;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.fusion1_events.R;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Activity that displays the full details of a single event.
 * <p>
 * Retrieves the event document from Firestore using its document ID and populates UI fields
 * with the title, description, registration start and end dates, number of attendees, image.
 */
public class AdminEventDetailsActivity extends AppCompatActivity {
    public static final String TAG = "FirestoreDebugEventDetails";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private ImageView ivEventImage;
    private TextView tvEventTitle, tvDescription, tvRegStart, tvRegEnd, tvAttendees;
    private Button buttonDelete;
    private ImageButton buttonBack;

    private String eventId;

    /**
     * Initializes the activity, binds UI components, and loads event details.
     *
     * @param savedInstanceState Bundle containing activity's previously saved state
     */
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
            Log.e(TAG, "Missing event ID");
            finish();
            return;
        }

        loadEventDetails();

        // Delete button
        buttonDelete.setOnClickListener(v -> onDeleteEvent());
    }

    /**
     * Loads event details from Firestore based on the eventId.
     * <p>
     * If the event exists, displays it in the UI. Otherwise, logs an error and finishes the activity.
     */
    private void loadEventDetails() {
        db.collection("Events").document(eventId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        displayEvent(documentSnapshot);
                    } else {
                        Log.e(TAG, "Event not found");
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading event", e);
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

        // Load image
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .into(ivEventImage);
        } else {
            ivEventImage.setImageResource(R.mipmap.ic_image_placeholder);
        }
    }

    /**
     * Deletes the current event from Firestore and finishes the activity.
     */
    private void onDeleteEvent() {
        db.collection("Events").document(eventId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    String successMessage = "Event deleted successfully";
                    Log.d(TAG, successMessage);
                    Toast.makeText(this, successMessage, Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    String failureMessage = "Error deleting event";
                    Log.e(TAG, failureMessage, e);
                    Toast.makeText(this, failureMessage, Toast.LENGTH_SHORT).show();
                });
    }
}