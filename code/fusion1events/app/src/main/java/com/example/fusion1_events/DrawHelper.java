package com.example.fusion1_events;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DrawHelper {

    private static final String TAG = "DrawHelper";
    public static void runDraw(String eventId, FirebaseFirestore db, Context context) {

        DocumentReference eventRef = db.collection("Events").document(eventId);

        eventRef.get().addOnSuccessListener(doc -> {

            if (!doc.exists()) {
                Log.e(TAG, "Event not found");
                if (context != null) Toast.makeText(context, "Event not found", Toast.LENGTH_SHORT).show();
                return;
            }

            int attendees = doc.getLong("attendees") != null ? doc.getLong("attendees").intValue() : 0;

            // Load waitingList
            List<DocumentReference> waitingList = new ArrayList<>();
            List<Object> rawWaiting = (List<Object>) doc.get("waitingList");
            if (rawWaiting != null) {
                for (Object o : rawWaiting) {
                    if (o instanceof DocumentReference) waitingList.add((DocumentReference) o);
                }
            }

            // Load invitedList
            List<DocumentReference> invitedList = new ArrayList<>();
            List<Object> rawInvited = (List<Object>) doc.get("invitedList");
            if (rawInvited != null) {
                for (Object o : rawInvited) {
                    if (o instanceof DocumentReference) invitedList.add((DocumentReference) o);
                }
            }

            int spaceLeft = attendees - invitedList.size();

            if (spaceLeft <= 0) {
                Log.d(TAG, "No more spaces available.");
                if (context != null) Toast.makeText(context, "No more spaces available.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (waitingList.isEmpty()) {
                Log.d(TAG, "Waiting list empty.");
                if (context != null) Toast.makeText(context, "Waiting list empty.", Toast.LENGTH_SHORT).show();
                return;
            }

            List<DocumentReference> selected = new ArrayList<>();

            // CASE A: waitingList <= spaceLeft → move all
            if (waitingList.size() <= spaceLeft) {
                selected.addAll(waitingList);
                invitedList.addAll(waitingList);
                waitingList.clear();
            }
            // CASE B: waitingList > spaceLeft → lottery
            else {
                List<DocumentReference> shuffled = new ArrayList<>(waitingList);
                Collections.shuffle(shuffled);

                selected = shuffled.subList(0, spaceLeft);
                invitedList.addAll(selected);
                waitingList.removeAll(selected);
            }

            // Update Firestore
            eventRef.update(
                    "invitedList", invitedList,
                    "waitingList", waitingList
            ).addOnSuccessListener(aVoid -> {
                Log.d(TAG, "Draw completed.");
                if (context != null) {
                    Toast.makeText(context, "Draw completed", Toast.LENGTH_SHORT).show();
                    Toast.makeText(context, "Draw completed", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> {
                Log.e(TAG, "Error updating draw", e);
                if (context != null) {
                    Toast.makeText(context, "Error updating draw", Toast.LENGTH_SHORT).show();
                }
            });

        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error fetching event", e);
            if (context != null) {
                Toast.makeText(context, "Error fetching event", Toast.LENGTH_SHORT).show();
            }
        });
    }
}