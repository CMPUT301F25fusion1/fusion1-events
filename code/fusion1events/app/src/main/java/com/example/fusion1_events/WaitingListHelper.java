package com.example.fusion1_events;

import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class WaitingListHelper {
    private static final String TAG = "WaitingListHelper";
    private FirebaseFirestore db;

    public WaitingListHelper() {
        this.db = FirebaseFirestore.getInstance();
    }

    public void addToWaitingList(String eventId, String entrantId, WaitingListCallback callback) {
        // Get reference to the entrant document
        DocumentReference entrantRef = db.collection("Entrants").document(entrantId);

        // Add the entrant reference to the event's waiting list array
        db.collection("Events").document(eventId)
                .update("waitingList", FieldValue.arrayUnion(entrantRef))
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Added entrant " + entrantId + " to event " + eventId);
                    callback.onSuccess("Entrant added to waiting list");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to add entrant to waiting list", e);
                    callback.onFailure(e.getMessage());
                });
    }

    public void removeFromWaitingList(String eventId, String entrantId, WaitingListCallback callback) {
        // Get reference to the entrant document
        DocumentReference entrantRef = db.collection("Entrants").document(entrantId);

        // Remove the entrant reference from the event's waiting list array
        db.collection("Events").document(eventId)
                .update("waitingList", FieldValue.arrayRemove(entrantRef))
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Removed entrant " + entrantId + " from event " + eventId);
                    callback.onSuccess("Entrant removed from waiting list");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to remove entrant from waiting list", e);
                    callback.onFailure(e.getMessage());
                });
    }
    public void getWaitingList(String eventId, WaitingListDataCallback callback) {
        db.collection("Events").document(eventId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Object waitingListObj = documentSnapshot.get("waitingList");
                        ArrayList<String> entrantIds = new ArrayList<>();

                        if (waitingListObj instanceof List) {
                            List<Object> waitingListRefs = (List<Object>) waitingListObj;

                            for (Object entrantObj : waitingListRefs) {
                                if (entrantObj instanceof DocumentReference) {
                                    DocumentReference entrantRef = (DocumentReference) entrantObj;
                                    entrantIds.add(entrantRef.getId());
                                }
                            }
                        }

                        callback.onSuccess(entrantIds);
                        Log.d(TAG, "Retrieved " + entrantIds.size() + " entrants from waiting list");
                    } else {
                        callback.onFailure("Event not found");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to get waiting list", e);
                    callback.onFailure(e.getMessage());
                });
    }

    public interface WaitingListCallback {
        void onSuccess(String message);
        void onFailure(String error);
    }

    public interface WaitingListDataCallback {
        void onSuccess(ArrayList<String> entrantIds);
        void onFailure(String error);
    }
}