package com.example.fusion1_events;

import static androidx.fragment.app.FragmentManager.TAG;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.installations.FirebaseInstallations;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
/**
 * File: EventDetailActivity.java
 *
 * Role:
 * - Displays full details for a selected event: title, description, date, signups, and image.
 * - Loads event data from Firestore based on the event ID passed through the intent.
 * - Allows entrant users to join or leave the event's waiting list.
 * - Provides navigation to the home screen, the user's events, or the user's profile.
 * - Handles UI state changes (e.g., showing/hiding Join/Leave buttons).
 *
 * Issues:
 * - Assumes device is online and Firestore requests succeed.
 * - Assumes event titles, IDs, and waiting list references are valid.
 * - No error handling for missing event documents or Firestore failures.
 * - joinWaitingList() and leaveWaitingList() assume Firestore operations always return success.
 *
 */
public class EventDetailActivity extends AppCompatActivity {
    private ImageView ivDetailImage;
    private TextView tvDetailTitle, tvDetailDate, tvDetailDescription,
            tvDetailSignups, tvCancelledMessage;
    private Button btnScanQR, btnJoinWaitingList, btnLeaveWaitingList,
            btnAcceptInvite, btnDeclineInvite, btnCancelInvite;
    private Profile currentUser;
    private FirebaseFirestore db;
    private String eventId;
    private String deviceId;
    private Event currentEvent;
    /**
     * Initializes the activity, loads event details from Firestore, sets up UI components,
     * and configures navigation and waiting list button logic.
     *
     * @param savedInstanceState the previously saved state of the activity, or null
     */
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
        tvCancelledMessage = findViewById(R.id.tvCancelledMessage);
        btnScanQR = findViewById(R.id.btnScanQR);
        btnJoinWaitingList = findViewById(R.id.btnJoinWaitingList);
        btnLeaveWaitingList = findViewById(R.id.btnLeaveWaitingList);
        btnAcceptInvite = findViewById(R.id.btnAcceptInvite);
        btnDeclineInvite = findViewById(R.id.btnDeclineInvite);
        btnCancelInvite = findViewById(R.id.btnCancelInvite);

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
                        if (currentEvent.getImageUrl() != null ) {
                            Glide.with(this).load(currentEvent.getImageUrl()).into(ivDetailImage);
                        } else {
                            ivDetailImage.setImageResource(R.drawable.logo_loading);
                        }


                        TextView tvHome = findViewById(R.id.tvHome);
                        TextView tvYourEvents = findViewById(R.id.tvYourEvents);

                        TextView tvYourProfile = findViewById(R.id.tvYourProfileEvent);

                        tvYourProfile.setOnClickListener(v -> {
                            Intent intent = new Intent(EventDetailActivity.this, ProfileViewActivity.class);
                            startActivity(intent);
                        });

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
                        if (currentEvent.getWaitingList().contains(entrantRef)) {//TODO: Check if waitlistsize is not null and signups isnt bigger than waitlistsize in or statement
                            btnJoinWaitingList.setVisibility(View.GONE);
                            btnLeaveWaitingList.setVisibility(View.VISIBLE);
                        } else {
                            btnJoinWaitingList.setVisibility(View.VISIBLE);
                            btnLeaveWaitingList.setVisibility(View.GONE);
                        }

                        // new
                        boolean isConfirmed = currentEvent.getConfirmed() != null &&
                                currentEvent.getConfirmed().contains(entrantRef);

                        boolean isInFinalList = currentEvent.getFinaList() != null &&
                                currentEvent.getFinaList().contains(entrantRef);

                        boolean isCancelled = currentEvent.getCancelled() != null &&
                                currentEvent.getCancelled().contains(entrantRef);

                        if (isConfirmed) {
                            btnScanQR.setVisibility(View.GONE);
                            btnJoinWaitingList.setVisibility(View.GONE);
                            btnLeaveWaitingList.setVisibility(View.GONE);
                            btnAcceptInvite.setVisibility(View.GONE);
                            btnDeclineInvite.setVisibility(View.GONE);
                            btnCancelInvite.setVisibility(View.VISIBLE);

                            btnCancelInvite.setOnClickListener(v -> cancelInvitation());
                        }

                        else if (isInFinalList) {
                            btnScanQR.setVisibility(View.GONE);
                            btnJoinWaitingList.setVisibility(View.GONE);
                            btnLeaveWaitingList.setVisibility(View.GONE);
                            btnAcceptInvite.setVisibility(View.VISIBLE);
                            btnDeclineInvite.setVisibility(View.VISIBLE);

                            btnAcceptInvite.setOnClickListener(v -> {
                                    acceptInvitation();
                                    btnAcceptInvite.setVisibility(View.GONE);
                                    btnDeclineInvite.setVisibility(View.GONE);
                                    btnCancelInvite.setVisibility(View.VISIBLE);

                                    btnCancelInvite.setOnClickListener(v2 -> cancelInvitation());
                            });

                            btnDeclineInvite.setOnClickListener(v -> declineInvitation());
                        }
                        else if (isCancelled) {
                            btnScanQR.setVisibility(View.GONE);
                            btnJoinWaitingList.setVisibility(View.GONE);
                            btnLeaveWaitingList.setVisibility(View.GONE);
                            btnAcceptInvite.setVisibility(View.GONE);
                            btnDeclineInvite.setVisibility(View.GONE);
                            tvCancelledMessage.setVisibility(View.VISIBLE);
                        }

                        btnJoinWaitingList.setOnClickListener(v -> joinWaitingList());
                        btnLeaveWaitingList.setOnClickListener(v -> leaveWaitingList());
                    });
        });
    }
    /**
     * Adds the current entrant to the event's waiting list in Firestore.
     * Updates the signups count locally and remotely, adjusts button visibility,
     * and provides user feedback via a Toast.
     */
    private void joinWaitingList() {
        DocumentReference eventRef = db.collection("Events").document(eventId);

        eventRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Long maxWaitingListSize = documentSnapshot.getLong("maxWaitingListSize");
                Long signups = documentSnapshot.getLong("Signups");

                if (maxWaitingListSize == null) maxWaitingListSize = 0L;
                if (signups == null) signups = 0L;

                if (maxWaitingListSize < signups && maxWaitingListSize != 0) {
                    Toast.makeText(this, "Waiting list is currently full!", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error fetching event", e);
            Toast.makeText(this, "Failed to check waiting list status", Toast.LENGTH_SHORT).show();
        });
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
     * Removes the current entrant from the event's waiting list in Firestore.
     * Updates the signups count locally and remotely, adjusts button visibility,
     * and provides user feedback via a Toast.
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

    //new stuff
    private void acceptInvitation() {
        DocumentReference eventRef = db.collection("Events").document(eventId);
        DocumentReference entrantRef = db.collection("Entrants").document(deviceId);

        eventRef.update("confirmed", FieldValue.arrayUnion(entrantRef))
                .addOnSuccessListener(aVoid -> {
                Toast.makeText(this, "Invitation accepted!", Toast.LENGTH_SHORT).show();

                    if (currentEvent.getConfirmed() == null) {
                        currentEvent.setConfirmed(new ArrayList<>());
                    }
                    currentEvent.getConfirmed().add(entrantRef);

                    btnAcceptInvite.setVisibility(View.GONE);
                    btnDeclineInvite.setVisibility(View.GONE);
                    btnCancelInvite.setVisibility(View.VISIBLE);
        });
    }
    private void declineInvitation() {
        DocumentReference eventRef = db.collection("Events").document(eventId);
        DocumentReference entrantRef = db.collection("Entrants").document(deviceId);

        eventRef.update("finaList", FieldValue.arrayRemove(entrantRef))
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "You declined the invitation.", Toast.LENGTH_SHORT).show();

                    btnAcceptInvite.setVisibility(View.GONE);
                    btnDeclineInvite.setVisibility(View.GONE);
                });
    }
    private void cancelInvitation(){
        DocumentReference eventRef = db.collection("Events").document(eventId);
        DocumentReference entrantRef = db.collection("Entrants").document(deviceId);

        eventRef.update("confirmed", FieldValue.arrayRemove(entrantRef))
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Invitation canceled!", Toast.LENGTH_SHORT).show();

                    eventRef.update("finaList", FieldValue.arrayRemove(entrantRef));
                    eventRef.update("cancelled", FieldValue.arrayUnion(entrantRef));
                    btnCancelInvite.setVisibility(View.GONE);
                    tvCancelledMessage.setVisibility(View.VISIBLE);
                });
    }
}
