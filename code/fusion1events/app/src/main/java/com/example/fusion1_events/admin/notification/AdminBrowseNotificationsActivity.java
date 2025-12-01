package com.example.fusion1_events.admin.notification;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fusion1_events.R;
import com.example.fusion1_events.admin.NavBarHelper;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Activity that allows an admin to browse all notifications logged in Firestore.
 */
public class AdminBrowseNotificationsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private AdminNotificationAdapter adapter;
    private List<AdminNotification> notifications = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_browse_notifications);

        // Initialize recycler view and set its layout manager
        recyclerView = findViewById(R.id.recyclerNotifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter with the list of events and event action listener
        adapter = new AdminNotificationAdapter(notifications);
        recyclerView.setAdapter(adapter);

        // Set up navigation bar for admin users
        NavBarHelper.setupNavBar(this, AdminBrowseNotificationsActivity.class);

        loadNotifications();

        // Set up back button to return to the previous screen
        ImageButton backButton = findViewById(R.id.buttonBack);
        backButton.setOnClickListener(v -> finish());

        // Listen for real-time updates to the "Notifications" collection.
        db.collection("Notifications")
                .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .addSnapshotListener((doc, e) -> {
            if (doc != null) {
                notifications.clear();
                List<AdminNotification> newNotificationss = doc.getDocuments()
                        .stream()
                        .map(this::docToNotification)
                        .collect(Collectors.toList());

                notifications.addAll(newNotificationss);
                Log.d("FirestoreDebug", newNotificationss.toString());
                adapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * Loads all events from the Firestore "Events" collection and populates the RecyclerView.
     */
    private void loadNotifications() {
        db.collection("Notifications")
                .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    notifications.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        // Convert each Firestore document into an Event object
                        AdminNotification notification = docToNotification(doc);
                        notifications.add(notification);
                    }
                    adapter.notifyDataSetChanged();
                    Log.d("Firestore", "Notifications loaded successfully");
                })
                .addOnFailureListener(e ->
                        Log.e("Firestore", "Error loading notifications", e));
    }

    /**
     * Converts a Firestore DocumentSnapshot into an AdminNotification object and assigns the document's ID to the AdminNotification.
     *
     * @param doc the Firestore document containing notificatiion data
     * @return an AdminNotification object with populated fields and Firestore ID
     */
    private @NonNull AdminNotification docToNotification(@NonNull DocumentSnapshot doc) {
        AdminNotification notification = doc.toObject(AdminNotification.class);
        notification.setId(doc.getId());
        return notification;
    }
}
