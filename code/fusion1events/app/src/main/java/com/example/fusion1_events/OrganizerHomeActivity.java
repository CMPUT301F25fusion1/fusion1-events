package com.example.fusion1_events;


import static androidx.fragment.app.FragmentManager.TAG;

import android.content.Intent;
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

import com.cloudinary.utils.ObjectUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.installations.FirebaseInstallations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class OrganizerHomeActivity extends AppCompatActivity implements AddEventFragment.AddEventDialogListener, EditEventFragment.EditEventDialogListener{
    /**
 * Main activity for organizers to manage their events.
 * Displays a list of events created by the organizer and provides functionality
 * to add, view, edit, and delete events.
 */

    private static final String TAG = "OrganizerHomeActivity";
    private FirebaseFirestore db;
    private FloatingActionButton addEventButton;
    private RecyclerView recyclerView;
    private EventsAdapter eventsAdapter;
    private ArrayList<EventsModel> eventsModels = new ArrayList<>();
    private DocumentReference organizerRef;
    private String deviceId;
    private Button profileButton;

    /**
     * Called when the activity is first created.
     * Initializes UI components, Firebase, Cloudinary, and loads events.
     *
     * @param savedInstanceState Bundle containing the activity's previously saved state
     */
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

    /**
     * Navigates to the profile view screen.
     */
    private void goProfileScreen() {
        startActivity(new android.content.Intent(this, ProfileViewActivity.class));
    }

    /**
     * Configures the RecyclerView with a LinearLayoutManager and EventsAdapter.
     * Sets up click listeners for viewing, editing, and deleting events.
     */
    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventsAdapter = new EventsAdapter(this,eventsModels, new EventsAdapter.OnEventClickListener() {
            @Override
            public void onEventClick(EventsModel event, int position) {
                EventCreatedDialogFragment confirmDialog =
                        EventCreatedDialogFragment.newInstance(event, null);
                confirmDialog.show(getSupportFragmentManager(), "Event Created");
            }

            @Override
            public void onEditClick(EventsModel event, int position) {
                EditEventFragment editEventFragment = EditEventFragment.newInstance(event, position);;
                editEventFragment.show(getSupportFragmentManager(), "Edit Event");
            }

            @Override
            public void onDeleteClick(EventsModel event, int position) {
                deleteEvent(event, position);
            }
            @Override
            public void onSampleClick(EventsModel event, int position) {
                Intent intent = new Intent(OrganizerHomeActivity.this, SampleEntrantsActivity.class);
                intent.putExtra("eventId", event.getEventId());
                startActivity(intent);
            }

        });
        recyclerView.setAdapter(eventsAdapter);
    }

    /**
     * Loads all events associated with the current organizer from Firestore.
     * Retrieves event details including waiting lists and updates the RecyclerView.
     * Displays appropriate messages if no events are found or if errors occur.
     */
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
                                                            Object invitedListObj = eventDoc.get("InvitedList");
                                                            Object keyWordsListObj = eventDoc.get("Keywords");
                                                            ArrayList<String> keyWords = new ArrayList<>();
                                                            ArrayList<String> waitingList = new ArrayList<>();
                                                            ArrayList<String> invitedList = new ArrayList<>();

                                                            //fill waitingList
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

                                                            //fill invitedList
                                                            if (invitedListObj instanceof List) {
                                                                List<Object> invitedListRefs = (List<Object>) invitedListObj;
                                                                Log.d(TAG, "Found " + invitedListRefs.size() + " entrants in waiting list");

                                                                for (Object refObj : invitedListRefs) {
                                                                    if (refObj instanceof DocumentReference) {
                                                                        DocumentReference entrantRef = (DocumentReference) refObj;
                                                                        String entrantId = entrantRef.getId();
                                                                        invitedList.add(entrantId);
                                                                        Log.d(TAG, "Added entrant to invited list: " + entrantId);
                                                                    } else if (refObj instanceof String) {
                                                                        invitedList.add((String) refObj);
                                                                        Log.d(TAG, "Added entrant ID to invited list: " + refObj);
                                                                    }
                                                                }
                                                            } else {
                                                                Log.d(TAG, "No invited list found or empty");
                                                            }

                                                            //fill keyWords
                                                            if (keyWordsListObj instanceof List) {
                                                                List<Object> keyWordsListRefs = (List<Object>) keyWordsListObj;
                                                                Log.d(TAG, "Found " + keyWordsListRefs.size() + " entrants in waiting list");

                                                                for (Object refObj : keyWordsListRefs) {
                                                                        keyWords.add((String) refObj);
                                                                        Log.d(TAG, "Added keyword: " + refObj);
                                                                    }

                                                            } else {
                                                                Log.d(TAG, "No invited list found or empty");
                                                            }

                                                            EventsModel event = new EventsModel(

                                                                    eventDoc.getString("title"),
                                                                    keyWords,
                                                                    eventDoc.getDate("registration_start"),
                                                                    eventDoc.getDate("registration_end"),
                                                                    eventDoc.getString("description"),
                                                                    eventDoc.getDate("date"),
                                                                    eventDoc.getLong("attendees"),
                                                                    eventDoc.getLong("Signups"),
                                                                    waitingList,
                                                                    eventDoc.getString("imageUrl"),
                                                                    eventDoc.getId(),
                                                                    invitedList,
                                                                    eventDoc.getLong("maxWaitingListSize"),
                                                                    eventDoc.getString("organizerId")


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

    /**
     * Callback method for adding a new event.
     * If an image is provided, uploads it to Cloudinary before creating the event.
     * Otherwise, creates the event without an image.
     *
     * @param eventsModel The event model containing event details
     * @param imageUri The URI of the event image, or null if no image was selected
     */
    @Override
    public void addEvent(EventsModel eventsModel, Uri imageUri) {
        if (imageUri != null) {
            uploadImageToCloudinaryAndCreateEvent(eventsModel, imageUri);
        } else {
            createEventInFirestore(eventsModel, null);
        }
    }

    @Override
    public void editEvent(int position, EventsModel event, EventsModel eventsModel, Uri imageUri) {
        if (imageUri != null) {
            replaceImageToCloudinaryAndEditEvent(eventsModel, imageUri, position);
        } else {
            editEventInFirestore(eventsModel, null, position);
        }
    }




    /**
     * Uploads an event image to Cloudinary and creates the event upon successful upload.
     * Displays progress and error messages during the upload process.
     *
     * @param eventsModel The event model containing event details
     * @param imageUri The URI of the image to upload
     */
    private void replaceImageToCloudinaryAndEditEvent(EventsModel eventsModel, Uri imageUri, int position) {
        Log.d(TAG, "Uploading image to Cloudinary: " + imageUri);
        String publicId = "event_images/" + System.currentTimeMillis();


        MediaManager.get().upload(imageUri)
                .option("invalidate", "true")
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

                        runOnUiThread(() -> editEventInFirestore(eventsModel, imageUrl, position));

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
    private void uploadImageToCloudinaryAndCreateEvent(EventsModel eventsModel, Uri imageUri) {//uri not needed here
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

    /**
     * Creates a new event in Firestore and links it to the organizer.
     * Initializes the event with an empty waiting list and adds it to the local list.
     * Displays a confirmation dialog upon successful creation.
     *
     * @param eventsModel The event model containing event details
     * @param imageUrl The Cloudinary URL of the event image, or null if no image
     */
    private void createEventInFirestore(EventsModel eventsModel, String imageUrl) {
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("title", eventsModel.getEventTitle());
        eventData.put("Keywords", eventsModel.getSelectedTags());
        eventData.put("registration_start", eventsModel.getRegistrationStart());
        eventData.put("registration_end", eventsModel.getRegistrationEnd());
        eventData.put("description", eventsModel.getEventDescription());
        eventData.put("date", eventsModel.getDate());
        eventData.put("attendees", eventsModel.getAttendees());
        eventData.put("Signups", eventsModel.getSignups());
        eventData.put("imageUrl", imageUrl);
        eventData.put("waitingList", new ArrayList<>());
        eventData.put("maxWaitingListSize",eventsModel.getMaxWaitList());

        db.collection("Events")
                .add(eventData)
                .addOnSuccessListener(eventDocRef -> {

                    Log.d(TAG, "Event added with ID: " + eventDocRef.getId());
                    Toast.makeText(OrganizerHomeActivity.this, "Created Event!" , Toast.LENGTH_SHORT).show();
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
    private void editEventInFirestore(EventsModel eventsModel, String imageUrl, int position) {
        String eventId = eventsModel.getEventId();
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("title", eventsModel.getEventTitle());
        eventData.put("Keywords", eventsModel.getSelectedTags());
        eventData.put("registration_start", eventsModel.getRegistrationStart());
        eventData.put("registration_end", eventsModel.getRegistrationEnd());
        eventData.put("description", eventsModel.getEventDescription());
        eventData.put("date", eventsModel.getDate());
        eventData.put("attendees", eventsModel.getAttendees());
        eventData.put("imageUrl", imageUrl);
        eventData.put("maxWaitingListSize", eventsModel.getMaxWaitList());

        Log.d(TAG, "Updating event: " + eventId);

        db.collection("Events")
                .document(eventId)
                .set(eventData, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {  // ← Changed from eventDocRef
                    Log.d(TAG, "Event successfully updated in Firestore");
                    Toast.makeText(OrganizerHomeActivity.this, "Updated Event!" , Toast.LENGTH_SHORT).show();
                    eventsModel.setImageUrl(imageUrl);

                    // Update the model in the list
                    eventsModels.set(position, eventsModel);

                    // Use notifyItemChanged instead of notifyItemInserted
                    eventsAdapter.notifyItemChanged(position);

                    // Optional: scroll to the updated item
                    recyclerView.smoothScrollToPosition(position);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error updating event", e);
                    Toast.makeText(OrganizerHomeActivity.this,
                            "Failed to update event",
                            Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Displays a confirmation dialog and deletes an event from Firestore.
     * Removes the event from the local list and logs information about waiting list cleanup.
     * The event's image remains in Cloudinary after deletion.
     *
     * @param event The event to delete
     * @param position The position of the event in the RecyclerView
     */
    private void deleteEvent(EventsModel event, int position) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Delete Event")
                .setMessage("Are you sure you want to delete \"" + event.getEventTitle() + "\"?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    String eventId = event.getEventId();

                    if (eventId != null) {
                        db.collection("Events").document(eventId)
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    Log.d(TAG, "Event deleted from Firestore");

                                    if (event.getWaitingListSize() > 0) {
                                        // TODO: Implement notification to entrants
                                    }

                                    // Delete image from Cloudinary if exists
                                    if (event.getImageUrl() != null && !event.getImageUrl().isEmpty()) {
                                        Pattern pattern = Pattern.compile("/upload/(?:v\\d+/)?(.+?)(?:\\.[^.]+)?$");
                                        Matcher matcher = pattern.matcher(event.getImageUrl());

                                        if (matcher.find()) {
                                            String publicId = matcher.group(1); // Extract the public ID

                                            ExecutorService executor = Executors.newSingleThreadExecutor();
                                            executor.execute(() -> {
                                                try {
                                                    MediaManager.get().getCloudinary().uploader()
                                                            .destroy(publicId, ObjectUtils.emptyMap());

                                                    runOnUiThread(() -> {
                                                        Log.d(TAG, "Image deleted from Cloudinary");
                                                        updateUIAfterDelete(position);
                                                    });

                                                } catch (Exception e) {
                                                    Log.e(TAG, "Error deleting from Cloudinary", e);
                                                    runOnUiThread(() -> {
                                                        Toast.makeText(this,
                                                                "Event deleted but image removal failed",
                                                                Toast.LENGTH_SHORT).show();
                                                        updateUIAfterDelete(position);
                                                    });
                                                }
                                            });
                                        } else {
                                            updateUIAfterDelete(position);
                                        }
                                    } else {
                                        updateUIAfterDelete(position);
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Error deleting event from Firestore", e);
                                    Toast.makeText(this,
                                            "Failed to delete event",
                                            Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // Helper method to update UI after deletion
    private void updateUIAfterDelete(int position) {
        eventsModels.remove(position);
        eventsAdapter.notifyItemRemoved(position);
        Toast.makeText(this, "Event deleted successfully", Toast.LENGTH_SHORT).show();
    }


}