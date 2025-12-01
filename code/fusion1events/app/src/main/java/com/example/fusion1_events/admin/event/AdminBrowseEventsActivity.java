package com.example.fusion1_events.admin.event;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fusion1_events.Event;
import com.example.fusion1_events.R;
import com.example.fusion1_events.admin.NavBarHelper;
import com.example.fusion1_events.admin.profile.AdminProfile;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Activity that allows an admin to browse all events stored in Firestore.
 * <p>
 * Features:
 * <ul>
 *     <li>Displays events in a RecyclerView.</li>
 *     <li>Shows real-time updates using Firestore snapshot listeners.</li>
 *     <li>Includes a back button to return to the previous screen.</li>
 * </ul>
 */
public class AdminBrowseEventsActivity extends AppCompatActivity{
    public static final String TAG = "FirestoreDebugBrowseEvents";
    private RecyclerView recyclerView;
    private AdminEventAdapter adapter;
    private List<Event> events = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * Initializes the activity.
     * <p>
     * Sets up the RecyclerView, adapter, navigation bar, back button.
     * Loads events from Firestore, and listens for real-time updates.
     *
     * @param savedInstanceState Bundle containing activity's previously saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_browse_events);

        // Initialize recycler view and set its layout manager
        recyclerView = findViewById(R.id.recyclerEvents);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter with the list of events and event action listener
        adapter = new AdminEventAdapter(events);
        recyclerView.setAdapter(adapter);

        // Set up navigation bar for admin users
        NavBarHelper.setupNavBar(this, AdminBrowseEventsActivity.class);

        loadEvents();

        // Set up back button to return to the previous screen
        ImageButton backButton = findViewById(R.id.buttonBack);
        backButton.setOnClickListener(v -> finish());

        // Listen for real-time updates to the "Events" collection.
        db.collection("Events").addSnapshotListener((doc, e) -> {
            if (doc != null) {
                events.clear();
                List<Event> newEvents = doc.getDocuments()
                        .stream()
                        .map(this::docToEvent)
                        .collect(Collectors.toList());

                events.addAll(newEvents);
                Log.d(TAG, newEvents.toString());
                adapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * Loads all events from the Firestore "Events" collection.
     * <p>
     * Clears the current list, converts each document to an Event object and updates
     * the RecyclerView adapter.
     */
    private void loadEvents() {
        db.collection("Events")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    events.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        // Convert each Firestore document into an Event object
                        Event event = docToEvent(doc);
                        events.add(event);

                        if (event.getRegistration_end() != null) {
                            Log.d(TAG, "registration_end: " + event.getRegistration_end().toDate());
                        } else {
                            Log.d(TAG, "registration_end: null");
                        }
                    }
                    adapter.notifyDataSetChanged();
                    Log.d(TAG, "Events loaded successfully");
                })
                .addOnFailureListener(e ->
                        Log.e(TAG, "Error loading events", e));
    }

    /**
     * Converts a Firestore DocumentSnapshot into an Event object and assigns the document's ID to the Event.
     *
     * @param doc the Firestore document containing event data
     * @return an Event object with populated fields and Firestore ID
     */
    private @NonNull Event docToEvent(@NonNull DocumentSnapshot doc) {
        Event event = doc.toObject(Event.class);
        event.setId(doc.getId());
        return event;
    }
}
