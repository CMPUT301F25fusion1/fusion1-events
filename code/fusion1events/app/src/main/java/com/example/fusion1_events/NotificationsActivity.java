package com.example.fusion1_events;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;
import com.google.firebase.installations.FirebaseInstallations;

import java.util.ArrayList;
import java.util.List;

public class NotificationsActivity extends AppCompatActivity {

    private static final String TAG = "NotificationsActivity";

    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private List<NotificationModel> notificationItems = new ArrayList<>();

    private CollectionReference notifRef;
    private CollectionReference entrantsRef;

    private CollectionReference profileRef;

    private Profile currentUser;  // optional, but matches EventDetailActivity usage

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrant_notifications);


        profileRef = DatabaseReferences.getProfileDatabase();

        currentUser = (Profile) getIntent().getSerializableExtra("currentUser");

        recyclerView = findViewById(R.id.notificationsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        notifRef = DatabaseReferences.getNotificationDatabase();
        entrantsRef = DatabaseReferences.getEntrantsDatabase();

        adapter = new NotificationAdapter(this, notificationItems, notifRef, currentUser);
        recyclerView.setAdapter(adapter);

        loadNotificationsForCurrentEntrant();


        FirebaseInstallations.getInstance().getId().addOnSuccessListener(deviceId -> {

            profileRef.document(deviceId).get().addOnSuccessListener(profile -> {

                if (profile.exists()) {
                    currentUser = profile.toObject(Profile.class);

                    TextView tvHome = findViewById(R.id.tvHome);
                    TextView tvYourEvents = findViewById(R.id.tvYourEvents);
                    TextView tvYourProfile = findViewById(R.id.tvYourProfile);
                    ImageView tvDetailImage = findViewById(R.id.ivDetailImage);
                    TextView tvNotifications = findViewById(R.id.tvNotifications);


                    tvYourProfile.setOnClickListener(v -> {
                        Intent intent = new Intent(NotificationsActivity.this, ProfileViewActivity.class);
                        startActivity(intent);
                    });

                    tvHome.setOnClickListener(v -> {
                        Intent intent = new Intent(NotificationsActivity.this, EntrantHomeActivity.class);
                        intent.putExtra("currentUser", currentUser);
                        startActivity(intent);
                    });

                    tvYourEvents.setOnClickListener(v -> {
                        Intent intent = new Intent(NotificationsActivity.this, YourEventsActivity.class);
                        intent.putExtra("currentUser", currentUser);
                        startActivity(intent);
                    });

                    tvNotifications.setOnClickListener(v -> {
                        Intent intent = new Intent(NotificationsActivity.this, NotificationsActivity.class);
                        intent.putExtra("currentUser", currentUser);
                        startActivity(intent);
                    });
                }
            });
        });
    }

    private void loadNotificationsForCurrentEntrant() {
        FirebaseInstallations.getInstance().getId().addOnSuccessListener(deviceId -> {

            DocumentReference entrantRef = entrantsRef.document(deviceId);

            notifRef.whereEqualTo("receiverId", entrantRef)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        notificationItems.clear();

                        querySnapshot.forEach(doc -> {
                            NotificationModel item = NotificationModel.fromSnapshot(doc);
                            notificationItems.add(item);
                        });

                        // Sort newest first (createdAt descending)
                        java.util.Collections.sort(notificationItems, (a, b) -> {
                            if (a.getCreatedAt() == null && b.getCreatedAt() == null) return 0;
                            if (a.getCreatedAt() == null) return 1;
                            if (b.getCreatedAt() == null) return -1;
                            return b.getCreatedAt().compareTo(a.getCreatedAt());
                        });

                        adapter.notifyDataSetChanged();
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error loading notifications", e);
                        Toast.makeText(this, "Failed to load notifications.", Toast.LENGTH_SHORT).show();
                    });

        }).addOnFailureListener(e -> {
            Log.e(TAG, "Failed to get device id", e);
            Toast.makeText(this, "Failed to get user id.", Toast.LENGTH_SHORT).show();
        });
    }

}
