package com.example.fusion1_events;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminImageBrowserActivity extends AppCompatActivity {

    private static final String TAG = "AdminImageBrowser";
    private RecyclerView recyclerView;
    private AdminImageAdapter adapter;
    private List<EventItem> eventList = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_image_browser);

        recyclerView = findViewById(R.id.recyclerAdminImages);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        adapter = new AdminImageAdapter(this, eventList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        loadEventsFromFirestore();
    }

    private void loadEventsFromFirestore() {
        db.collection("events")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    eventList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        EventItem event = new EventItem();

                        // Load imageUrl (exists in Firestore)
                        event.setImageUrl(doc.getString("imageUrl"));

                        // organizerName is not in your example, use a placeholder
                        //event.setOrganizerName("Unknown Organizer");

                        // date is a Timestamp in Firestore, convert to readable string
                        if (doc.getTimestamp("date") != null) {
                            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
                                    "MMM dd, yyyy HH:mm", java.util.Locale.getDefault()
                            );
                            String formattedDate = sdf.format(doc.getTimestamp("date").toDate());
                            event.setDate(formattedDate);
                        } else {
                            event.setDate("No date");
                        }

                        eventList.add(event);
                    }
                    adapter.notifyDataSetChanged();
                    Toast.makeText(this, "Loaded " + eventList.size() + " events", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading events", e);
                    Toast.makeText(this, "Failed to load events: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

}
