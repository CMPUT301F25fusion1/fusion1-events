package com.example.fusion1_events.admin;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fusion1_events.Event;
import com.example.fusion1_events.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity that allows an admin to browse all events stored in Firestore.
 * Admins can view, and delete events from this screen.
 */
public class AdminBrowseEventsActivity extends AppCompatActivity{
    private RecyclerView recyclerView;
    private AdminEventAdapter adapter;
    private List<Event> events = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

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
        Button backButton = findViewById(R.id.buttonBack);
        backButton.setOnClickListener(v -> finish());
    }

    /**
     * Loads all events from the Firestore "Events" collection and populates the RecyclerView.
     */
    private void loadEvents() {
        db.collection("Events")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    events.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        // Convert each Firestore document into an Event object
                        Event event = doc.toObject(Event.class);
                        event.setId(doc.getId());
                        events.add(event);

                        if (event.getRegistration_end() != null) {
                            Log.d("FirestoreDebug", "registration_end: " + event.getRegistration_end().toDate());
                        } else {
                            Log.d("FirestoreDebug", "registration_end: null");
                        }
                    }
                    adapter.notifyDataSetChanged();
                    Log.d("Firestore", "Events loaded successfully");
                })
                .addOnFailureListener(e ->
                        Log.e("Firestore", "Error loading events", e));
    }
}
