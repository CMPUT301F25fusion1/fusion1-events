package com.example.fusion1_events;

import static androidx.fragment.app.FragmentManager.TAG;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.installations.FirebaseInstallations;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private ImageView ivDetailImage;
    private TextView tvDetailTitle, tvDetailDate, tvDetailDescription,
            tvDetailSignups, tvCancelledMessage, tvDetailDeadline, tvDetailTime, tvPastDeadline;
    private Button btnScanQR, btnJoinWaitingList, btnLeaveWaitingList,
            btnAcceptInvite, btnDeclineInvite, btnCancelInvite;
    private Profile currentUser;
    private FirebaseFirestore db;
    private String eventId;
    private String deviceId;
    private Event currentEvent;
    private FusedLocationProviderClient fusedLocationClient;

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
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        eventId = getIntent().getStringExtra("eventId");
        currentUser = (Profile) getIntent().getSerializableExtra("currentUser");

        ivDetailImage = findViewById(R.id.ivDetailImage);
        tvDetailDeadline = findViewById(R.id.tvDetailDeadline);
        tvDetailTime = findViewById(R.id.tvDetailTime);
        tvDetailTitle = findViewById(R.id.tvDetailTitle);
        tvDetailDate = findViewById(R.id.tvDetailDate);
        tvDetailDescription = findViewById(R.id.tvDetailDescription);
        tvDetailSignups = findViewById(R.id.tvDetailSignups);
        tvCancelledMessage = findViewById(R.id.tvCancelledMessage);
        tvPastDeadline = findViewById(R.id.tvPastDeadline);
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
                        Date registrationDate = currentEvent.getRegistration_end().toDate();
                        SimpleDateFormat reg = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                        tvDetailDeadline.setText(reg.format(registrationDate));

                        Date date = currentEvent.getDate().toDate();
                        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                        SimpleDateFormat time = new SimpleDateFormat("h:mm a", Locale.getDefault());
                        tvDetailDate.setText(sdf.format(date));
                        tvDetailTime.setText(time.format(date));

                        tvDetailSignups.setText(String.valueOf(currentEvent.getSignups()));
                        if (currentEvent.getImageUrl() != null ) {
                            Glide.with(this).load(currentEvent.getImageUrl()).into(ivDetailImage);
                        } else {
                            ivDetailImage.setImageResource(R.drawable.logo_loading);
                        }

                            TextView tvHome = findViewById(R.id.tvHome);
                        TextView tvYourEvents = findViewById(R.id.tvYourEvents);

                        TextView tvYourProfile = findViewById(R.id.tvYourProfileEvent);

                        TextView tvNotifications = findViewById(R.id.tvNotifications);

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

                        tvNotifications.setOnClickListener(v -> {
                            Intent intent = new Intent( this, NotificationsActivity.class);
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

                        boolean isInFinalList = currentEvent.getInvitedList() != null &&
                                currentEvent.getInvitedList().contains(entrantRef);

                        boolean isCancelled = currentEvent.getCancelled() != null &&
                                currentEvent.getCancelled().contains(entrantRef);

                        // new entrants can't join waiting list after reg deadline
                        Date currentDate = new Date();
                        Date regDate = currentEvent.getRegistration_end().toDate();
                        boolean afterRegistration = currentDate.after(regDate);
                        boolean waiting = currentEvent.getWaitingList() != null &&
                                currentEvent.getWaitingList().contains(entrantRef);
                        boolean invited = currentEvent.getInvitedList() != null &&
                                currentEvent.getInvitedList().contains(entrantRef);

                        if (afterRegistration && !waiting && !invited) {
                            btnScanQR.setVisibility(View.GONE);
                            btnJoinWaitingList.setVisibility(View.GONE);
                            btnLeaveWaitingList.setVisibility(View.GONE);
                            btnAcceptInvite.setVisibility(View.GONE);
                            btnDeclineInvite.setVisibility(View.GONE);
                            tvPastDeadline.setVisibility(View.VISIBLE);
                        }

                        else if (isConfirmed) {
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

                        btnJoinWaitingList.setOnClickListener(v -> {
                            if (checkLocationPermission()) {
                                getUserLocationAndJoinWaitingList();
                            }
                        });
                        btnLeaveWaitingList.setOnClickListener(v -> leaveWaitingList());
                    });
        });
    }

    // Location
    private boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getUserLocationAndJoinWaitingList();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getUserLocationAndJoinWaitingList() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) return;

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        saveLocationToFirestore(latitude, longitude);
                    }
                    joinWaitingList();
                });
    }

    private void saveLocationToFirestore(double latitude, double longitude) {
        DocumentReference entrantRef = db.collection("Entrants").document(deviceId);
        Map<String, Object> updates = new HashMap<>();
        updates.put("latitude", latitude);
        updates.put("longitude", longitude);

        entrantRef.update(updates)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Location updated"))
                .addOnFailureListener(e -> Log.e("Firestore", "Failed to update location", e));
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

    /**
     * Adds the current entrant to the event's confirmed list in Firestore.
     * Provides user feedback via a Toast.
     * Display a cancel button for the entrant to cancel their invitation
     */
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
    /**
     * Removes the current entrant from the event's waiting list and invited list in Firestore.
     * Draws a new entrant for the invited list.
     * Provides user feedback via a Toast.
     */
    private void declineInvitation() {
        DocumentReference eventRef = db.collection("Events").document(eventId);
        DocumentReference entrantRef = db.collection("Entrants").document(deviceId);

        eventRef.update("invitedList", FieldValue.arrayRemove(entrantRef))
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "You declined the invitation.", Toast.LENGTH_SHORT).show();

                    btnScanQR.setVisibility(View.GONE);
                    btnJoinWaitingList.setVisibility(View.GONE);
                    btnLeaveWaitingList.setVisibility(View.GONE);
                    btnAcceptInvite.setVisibility(View.GONE);
                    btnDeclineInvite.setVisibility(View.GONE);

                    eventRef.get().addOnSuccessListener(eventDoc -> {

                        if (!eventDoc.exists()) {
                            Log.e("EntrantEvent", "Event doc does not exist for id = " + eventId);
                            return;
                        }

                        Log.d("EntrantEvent", "Event data = " + eventDoc.getData());

                        DocumentReference organizerRef = eventDoc.getDocumentReference("organizerId");

                        if (organizerRef == null) {
                            Log.e("EntrantEvent", "organizerId is null or not a reference on event " + eventId);
                            return;
                        }

                        String organizerId = organizerRef.getId();
                        Log.d("EntrantEvent", "OrganizerRef path = " + organizerRef.getPath()
                                + ", organizerId = " + organizerId);

                        DrawHelper.runDraw(eventId, organizerId, FirebaseFirestore.getInstance(), this);

                    }).addOnFailureListener(e -> {
                        Log.e("EntrantEvent", "Failed to load event " + eventId, e);
                    });

                });
    }
    /**
     * Removes the current entrant from the event's invited list and confirmed list in Firestore.
     * Provides user feedback via a Toast.
     */
    private void cancelInvitation(){
        DocumentReference eventRef = db.collection("Events").document(eventId);
        DocumentReference entrantRef = db.collection("Entrants").document(deviceId);

        eventRef.update("confirmed", FieldValue.arrayRemove(entrantRef))
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Invitation canceled!", Toast.LENGTH_SHORT).show();

                    eventRef.update("invitedList", FieldValue.arrayRemove(entrantRef));
                    btnCancelInvite.setVisibility(View.GONE);
                    tvCancelledMessage.setVisibility(View.VISIBLE);
                });
    }
}
