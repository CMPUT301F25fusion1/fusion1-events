package com.example.fusion1_events;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminBrowseEventsActivity extends AppCompatActivity implements AdminEventAdapter.onEventActionListener{
    private RecyclerView recyclerView;
    private AdminEventAdapter adapter;
    private List<Event> events = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_browse_events);

        recyclerView = findViewById(R.id.recyclerEvents);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminEventAdapter(events, this);
        recyclerView.setAdapter(adapter);

        NavBarHelper.setupNavBar(this, AdminBrowseEventsActivity.class);

        loadEvents();

        // back button
        Button backButton = findViewById(R.id.buttonBack);
        backButton.setOnClickListener(v -> finish());
    }

    private void loadEvents() {
        db.collection("Events")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    events.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
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

    @Override
    public void onDeleteEvent(Event event) {
        String eventId = event.getId();
        if (eventId == null) return;

        db.collection("Events").document(eventId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    events.remove(event);
                    adapter.notifyDataSetChanged();
                    Log.d("Firestore", "Event deleted successfully");
                })
                .addOnFailureListener(e ->
                        Log.e("Firestore", "Error deleting event"));
    }
}
