package com.example.fusion1_events;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EventDetailActivity extends AppCompatActivity {
    private static final String TAG = "EventDetailActivity";
    private ImageView ivDetailImage;
    private TextView tvDetailTitle, tvDetailDate, tvDetailDescription,
            tvDetailSignups;
    private Button btnScanQR, btnJoinWaitingList, btnLeaveWaitingList;
    private Profile currentUser;
    private FirebaseFirestore db;
    private String eventId;
    private String deviceId;
    private Event currentEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        db = FirebaseFirestore.getInstance();
        eventId = getIntent().getStringExtra("eventId");
        currentUser = (Profile) getIntent().getSerializableExtra("currentUser");

        ivDetailImage = findViewById(R.id.ivDetailImage);
        tvDetailTitle = findViewById(R.id.tvDetailTitle);
        tvDetailDate = findViewById(R.id.tvDetailDate);
        tvDetailDescription = findViewById(R.id.tvDetailDescription);
        tvDetailSignups = findViewById(R.id.tvDetailSignups);
        btnScanQR = findViewById(R.id.btnScanQR);
        btnJoinWaitingList = findViewById(R.id.btnJoinWaitingList);
        btnLeaveWaitingList = findViewById(R.id.btnLeaveWaitingList);

        TextView tvHome = findViewById(R.id.tvHome);
        TextView tvYourEvents = findViewById(R.id.tvYourEvents);
        tvHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, EventActivity.class);
            intent.putExtra("currentUser", currentUser);
            startActivity(intent);
        });
        tvYourEvents.setOnClickListener(v -> {
            Intent intent = new Intent(this, YourEventsActivity.class);
            intent.putExtra("currentUser", currentUser);
            startActivity(intent);
        });

        // Scan QR currently not used — keep placeholder
        btnScanQR.setOnClickListener(v ->
                Toast.makeText(this, "Scan QR functionality not enabled in this build.", Toast.LENGTH_SHORT).show()
        );

        // Set join/leave click listeners (these will check deviceId is ready)
        btnJoinWaitingList.setOnClickListener(v -> joinWaitingListTransaction());
        btnLeaveWaitingList.setOnClickListener(v -> leaveWaitingListTransaction());

        // Get device id then load event
        FirebaseInstallations.getInstance().getId().addOnSuccessListener(id -> {
            deviceId = id;
            if (eventId == null || eventId.isEmpty()) {
                Toast.makeText(this, "No event selected.", Toast.LENGTH_SHORT).show();
                return;
            }
            loadEventAndSetupUI(eventId);
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to get device id: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "FirebaseInstallations.getId() failed", e);
        });
    }

    private void loadEventAndSetupUI(String id) {
        DocumentReference docRef = db.collection("Events").document(id);
        docRef.get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) {
                        Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    currentEvent = doc.toObject(Event.class);
                    if (currentEvent == null) {
                        Toast.makeText(this, "Failed to parse event data", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Populate UI
                    tvDetailTitle.setText(currentEvent.getTitle() != null ? currentEvent.getTitle() : "Untitled");
                    tvDetailDescription.setText(currentEvent.getDescription() != null ? currentEvent.getDescription() : "");

                    if (currentEvent.getDate() != null) {
                        Date date = currentEvent.getDate().toDate();
                        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy h:mm a", Locale.getDefault());
                        tvDetailDate.setText(sdf.format(date));
                    } else {
                        tvDetailDate.setText("TBA");
                    }

                    // Signups display (fallback to 0)
                    tvDetailSignups.setText(String.valueOf(currentEvent.getSignups()));


                    // Placeholder image
                    ivDetailImage.setImageResource(R.drawable.ic_launcher_background);

                    // Show/hide join/leave depending on membership
                    boolean isJoined = false;
                    List<String> waiting = currentEvent.getWaitingList();
                    if (waiting != null && deviceId != null) {
                        isJoined = waiting.contains(deviceId);
                    }
                    btnJoinWaitingList.setVisibility(isJoined ? View.GONE : View.VISIBLE);
                    btnLeaveWaitingList.setVisibility(isJoined ? View.VISIBLE : View.GONE);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "loadEvent error", e);
                });
    }

    /**
     * Join waiting list using a transaction:
     * - read document
     * - if deviceId not present, add to waitingList and increment Signups
     * - if already present, do nothing
     */
    private void joinWaitingListTransaction() {
        if (deviceId == null || eventId == null) {
            Toast.makeText(this, "Please wait — device or event not ready.", Toast.LENGTH_SHORT).show();
            return;
        }

        // disable button to prevent multiple taps
        btnJoinWaitingList.setEnabled(false);

        DocumentReference docRef = db.collection("Events").document(eventId);
        db.runTransaction(transaction -> {
            DocumentSnapshot snapshot = transaction.get(docRef);
            if (!snapshot.exists()) {
                throw new FirebaseFirestoreException("Event not found",
                        FirebaseFirestoreException.Code.NOT_FOUND);
            }

            List<String> waiting = (List<String>) snapshot.get("waitingList");
            Long signupsLong = snapshot.getLong("Signups");
            long signups = signupsLong != null ? signupsLong : 0L;

            // if waitingList already contains deviceId, nothing to do
            if (waiting != null && waiting.contains(deviceId)) {
                return "already_joined";
            }

            // perform atomic updates: arrayUnion and increment
            transaction.update(docRef, "waitingList", FieldValue.arrayUnion(deviceId));
            transaction.update(docRef, "Signups", FieldValue.increment(1));

            return "joined";
        }).addOnSuccessListener(result -> {
            if ("already_joined".equals(result)) {
                Toast.makeText(this, "You are already on the waiting list.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "You joined the waiting list!", Toast.LENGTH_SHORT).show();
            }
            // refresh UI
            loadEventAndSetupUI(eventId);
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to join waiting list: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "joinWaitingListTransaction failed", e);
        }).addOnCompleteListener(task -> {
            btnJoinWaitingList.setEnabled(true);
        });
    }

    /**
     * Leave waiting list using a transaction:
     * - read document
     * - if deviceId present, remove from waitingList and decrement Signups
     * - if not present, do nothing
     */
    private void leaveWaitingListTransaction() {
        if (deviceId == null || eventId == null) {
            Toast.makeText(this, "Please wait — device or event not ready.", Toast.LENGTH_SHORT).show();
            return;
        }

        btnLeaveWaitingList.setEnabled(false);

        DocumentReference docRef = db.collection("Events").document(eventId);
        db.runTransaction(transaction -> {
            DocumentSnapshot snapshot = transaction.get(docRef);
            if (!snapshot.exists()) {
                throw new FirebaseFirestoreException("Event not found",
                        FirebaseFirestoreException.Code.NOT_FOUND);
            }

            List<String> waiting = (List<String>) snapshot.get("waitingList");
            Long signupsLong = snapshot.getLong("Signups");
            long signups = signupsLong != null ? signupsLong : 0L;

            // if not present, nothing to do
            if (waiting == null || !waiting.contains(deviceId)) {
                return "not_joined";
            }

            // remove and decrement
            transaction.update(docRef, "waitingList", FieldValue.arrayRemove(deviceId));
            // ensure signups doesn't go below zero in semantic terms - decrement anyway
            transaction.update(docRef, "Signups", FieldValue.increment(-1));

            return "left";
        }).addOnSuccessListener(result -> {
            if ("not_joined".equals(result)) {
                Toast.makeText(this, "You were not on the waiting list.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "You left the waiting list.", Toast.LENGTH_SHORT).show();
            }
            // refresh UI
            loadEventAndSetupUI(eventId);
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to leave waiting list: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "leaveWaitingListTransaction failed", e);
        }).addOnCompleteListener(task -> {
            btnLeaveWaitingList.setEnabled(true);
        });
    }
}