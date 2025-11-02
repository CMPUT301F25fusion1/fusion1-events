package com.example.fusion1_events;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class EventDetailActivity extends AppCompatActivity {
    private ImageView ivDetailImage;
    private TextView tvDetailTitle, tvDetailDate, tvDetailTime, tvDetailLocation, tvDetailPrice,
            tvDetailDeadline, tvDetailTotalEntrants, tvDetailWaitingList, tvDetailOrganizer,
            tvDetailDescription;
    private Button btnScanQR, btnJoinWaitingList, btnLeaveWaitingList;
    private Event event;
    private Profile currentUser;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        db = FirebaseFirestore.getInstance();

        event = (Event) getIntent().getSerializableExtra("event");
        currentUser = (Profile) getIntent().getSerializableExtra("currentUser");

        ivDetailImage = findViewById(R.id.ivDetailImage);
        tvDetailTitle = findViewById(R.id.tvDetailTitle);
        tvDetailDate = findViewById(R.id.tvDetailDate);
        tvDetailTime = findViewById(R.id.tvDetailTime);
        tvDetailLocation = findViewById(R.id.tvDetailLocation);
        tvDetailPrice = findViewById(R.id.tvDetailPrice);
        tvDetailDeadline = findViewById(R.id.tvDetailDeadline);
        tvDetailTotalEntrants = findViewById(R.id.tvDetailTotalEntrants);
        tvDetailWaitingList = findViewById(R.id.tvDetailWaitingList);
        tvDetailOrganizer = findViewById(R.id.tvDetailOrganizer);
        tvDetailDescription = findViewById(R.id.tvDetailDescription);

        btnScanQR = findViewById(R.id.btnScanQR);
        btnJoinWaitingList = findViewById(R.id.btnJoinWaitingList);
        btnLeaveWaitingList = findViewById(R.id.btnLeaveWaitingList);

        if (event.getWaitingList().contains(currentUser.getName())) {
            btnJoinWaitingList.setVisibility(View.GONE);
            btnLeaveWaitingList.setVisibility(View.VISIBLE);
        } else {
            btnJoinWaitingList.setVisibility(View.VISIBLE);
            btnLeaveWaitingList.setVisibility(View.GONE);
        }

        tvDetailTitle.setText(event.getName());
        tvDetailDate.setText(event.getDate());
        tvDetailTime.setText(event.getTime());
        tvDetailLocation.setText(event.getLocation());
        tvDetailPrice.setText(event.getPrice());
        tvDetailDeadline.setText(event.getRegistrationDeadline());
        tvDetailTotalEntrants.setText(String.valueOf(event.getTotalEntrants()));
        tvDetailWaitingList.setText(event.getWaitingListMax() + ""); // show capacity
        tvDetailOrganizer.setText(event.getOrganizer());
        tvDetailDescription.setText(event.getDescription());
        ivDetailImage.setImageResource(event.getImageResId());

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

        btnScanQR.setOnClickListener(v -> {
            Toast.makeText(this, "Scan QR functionality coming soon!", Toast.LENGTH_SHORT).show();
        });

        btnJoinWaitingList.setOnClickListener(v -> joinWaitingList());
        btnLeaveWaitingList.setOnClickListener(v -> leaveWaitingList());
    }
    private void joinWaitingList() {
        if (event.getWaitingList().contains(currentUser.getEmail())) {
            Toast.makeText(this, "You already joined this waiting list!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (event.getWaitingListSize() >= event.getWaitingListMax()) {
            Toast.makeText(this, "Waiting list is full.", Toast.LENGTH_SHORT).show();
            return;
        }
        event.addEntrant(currentUser.getName());
        Toast.makeText(this, "You joined the waiting list!", Toast.LENGTH_SHORT).show();

        DocumentReference eventRef = db.collection("Events").document(event.getId());
        eventRef.update("waitingList", event.getWaitingList())
                .addOnSuccessListener(aVoid -> {
                    btnLeaveWaitingList.setVisibility(View.VISIBLE);
                    btnJoinWaitingList.setVisibility(View.GONE);
                    tvDetailTotalEntrants.setText(String.valueOf(event.getTotalEntrants()));
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error updating database: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show());
    }
    private void leaveWaitingList() {
        List<String> waitingList = event.getWaitingList();
        waitingList.remove(currentUser.getName());

        DocumentReference eventRef = db.collection("Events").document(event.getId());
        eventRef.update("waitingList", waitingList)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "You left the waiting list.", Toast.LENGTH_SHORT).show();
                    tvDetailTotalEntrants.setText(String.valueOf(event.getTotalEntrants()));
                    btnJoinWaitingList.setVisibility(View.VISIBLE);
                    btnLeaveWaitingList.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> Toast.makeText(this,
                        "Error updating database: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show());
    }
}
