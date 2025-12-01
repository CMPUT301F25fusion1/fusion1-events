package com.example.fusion1_events.admin.notification;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fusion1_events.R;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Activity that displays the full details of a single notification.
 * <p>
 * Retrieves the notification document from Firestore using its document ID and populates
 * UI fields with the notification title, message, event name, sender, receiver, timestamp,
 * and notified and read status.
 */
public class AdminNotificationDetailsActivity extends AppCompatActivity {
    public static final String UNKNOWN = "Unknown";
    public static final String TAG = "FirestoreDebugNotificationDetails";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TextView tvTitle, tvMessage, tvEventName, tvSender, tvReceiver, tvCreatedAt, tvNotified, tvRead;
    private ImageButton buttonBack;
    private String notificationId;

    /**
     * Initializes the activity, binds UI components, and loads notification details.
     *
     * @param savedInstanceState Bundle containing activity's previously saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_notification_details);

        // Bind views
        tvTitle = findViewById(R.id.tvNotificationTitle);
        tvMessage = findViewById(R.id.tvNotificationMessage);
        tvEventName = findViewById(R.id.tvEventName);
        tvSender = findViewById(R.id.tvSender);
        tvReceiver = findViewById(R.id.tvReceiver);
        tvCreatedAt = findViewById(R.id.tvCreatedAt);
        tvNotified = findViewById(R.id.tvNotified);
        tvRead = findViewById(R.id.tvRead);
        buttonBack = findViewById(R.id.buttonBack);

        buttonBack.setOnClickListener(v -> finish());

        // Get Firestore ID passed from adapter
        notificationId = getIntent().getStringExtra("notificationId");

        if (notificationId == null || notificationId.isEmpty()) {
            Log.e(TAG, "Missing notification ID");
            finish();
            return;
        }

        loadNotificationDetails();
    }

    /**
     * Loads the notification document from Firestore using its document ID.
     * <p>
     * On success, calls {@link #displayNotification(DocumentSnapshot)}.
     * On failure, logs the error and closes the activity.
     */
    private void loadNotificationDetails() {
        db.collection("Notifications").document(notificationId)
                .get()
                .addOnSuccessListener(this::displayNotification)
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading notification", e);
                    finish();
                });
    }

    /**
     * Populates the UI fields with data from the given Firestore document.
     * <p>
     * Fields include title, message, event name, timestamp, notified and read status,
     * and resolves Firestore references to sender and receiver names.
     *
     * @param doc Firestore document containing notification data
     */
    private void displayNotification(DocumentSnapshot doc) {
        if (!doc.exists()) {
            Log.e(TAG, "Notification not found");
            finish();
            return;
        }

        // Basic fields
        String title = doc.getString("notificationTitle");
        String message = doc.getString("notificationMessage");
        String eventName = doc.getString("eventName");
        Timestamp createdAt = doc.getTimestamp("createdAt");
        Boolean notified = doc.getBoolean("notified");
        Boolean read = doc.getBoolean("read");

        // Firestore references
        DocumentReference senderRef = doc.getDocumentReference("senderID");
        DocumentReference receiverRef = doc.getDocumentReference("receiverId");

        // Set basic UI
        tvTitle.setText(title != null ? title : "No title");
        tvMessage.setText(message != null ? message : "No message");
        tvEventName.setText(eventName != null ? eventName : "N/A");

        // Format timestamp
        if (createdAt != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd - HH:mm", Locale.getDefault());
            tvCreatedAt.setText(sdf.format(createdAt.toDate()));
        } else {
            tvCreatedAt.setText("N/A");
        }

        // Notified / Read
        tvNotified.setText(notified != null && notified ? "Yes" : "No");
        tvRead.setText(read != null && read ? "Yes" : "No");

        // Fetch sender name from Organizers collection
        if (senderRef != null) {
            senderRef.get()
                    .addOnSuccessListener(senderDoc -> {
                        Log.i("DEBUG", "Sender document exists? " + senderDoc.exists() + " Path: " + senderDoc.getReference().getPath());
                        if (senderDoc.exists()) {
                            String senderName = senderDoc.getString("name");
                            tvSender.setText(senderName != null ? senderName : UNKNOWN);
                        } else {
                            tvSender.setText(UNKNOWN);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.i("DEBUG", "Failed to fetch sender", e);
                        tvSender.setText(UNKNOWN);
                    });
        } else {
            tvSender.setText(UNKNOWN);
        }

        // Fetch receiver name from Entrants collection
        if (receiverRef != null) {
            receiverRef.get().addOnSuccessListener(receiverDoc -> {
                if (receiverDoc.exists()) {
                    String receiverName = receiverDoc.getString("name");
                    tvReceiver.setText(receiverName != null ? receiverName : UNKNOWN);
                } else {
                    tvReceiver.setText(UNKNOWN);
                }
            }).addOnFailureListener(e -> tvReceiver.setText(UNKNOWN));
        } else {
            tvReceiver.setText(UNKNOWN);
        }
    }
}
