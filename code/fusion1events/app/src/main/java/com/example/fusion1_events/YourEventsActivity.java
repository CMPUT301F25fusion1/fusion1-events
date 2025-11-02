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
import java.util.List;

public class YourEventsActivity extends AppCompatActivity {
    private RecyclerView yourEventsRecyclerView;
    private EventAdapter adapter;
    private List<Event> userEventList;
    private Profile currentUser;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_events);

        currentUser = (Profile) getIntent().getSerializableExtra("currentUser");
        db = FirebaseFirestore.getInstance();
        userEventList = new ArrayList<>();

        yourEventsRecyclerView = findViewById(R.id.your_events_recycler_view);
        yourEventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

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

        adapter = new EventAdapter(this, userEventList, currentUser);
        yourEventsRecyclerView.setAdapter(adapter);

        db.collection("Events")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Event event = doc.toObject(Event.class);
                        if (event != null) {
                            Log.d("FirestoreEvent", event.getName());
                            if (event.getWaitingList() != null && event.getWaitingList().contains(currentUser.getName())) {
                                userEventList.add(event);
                                Toast.makeText(this, "Added " + event.getName() + " to userEventList", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "Failed to map document: " + doc.getId(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load your events: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}
