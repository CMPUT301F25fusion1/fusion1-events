package com.example.fusion1_events;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

public class NotificationModel {

    private String id;
    private String eventName;
    private String notificationTitle;
    private String notificationMessage;
    private boolean read;
    private Timestamp createdAt;
    private DocumentReference eventId;
    private DocumentReference receiverId;
    private DocumentReference senderID;

    private boolean notified;

    public NotificationModel() {
    }

    public NotificationModel(String id,
                            String eventName,
                            String notificationTitle,
                            String notificationMessage,
                            boolean read,
                            Timestamp createdAt,
                            DocumentReference eventId,
                            DocumentReference receiverId,
                            DocumentReference senderID,
                            boolean notified) {
        this.id = id;
        this.eventName = eventName;
        this.notificationTitle = notificationTitle;
        this.notificationMessage = notificationMessage;
        this.read = read;
        this.createdAt = createdAt;
        this.eventId = eventId;
        this.receiverId = receiverId;
        this.senderID = senderID;
        this.notified = notified;
    }

    public static NotificationModel fromSnapshot(DocumentSnapshot doc) {
        String id = doc.getId();
        String eventName = doc.getString("eventName");
        String notificationTitle = doc.getString("notificationTitle");
        String notificationMessage = doc.getString("notificationMessage");
        Boolean read = doc.getBoolean("read");
        Timestamp createdAt = doc.getTimestamp("createdAt");
        DocumentReference eventId = doc.getDocumentReference("eventId");
        DocumentReference receiverId = doc.getDocumentReference("receiverId");
        DocumentReference senderID = doc.getDocumentReference("senderID");

        Boolean notified = doc.getBoolean("notified");
        boolean isNotified = notified != null && notified;


        if (eventName == null) eventName = "";
        if (notificationTitle == null) notificationTitle = "";
        if (notificationMessage == null) notificationMessage = "";
        boolean isRead = read != null && read;

        return new NotificationModel(
                id,
                eventName,
                notificationTitle,
                notificationMessage,
                isRead,
                createdAt,
                eventId,
                receiverId,
                senderID,
                isNotified

        );
    }

    public String getNotificationId() {
        return id;
    }

    public String getEventName() {
        return eventName;
    }

    public String getNotificationTitle() {
        return notificationTitle;
    }

    public String getNotificationMessage() {
        return notificationMessage;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public DocumentReference getNotificationEventId() {
        return eventId;
    }

    public DocumentReference getReceiverId() {
        return receiverId;
    }

    public DocumentReference getSenderID() {
        return senderID;
    }

    public boolean isNotified() { return notified; }
    public void setNotified(boolean notified) { this.notified = notified; }
}
