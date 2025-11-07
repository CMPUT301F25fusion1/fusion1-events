package com.example.fusion1_events;


import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.installations.FirebaseInstallations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class OrganizerHomeActivity extends AppCompatActivity implements AddEventFragment.AddEventDialogListener {
    private static final String TAG = "OrganizerHomeActivity";
    private FirebaseFirestore db;
    private FloatingActionButton addEventButton;
    private RecyclerView recyclerView;
    private EventsAdapter eventsAdapter;
    private ArrayList<EventsModel> eventsModels = new ArrayList<>();
    private DocumentReference organizerRef;
    private String deviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_organizer_home);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        deviceId = getIntent().getStringExtra("device_id");

        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();
        organizerRef = db.collection("Organizers").document(deviceId);



        // Initialize views
        addEventButton = findViewById(R.id.fabAddEvent);
        recyclerView = findViewById(R.id.recyclerEvents);

        // Set up RecyclerView
        setupRecyclerView();

        // Set up click listener for add event button
        addEventButton.setOnClickListener(view -> {
            AddEventFragment addEventFragment = new AddEventFragment();
            addEventFragment.show(getSupportFragmentManager(), "Add Event");
        });

        // Load existing events from Firestore
        loadEvents();
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventsAdapter = new EventsAdapter(eventsModels, new EventsAdapter.OnEventClickListener() {
            @Override
            public void onEventClick(EventsModel event, int position) {
                // Handle event card click - you can navigate to event details
                EventCreatedDialogFragment confirmDialog =
                        EventCreatedDialogFragment.newInstance(event.getEventTitle(), null);
                confirmDialog.show(getSupportFragmentManager(), "Event Created");
            }

            @Override
            public void onEditClick(EventsModel event, int position) {
                // Handle edit click
                Toast.makeText(OrganizerHomeActivity.this,
                        "Edit: " + event.getEventTitle(),
                        Toast.LENGTH_SHORT).show();
                // TODO: Implement edit functionality
            }

            @Override
            public void onDeleteClick(EventsModel event, int position) {
                // Handle delete click
                deleteEvent(event, position);
            }
        });
        recyclerView.setAdapter(eventsAdapter);
    }

    private void loadEvents() {
        Log.d(TAG, "Loading events for organizer: " + organizerRef.getPath());

        // Get the organizer document to retrieve the Events array
        organizerRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    Log.d(TAG, "Organizer document retrieved successfully");

                    if (documentSnapshot.exists()) {
                        Log.d(TAG, "Organizer document exists");
                        Log.d(TAG, "Document data: " + documentSnapshot.getData());

                        // Get the Events array (array of references)
                        Object eventsObj = documentSnapshot.get("events");
                        Log.d(TAG, "Events object type: " + (eventsObj != null ? eventsObj.getClass().getName() : "null"));
                        Log.d(TAG, "Events object: " + eventsObj);

                        if (eventsObj instanceof List) {
                            List<Object> eventsList = (List<Object>) eventsObj;

                            if (!eventsList.isEmpty()) {
                                eventsModels.clear();
                                Log.d(TAG, "Found " + eventsList.size() + " event references");

                                // Load each event by following its reference
                                for (Object eventObj : eventsList) {
                                    if (eventObj instanceof DocumentReference) {
                                        DocumentReference eventRef = (DocumentReference) eventObj;
                                        Log.d(TAG, "Loading event from: " + eventRef.getPath());

                                        eventRef.get()
                                                .addOnSuccessListener(eventDoc -> {
                                                    if (eventDoc.exists()) {
                                                        Log.d(TAG, "Event document exists: " + eventDoc.getId());
                                                        Log.d(TAG, "Event data: " + eventDoc.getData());

                                                        try {
                                                            // Map field names from database to EventsModel
                                                            EventsModel event = new EventsModel(
                                                                    eventDoc.getString("title"),
                                                                    eventDoc.getDate("registration_start"),
                                                                    eventDoc.getDate("registration_end"),
                                                                    eventDoc.getString("description"),
                                                                    eventDoc.getDate("date"),
                                                                    eventDoc.getLong("attendees"),
                                                                    eventDoc.getLong("Signups")
                                                            );
                                                            eventsModels.add(event);
                                                            eventsAdapter.notifyDataSetChanged();
                                                            Log.d(TAG, "Event added to list: " + event.getEventTitle());
                                                        } catch (Exception e) {
                                                            Log.e(TAG, "Error parsing event data", e);
                                                        }
                                                    } else {
                                                        Log.w(TAG, "Event document does not exist: " + eventRef.getPath());
                                                    }
                                                })
                                                .addOnFailureListener(e -> {
                                                    Log.e(TAG, "Error loading event from " + eventRef.getPath(), e);
                                                });
                                    } else {
                                        Log.w(TAG, "Event object is not a DocumentReference: " + eventObj.getClass().getName());
                                    }
                                }
                            } else {
                                Log.d(TAG, "Events array is empty");
                                Toast.makeText(OrganizerHomeActivity.this,
                                        "No events found",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.w(TAG, "Events field is not a List or is null");
                            Toast.makeText(OrganizerHomeActivity.this,
                                    "No events found",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.w(TAG, "Organizer document does not exist");
                        Toast.makeText(OrganizerHomeActivity.this,
                                "Organizer not found",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading organizer document", e);
                    Toast.makeText(OrganizerHomeActivity.this,
                            "Failed to load events: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void addEvent(EventsModel eventsModel) {
        // Create event data map matching database field names
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("title", eventsModel.getEventTitle()); // "title" in DB
        eventData.put("registration_start", eventsModel.getRegistrationStart()); // "registration_start" in DB
        eventData.put("registration_end", eventsModel.getRegistrationEnd()); // "registration_end" in DB
        eventData.put("description", eventsModel.getEventDescription()); // "description" in DB
        eventData.put("date", eventsModel.getDate()); // "date" in DB
        eventData.put("attendees", eventsModel.getAttendees()); // "attendees" in DB
        eventData.put("Signups", eventsModel.getSignups());


        db.collection("Events")
                .add(eventData)
                .addOnSuccessListener(eventDocRef -> {
                    Log.d(TAG, "Event added with ID: " + eventDocRef.getId());

                    // Then, add the event reference to the organizer's Events array
                    organizerRef.update("Events", FieldValue.arrayUnion(eventDocRef))
                            .addOnSuccessListener(aVoid -> {
                                Log.d(TAG, "Event reference added to organizer");

                                // Add to local list
                                eventsModels.add(0, eventsModel); // Add to top of list
                                eventsAdapter.notifyItemInserted(0);
                                recyclerView.smoothScrollToPosition(0);

                                // Show confirmation dialog
                                EventCreatedDialogFragment confirmDialog = EventCreatedDialogFragment.newInstance(
                                        eventsModel.getEventTitle(), eventDocRef.getId());
                                confirmDialog.show(getSupportFragmentManager(), "Event Created");
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Error adding event reference to organizer", e);
                                Toast.makeText(OrganizerHomeActivity.this,
                                        "Event created but failed to link to organizer",
                                        Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error creating event", e);
                    Toast.makeText(OrganizerHomeActivity.this,
                            "Failed to create event: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void deleteEvent(EventsModel event, int position) {
        // Show confirmation dialog before deleting
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Delete Event")
                .setMessage("Are you sure you want to delete \"" + event.getEventTitle() + "\"?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    // TODO: Find and delete the event from Firestore
                    // This requires storing the event document reference in EventsModel
                    // For now, just remove from local list
                    eventsModels.remove(position);
                    eventsAdapter.notifyItemRemoved(position);
                    Toast.makeText(OrganizerHomeActivity.this,
                            "Event deleted from list",
                            Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}