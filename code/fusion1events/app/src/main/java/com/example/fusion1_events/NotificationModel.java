package com.example.fusion1_events;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

/**
 * File: NotificationModel.java
 * Role:<br>
 * - Represents a notification sent to a user. <br>
 * - Stores information about the event, title, message, timestamp, read status,
 *   senderId, receiverId.<br>
 * <br>
 * Issues:<br>
 * - Assumes Firestore fields exist and are formatted as expected.<br>
 */
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

    /**
     * Constructs a NotificationModel with all fields provided.
     *
     * @param id the ID of the notification document
     * @param eventName the name of the event
     * @param notificationTitle the title of the notification
     * @param notificationMessage the message of the notification
     * @param read boolean to see whether the notification has been marked as read
     * @param createdAt the timestamp when the notification was created
     * @param eventId Firestore reference to the event document
     * @param receiverId Firestore reference to the receiving user
     * @param senderID Firestore reference to the sending user
     * @param notified boolean to check if the entrant has been notified
     */
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

    /**
     * Creates a NotificationModel instance from a Firestore document.<br>
     *
     * @param doc Firestore document snapshot
     * @return a populated NotificationModel object
     */
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

    /**
     * Returns the ID of the notification document.
     *
     * @return notification ID string
     */
    public String getNotificationId() {
        return id;
    }

    /**
     * Returns the name of the related event.
     *
     * @return event name string
     */
    public String getEventName() {
        return eventName;
    }

    /**
     * Returns the notification title.
     *
     * @return title string
     */
    public String getNotificationTitle() {
        return notificationTitle;
    }

    /**
     * Returns the notification message.
     *
     * @return message string
     */
    public String getNotificationMessage() {
        return notificationMessage;
    }

    /**
     * Returns whether the notification has been read.
     * @return boolean value
     */
    public boolean isRead() {
        return read;
    }

    /**
     * Sets the read status of the notification.
     *
     * @param read boolean value
     */
    public void setRead(boolean read) {
        this.read = read;
    }

    /**
     * Returns the timestamp when the notification was created.
     *
     * @return createAt timestamp
     */
    public Timestamp getCreatedAt() {
        return createdAt;
    }


    /**
     * Returns the Firestore reference of the event.
     *
     * @return event reference
     */
    public DocumentReference getNotificationEventId() {
        return eventId;
    }

    /**
     * Returns the Firestore reference of the notification receiver.
     *
     * @return receiver reference
     */
    public DocumentReference getReceiverId() {
        return receiverId;
    }

    /**
     * Returns the Firestore reference of the notification sender.
     *
     * @return sender reference
     */
    public DocumentReference getSenderID() {
        return senderID;
    }

    /**
     * Returns whether the notification was delivered to the user.
     *
     * @return delivery status boolean
     */
    public boolean isNotified() { return notified; }

    /**
     * Sets the delivery status for the notification.
     *
     * @param notified boolean value
     */
    public void setNotified(boolean notified) { this.notified = notified; }
}
