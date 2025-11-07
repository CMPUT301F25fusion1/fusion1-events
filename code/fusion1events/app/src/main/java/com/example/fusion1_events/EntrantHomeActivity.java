package com.example.fusion1_events;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.installations.FirebaseInstallations;

import java.util.ArrayList;
import java.util.List;
/**
 * File: EntrantHomeActivity.java
 *
 * Role:
 * - Acts as the home screen for users with the Entrant role.
 * - Retrieves the current user’s profile using their device ID.
 * - Displays a list of all available events from Firestore.
 * - Provides navigation to:
 *      - Home screen
 *      - Entrant's events screen
 *      - Entrant's profile screen
 * - Initializes and binds an EventAdapter to show event cards for the entrant.
 *
 * Issues:
 * - Assumes successful retrieval of the Firebase Installation ID.
 * - Assumes device is online for Firestore operations.
 * - No explicit error handling for missing profiles or failed Firestore queries.
 */
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



        FirebaseInstallations.getInstance().getId().addOnSuccessListener(deviceId -> {

            profileRef.document(deviceId).get().addOnSuccessListener(profile -> {

                if (profile.exists()) {
                    currentUser = profile.toObject(Profile.class);

                    TextView tvHome = findViewById(R.id.tvHome);
                    TextView tvYourEvents = findViewById(R.id.tvYourEvents);
                    TextView tvYourProfile = findViewById(R.id.tvYourProfile);
                    ImageView tvDetailImage = findViewById(R.id.ivDetailImage);


                    tvYourProfile.setOnClickListener(v -> {
                        Intent intent = new Intent(EntrantHomeActivity.this, ProfileViewActivity.class);
                        startActivity(intent);
                    });

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

    }


