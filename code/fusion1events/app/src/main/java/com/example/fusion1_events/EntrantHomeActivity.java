package com.example.fusion1_events;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.installations.FirebaseInstallations;

import java.util.ArrayList;
import java.util.List;

public class EntrantHomeActivity extends AppCompatActivity {

    private CollectionReference profileRef;
    private CollectionReference eventsRef;
    private RecyclerView eventsRecyclerView;
    private EventAdapter adapter;
    private List<Event> eventList;
    private Profile currentUser;

    private Button profileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_event_screen);

        profileRef = DatabaseReferences.getProfileDatabase();
        eventsRef = DatabaseReferences.getEvents();
        eventsRecyclerView = findViewById(R.id.events_recycler_view);
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventList = new ArrayList<>();

        profileButton = findViewById(R.id.buttonProfileEntrantHome);
        profileButton.setOnClickListener(v -> goProfileScreen());

        FirebaseInstallations.getInstance().getId().addOnSuccessListener(deviceId -> {

            profileRef.document(deviceId).get().addOnSuccessListener(profile -> {

                if (profile.exists()) {
                    currentUser = profile.toObject(Profile.class);

                    TextView tvHome = findViewById(R.id.tvHome);
                    TextView tvYourEvents = findViewById(R.id.tvYourEvents);

                    tvHome.setOnClickListener(v -> {
                        Intent intent = new Intent(EntrantHomeActivity.this, EntrantHomeActivity.class);
                        intent.putExtra("currentUser", currentUser);
                        startActivity(intent);
                    });

                    tvYourEvents.setOnClickListener(v -> {
                        Intent intent = new Intent(EntrantHomeActivity.this, YourEventsActivity.class);
                        intent.putExtra("currentUser", currentUser);
                        startActivity(intent);
                    });

                    adapter = new EventAdapter(this, eventList, currentUser);
                    eventsRecyclerView.setAdapter(adapter);

                    eventsRef
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                for (DocumentSnapshot doc : queryDocumentSnapshots) {
                                    Event event = doc.toObject(Event.class);
                                    eventList.add(event);
                                }
                                adapter.notifyDataSetChanged();
                            });
                }
            });
        });

    }

    private void goProfileScreen(){
        startActivity(new android.content.Intent(this, ProfileViewActivity.class));
        finish();
    }
    }


