package com.example.fusion1_events;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationHelperClass {

    private static final String TAG = "NotificationHelperClass";

    private static final CollectionReference eventsRef =
            DatabaseReferences.getEvents();

    private static final CollectionReference notifRef =
            DatabaseReferences.getNotificationDatabase();

    private static final CollectionReference organizerRef =
            DatabaseReferences.getOrganizersDatabase();


    public static void sendWaitingListNotifications(Context context,
                                                    String eventId,
                                                    String organizerId) {
        Log.d(TAG, "sendWaitingListNotifications called for eventId=" + eventId);

        eventsRef.document(eventId).get()
                .addOnSuccessListener(eventDoc -> {
                    if (!eventDoc.exists()) {
                        Log.d(TAG, "Event not found for waiting list notifications");
                        Toast.makeText(context, "Event not found.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String eventTitle = eventDoc.getString("title");
                    if (eventTitle == null || eventTitle.trim().isEmpty()) {
                        eventTitle = "this event";
                    }

                    @SuppressWarnings("unchecked")
                    List<DocumentReference> waitingList =
                            (List<DocumentReference>) eventDoc.get("waitingList");

                    if (waitingList == null || waitingList.isEmpty()) {
                        Log.d(TAG, "No entrants in waitingList to notify.");
                        return;
                    }

                    String message =
                            "Sorry you didn’t get selected for this round of lottery for the \"" +
                                    eventTitle + "\" event. You are still in the waiting list and may be selected in future draws.";

                    DocumentReference senderRef = organizerRef.document(organizerId);

                    sendNotificationsToEntrantRefs(
                            waitingList,
                            eventId,
                            eventTitle,
                            "Lottery update for \"" + eventTitle + "\"",
                            message,
                            senderRef,
                            context
                    );
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading event for waiting list notifications", e);
                    Toast.makeText(context,
                            "Failed to load event for waiting list notifications.",
                            Toast.LENGTH_SHORT).show();
                });
    }


    public static void sendInvitedNotifications(Context context,
                                                String eventId,
                                                String organizerId) {
        Log.d(TAG, "sendInvitedNotifications called for eventId=" + eventId);

        eventsRef.document(eventId).get()
                .addOnSuccessListener(eventDoc -> {
                    if (!eventDoc.exists()) {
                        Log.d(TAG, "Event not found for invited notifications");
                        Toast.makeText(context, "Event not found.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String eventTitle = eventDoc.getString("title");
                    if (eventTitle == null || eventTitle.trim().isEmpty()) {
                        eventTitle = "this event";
                    }

                    @SuppressWarnings("unchecked")
                    List<DocumentReference> invitedList =
                            (List<DocumentReference>) eventDoc.get("invitedList");

                    if (invitedList == null || invitedList.isEmpty()) {
                        Log.d(TAG, "No entrants in invitedList to notify.");
                        return;
                    }

                    String message =
                            "Congratulations! You were selected in the lottery for the \"" +
                                    eventTitle +
                                    "\" event. You can accept or reject your invitation in event details.";

                    DocumentReference senderRef = organizerRef.document(organizerId);

                    sendNotificationsToEntrantRefs(
                            invitedList,
                            eventId,
                            eventTitle,
                            "You were selected for \"" + eventTitle + "\"",
                            message,
                            senderRef,
                            context
                    );
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading event for invited notifications", e);
                    Toast.makeText(context,
                            "Failed to load event for invited notifications.",
                            Toast.LENGTH_SHORT).show();
                });
    }

    public static void sendSingleCancelledNotification(
            Context context,
            String eventId,
            String organizerId,
            DocumentReference entrantRef
    ) {

        eventsRef.document(eventId).get()
                .addOnSuccessListener(eventDoc -> {
                    if (!eventDoc.exists()) return;

                    String eventTitle = eventDoc.getString("title");
                    if (eventTitle == null) eventTitle = "this event";

                    String message = "Your invitation for the \"" + eventTitle +
                            "\" event was canceled by the organizer.";

                    String title = "Invitation canceled for \"" + eventTitle + "\"";

                    DocumentReference senderRef = organizerId == null ?
                            null : organizerRef.document(organizerId);

                    Map<String, Object> data = new HashMap<>();
                    data.put("createdAt", FieldValue.serverTimestamp());
                    data.put("eventId", eventsRef.document(eventId));
                    data.put("eventName", eventTitle);
                    data.put("notificationMessage", message);
                    data.put("notificationTitle", title);
                    data.put("read", false);
                    data.put("notified", false);
                    data.put("receiverId", entrantRef);
                    data.put("senderID", senderRef);

                    notifRef.add(data);
                });
    }

    private static void sendNotificationsToEntrantRefs(List<DocumentReference> entrantRefs,
                                                       String eventIdString,
                                                       String eventTitle,
                                                       String notificationTitle,
                                                       String notificationMessage,
                                                       DocumentReference senderRef,
                                                       Context context) {

        if (entrantRefs == null || entrantRefs.isEmpty()) {
            Log.d(TAG, "sendNotificationsToEntrantRefs: empty entrant list, nothing to do.");
            return;
        }

        DocumentReference eventRef = eventsRef.document(eventIdString);

        Log.d(TAG, "Sending notifications to " + entrantRefs.size() + " entrants.");

        for (DocumentReference entrantRef : entrantRefs) {

            if (entrantRef == null) {
                Log.d(TAG, "Null entrantRef encountered, skipping.");
                continue;
            }

            entrantRef.get()
                    .addOnSuccessListener(entrantDoc -> {
                        if (!entrantDoc.exists()) {
                            Log.d(TAG, "Entrant doc does not exist: " + entrantRef.getPath());
                            return;
                        }
                        Boolean allow = entrantDoc.getBoolean("allowNotification");
                        if (allow != null && !allow) {
                            Log.d(TAG, "Notifications disabled for " + entrantRef.getPath());
                            return;
                        }
                        Map<String, Object> notificationData = new HashMap<>();
                        notificationData.put("createdAt", FieldValue.serverTimestamp());
                        notificationData.put("eventId", eventRef);
                        notificationData.put("eventName", eventTitle);
                        notificationData.put("notificationTitle", notificationTitle);
                        notificationData.put("notificationMessage", notificationMessage);
                        notificationData.put("read", false);
                        notificationData.put("notified", false);
                        notificationData.put("receiverId", entrantRef);

                        if (senderRef != null) {
                            notificationData.put("senderID", senderRef);
                        }


                        Log.d(TAG, "Adding notification for entrant: " + entrantRef.getPath());

                        notifRef.add(notificationData)
                                .addOnSuccessListener(docRef ->
                                        Log.d(TAG, "Notification added with id: " + docRef.getId()))
                                .addOnFailureListener(e ->
                                        Log.e(TAG, "Failed to add notification", e));
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to fetch entrant doc: " + entrantRef.getPath(), e);
                        Toast.makeText(context, "Failed to send notification.", Toast.LENGTH_SHORT).show();
                    });
        }
    }
}
