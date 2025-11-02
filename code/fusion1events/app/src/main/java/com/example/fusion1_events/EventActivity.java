package com.example.fusion1_events;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EventActivity extends AppCompatActivity {
    private RecyclerView eventsRecyclerView;
    private EventAdapter adapter;
    private List<Event> eventList;
    private Profile currentUser;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_screen);

        currentUser = (Profile) getIntent().getSerializableExtra("currentUser");
        db = FirebaseFirestore.getInstance();
        eventList = new ArrayList<>();

        eventsRecyclerView = findViewById(R.id.events_recycler_view);
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        TextView tvHome = findViewById(R.id.tvHome);
        TextView tvYourEvents = findViewById(R.id.tvYourEvents);

        tvHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, EventActivity.class);
            intent.putExtra("currentUser", currentUser);
            startActivity(intent);
        });

        tvYourEvents.setOnClickListener(v -> {
            Intent intent = new Intent(this, YourEventsActivity.class);
            intent.putExtra("currentUser", currentUser);
            startActivity(intent);
        });

        adapter = new EventAdapter(this, eventList, currentUser);
        eventsRecyclerView.setAdapter(adapter);

        db.collection("Events")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Event event = doc.toObject(Event.class);
                        if (event != null) {
                            Log.d("FirestoreEvent", event.getName());
                            eventList.add(event);
                        } else {
                            Log.d("FirestoreEvent", "Failed to map document: " + doc.getId());
                        }
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load events: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}
