package com.example.fusion1_events;

import android.content.Intent;
import android.os.Bundle;
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

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
/**
 * EventDetailActivity displays detailed information about a single event.
 *
 * Shows the event's title, description, date, signups, and image. Users
 * can join or leave the event's waiting list and navigate back to the home
 * screen or their events.
 */
public class EventDetailActivity extends AppCompatActivity {
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

        FirebaseInstallations.getInstance().getId().addOnSuccessListener(id -> {
            deviceId = id;

            db.collection("Events").document(eventId)
                    .get()
                    .addOnSuccessListener(doc -> {
                        currentEvent = doc.toObject(Event.class);

                        tvDetailTitle.setText(currentEvent.getTitle());
                        tvDetailDescription.setText(currentEvent.getDescription());

                        Date date = currentEvent.getDate().toDate();
                        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy h:mm a", Locale.getDefault());
                        tvDetailDate.setText(sdf.format(date));

                        tvDetailSignups.setText(String.valueOf(currentEvent.getSignups()));
                        ivDetailImage.setImageResource(R.drawable.ic_launcher_background);

                        TextView tvHome = findViewById(R.id.tvHome);
                        TextView tvYourEvents = findViewById(R.id.tvYourEvents);

                        tvHome.setOnClickListener(v -> {
                            Intent intent = new Intent(this, EntrantHomeActivity.class);
                            intent.putExtra("currentUser", currentUser);
                            startActivity(intent);
                        });

                        tvYourEvents.setOnClickListener(v -> {
                            Intent intent = new Intent(this, YourEventsActivity.class);
                            intent.putExtra("currentUser", currentUser);
                            startActivity(intent);
                        });

                        btnScanQR.setOnClickListener(v ->
                                Toast.makeText(this, "Scan QR functionality coming soon!", Toast.LENGTH_SHORT).show()
                        );
                        DocumentReference entrantRef = db.collection("Entrants").document(deviceId);
                        if (currentEvent.getWaitingList().contains(entrantRef)) {
                            btnJoinWaitingList.setVisibility(View.GONE);
                            btnLeaveWaitingList.setVisibility(View.VISIBLE);
                        } else {
                            btnJoinWaitingList.setVisibility(View.VISIBLE);
                            btnLeaveWaitingList.setVisibility(View.GONE);
                        }

                        btnJoinWaitingList.setOnClickListener(v -> joinWaitingList());
                        btnLeaveWaitingList.setOnClickListener(v -> leaveWaitingList());
                    });
        });
    }
    /**
     * Adds the current user to the event's waiting list.
     */
    private void joinWaitingList() {
        DocumentReference eventRef = db.collection("Events").document(eventId);
        DocumentReference entrantRef = db.collection("Entrants").document(deviceId);
        eventRef.update("waitingList", FieldValue.arrayUnion(entrantRef))
                .addOnSuccessListener(aVoid -> {
                    int newSignups = currentEvent.getWaitingList().size() + 1;
                    currentEvent.getWaitingList().add(entrantRef);
                    currentEvent.setSignups(newSignups);
                    eventRef.update("Signups", newSignups);

                    tvDetailSignups.setText(String.valueOf(newSignups));
                    btnJoinWaitingList.setVisibility(View.GONE);
                    btnLeaveWaitingList.setVisibility(View.VISIBLE);
                    Toast.makeText(this, "You joined the waiting list!", Toast.LENGTH_SHORT).show();
                });
    }
    /**
     * Removes the current user from the event's waiting list.
     */
    private void leaveWaitingList() {
        DocumentReference eventRef = db.collection("Events").document(eventId);
        DocumentReference entrantRef = db.collection("Entrants").document(deviceId);
        eventRef.update("waitingList", FieldValue.arrayRemove(entrantRef))
                .addOnSuccessListener(aVoid -> {
                    currentEvent.getWaitingList().remove(entrantRef);
                    int newSignups = currentEvent.getWaitingList().size();
                    currentEvent.setSignups(newSignups);
                    eventRef.update("Signups", newSignups);

                    tvDetailSignups.setText(String.valueOf(newSignups));
                    btnJoinWaitingList.setVisibility(View.VISIBLE);
                    btnLeaveWaitingList.setVisibility(View.GONE);
                    Toast.makeText(this, "You left the waiting list!", Toast.LENGTH_SHORT).show();
                });
        }

}
