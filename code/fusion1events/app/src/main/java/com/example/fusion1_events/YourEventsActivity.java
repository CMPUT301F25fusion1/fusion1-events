package com.example.fusion1_events;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.installations.FirebaseInstallations;

import java.util.ArrayList;
import java.util.List;
/**
 * YourEventsActivity displays the list of events the current user has joined or is on the waiting list for.
 *
 * The activity retrieves the user's events from Firestore, shows them in a RecyclerView using EventAdapter,
 * and allows navigation back to the home screen.
 */
public class YourEventsActivity extends AppCompatActivity {
    private RecyclerView yourEventsRecyclerView;
    private EventAdapter adapter;
    private List<Event> userEventList;
    private Profile currentUser;
    private FirebaseFirestore db;
    private String deviceId;

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
            Intent intent = new Intent(this, EntrantHomeActivity.class);
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

        FirebaseInstallations.getInstance().getId().addOnSuccessListener(id -> {
            deviceId = id;
            DocumentReference userRef = db.collection("Entrants").document(deviceId);

            db.collection("Events")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            Event event = doc.toObject(Event.class);
                            if (event != null && event.getWaitingList() != null) {
                                if (event.getWaitingList().contains(userRef)) {
                                    userEventList.add(event);
                                }
                            }
                        }
                        adapter.notifyDataSetChanged();
                    });
        });
    }
}
