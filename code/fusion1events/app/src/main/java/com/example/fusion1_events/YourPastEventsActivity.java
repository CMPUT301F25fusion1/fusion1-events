package com.example.fusion1_events;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.installations.FirebaseInstallations;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class YourPastEventsActivity extends AppCompatActivity {
    private RecyclerView yourEventsRecyclerView;
    private EventAdapter adapter;
    private List<Event> userEventList;
    private Profile currentUser;
    private FirebaseFirestore db;
    private String deviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_past_events);

        currentUser = (Profile) getIntent().getSerializableExtra("currentUser");
        db = FirebaseFirestore.getInstance();
        userEventList = new ArrayList<>();

        yourEventsRecyclerView = findViewById(R.id.past_events_recycler_view);
        yourEventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        TextView tvHome = findViewById(R.id.tvHome);
        TextView tvYourEvents = findViewById(R.id.tvYourEvents);
        TextView tvCurrentEvents = findViewById(R.id.tvCurrentEvents);
        TextView tvYourProfile = findViewById(R.id.tvYourProfile);

        // Menu listeners
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

        tvYourProfile.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileViewActivity.class);
            startActivity(intent);
        });

        tvCurrentEvents.setOnClickListener(v -> {
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
                            if (event != null) {
                                Date currentDate = new Date();
                                Date eventDate = event.getDate().toDate();
                                boolean inWaitingList = event.getWaitingList() != null && event.getWaitingList().contains(userRef);
                                boolean inFinalList   = event.getInvitedList() != null && event.getInvitedList().contains(userRef);
                                if (inWaitingList || inFinalList) {
                                    if (currentDate.after(eventDate)) {
                                        userEventList.add(event);
                                    }
                                }
                            }
                        }
                        adapter.notifyDataSetChanged();
                    });
        });
    }
}
