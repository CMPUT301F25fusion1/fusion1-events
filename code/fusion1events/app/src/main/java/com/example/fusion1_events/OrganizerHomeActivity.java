package com.example.fusion1_events;


import static androidx.fragment.app.FragmentManager.TAG;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;

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
    private Button profileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_organizer_home);

        profileButton = findViewById(R.id.buttonProfileOrganizerHome);
        profileButton.setOnClickListener(v -> goProfileScreen());



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        CloudinaryConfig.init(this);

        deviceId = getIntent().getStringExtra("device_id");

        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();
        organizerRef = db.collection("Organizers").document(deviceId);



        addEventButton = findViewById(R.id.fabAddEvent);
        recyclerView = findViewById(R.id.recyclerEvents);


        setupRecyclerView();

        addEventButton.setOnClickListener(view -> {
            AddEventFragment addEventFragment = new AddEventFragment();
            addEventFragment.show(getSupportFragmentManager(), "Add Event");
        });

        loadEvents();
    }

    private void goProfileScreen(){
        startActivity(new android.content.Intent(this, ProfileViewActivity.class));

    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventsAdapter = new EventsAdapter(eventsModels, new EventsAdapter.OnEventClickListener() {
            @Override
            public void onEventClick(EventsModel event, int position) {
                EventCreatedDialogFragment confirmDialog =
                        EventCreatedDialogFragment.newInstance(event, null);
                confirmDialog.show(getSupportFragmentManager(), "Event Created");
            }

            @Override
            public void onEditClick(EventsModel event, int position) {
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

        organizerRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    Log.d(TAG, "Organizer document retrieved successfully");

                    if (documentSnapshot.exists()) {
                        Log.d(TAG, "Organizer document exists");
                        Log.d(TAG, "Document data: " + documentSnapshot.getData());


                        Object eventsObj = documentSnapshot.get("events");
                        Log.d(TAG, "Events object type: " + (eventsObj != null ? eventsObj.getClass().getName() : "null"));
                        Log.d(TAG, "Events object: " + eventsObj);

                        if (eventsObj instanceof List) {
                            List<Object> eventsList = (List<Object>) eventsObj;

                            if (!eventsList.isEmpty()) {
                                eventsModels.clear();
                                Log.d(TAG, "Found " + eventsList.size() + " event references");


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
                                                            Object waitingListObj = eventDoc.get("waitingList");
                                                            ArrayList<String> waitingList = new ArrayList<>();
                                                            ArrayList<String> finaList = new ArrayList<>();

                                                            if (waitingListObj instanceof List) {
                                                                List<Object> waitingListRefs = (List<Object>) waitingListObj;
                                                                Log.d(TAG, "Found " + waitingListRefs.size() + " entrants in waiting list");


                                                                for (Object refObj : waitingListRefs) {
                                                                    if (refObj instanceof DocumentReference) {
                                                                        DocumentReference entrantRef = (DocumentReference) refObj;

                                                                        String entrantId = entrantRef.getId();
                                                                        waitingList.add(entrantId);
                                                                        Log.d(TAG, "Added entrant to waiting list: " + entrantId);
                                                                    } else if (refObj instanceof String) {

                                                                        waitingList.add((String) refObj);
                                                                        Log.d(TAG, "Added entrant ID to waiting list: " + refObj);
                                                                    }
                                                                }
                                                            } else {
                                                                Log.d(TAG, "No waiting list found or empty");
                                                            }


                                                            EventsModel event = new EventsModel(
                                                                    eventDoc.getString("title"),
                                                                    eventDoc.getDate("registration_start"),
                                                                    eventDoc.getDate("registration_end"),
                                                                    eventDoc.getString("description"),
                                                                    eventDoc.getDate("date"),
                                                                    eventDoc.getLong("attendees"),
                                                                    eventDoc.getLong("Signups"),
                                                                    waitingList, // ADDED: Pass waiting list
                                                                    eventDoc.getString("imageUrl"),
                                                                    eventDoc.getId(),
                                                                    finaList
                                                            );
                                                            eventsModels.add(event);
                                                            eventsAdapter.notifyDataSetChanged();
                                                            Log.d(TAG, "Event added to list: " + event.getEventTitle() +
                                                                    " with " + waitingList.size() + " entrants in waiting list");
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
    public void addEvent(EventsModel eventsModel, Uri imageUri) {
        // Check if user selected an image
        if (imageUri != null) {
            // Upload image to Cloudinary first, then create event with URL
            uploadImageToCloudinaryAndCreateEvent(eventsModel, imageUri);
        } else {
            // No image selected, create event without image
            createEventInFirestore(eventsModel, null);
        }
    }

    // ADDED: Upload to Cloudinary
    private void uploadImageToCloudinaryAndCreateEvent(EventsModel eventsModel, Uri imageUri) {
        Log.d(TAG, "Uploading image to Cloudinary: " + imageUri);

        String publicId = "event_images/" + System.currentTimeMillis();

        MediaManager.get().upload(imageUri)
                .option("folder", "event_images")
                .option("public_id", publicId)
                .option("resource_type", "image")
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {
                        Log.d(TAG, "Cloudinary upload started: " + requestId);
                    }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {
                        double progress = (double) bytes / totalBytes * 100;
                        Log.d(TAG, " progress: " + progress + "%");
                    }

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        String imageUrl = (String) resultData.get("secure_url");
                        Log.d(TAG, "Cloudinary upload URL: " + imageUrl);

                        runOnUiThread(() -> createEventInFirestore(eventsModel, imageUrl));
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        Log.e(TAG, "Cloudinary upload failed: " + error.getDescription());
                        runOnUiThread(() -> {
                            Toast.makeText(OrganizerHomeActivity.this,
                                    "Failed to upload image: " + error.getDescription(),
                                    Toast.LENGTH_SHORT).show();
                        });
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {
                        Log.w(TAG, "Cloudinary upload rescheduled: " + error.getDescription());
                    }
                })
                .dispatch();
        }


    private void createEventInFirestore(EventsModel eventsModel, String imageUrl) {
        // Create event data map matching database field names
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("title", eventsModel.getEventTitle());
        eventData.put("registration_start", eventsModel.getRegistrationStart());
        eventData.put("registration_end", eventsModel.getRegistrationEnd());
        eventData.put("description", eventsModel.getEventDescription());
        eventData.put("date", eventsModel.getDate());
        eventData.put("attendees", eventsModel.getAttendees());
        eventData.put("Signups", eventsModel.getSignups());
        eventData.put("imageUrl", imageUrl); // Cloudinary URL
        eventData.put("waitingList", new ArrayList<>()); // ADDED: Initialize with empty waiting list

        db.collection("Events")
                .add(eventData)
                .addOnSuccessListener(eventDocRef -> {
                    Log.d(TAG, "Event added with ID: " + eventDocRef.getId());

                    eventsModel.setEventId(eventDocRef.getId());
                    eventsModel.setImageUrl(imageUrl);

                    organizerRef.update("events", FieldValue.arrayUnion(eventDocRef))
                            .addOnSuccessListener(aVoid -> {
                                Log.d(TAG, "Event reference added to organizer");

                                eventsModels.add(0, eventsModel);
                                eventsAdapter.notifyItemInserted(0);
                                recyclerView.smoothScrollToPosition(0);

                                EventCreatedDialogFragment confirmDialog =
                                        EventCreatedDialogFragment.newInstance(
                                                eventsModel,
                                                null);
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

    // MODIFIED: Delete event and handle waiting list cleanup if needed
    private void deleteEvent(EventsModel event, int position) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Delete Event")
                .setMessage("Are you sure you want to delete \"" + event.getEventTitle() + "\"?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    String eventId = event.getEventId();
                    String imageUrl = event.getImageUrl();

                    if (eventId != null) {
                        // Delete from Firestore
                        db.collection("Events").document(eventId)
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    Log.d(TAG, "Event deleted from Firestore");

                                    // ADDED: Log waiting list cleanup
                                    if (event.getWaitingListSize() > 0) {
                                        Log.d(TAG, "Event had " + event.getWaitingListSize() +
                                                " entrants in waiting list. Consider notifying them.");
                                        // TODO: Implement notification to entrants
                                    }

                                    //Cloudinary images remain (see previous implementation notes)
                                    if (imageUrl != null && !imageUrl.isEmpty()) {
                                        Log.d(TAG, "Image remains in Cloudinary: " + imageUrl);
                                    }

                                    // Remove from local list
                                    eventsModels.remove(position);
                                    eventsAdapter.notifyItemRemoved(position);
                                    Toast.makeText(OrganizerHomeActivity.this,
                                            "Event deleted",
                                            Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Failed to delete event", e);
                                    Toast.makeText(OrganizerHomeActivity.this,
                                            "Failed to delete event",
                                            Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        eventsModels.remove(position);
                        eventsAdapter.notifyItemRemoved(position);
                        Toast.makeText(OrganizerHomeActivity.this,
                                "Event deleted from list",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();

        //TODO: add editing events functions
    }
}