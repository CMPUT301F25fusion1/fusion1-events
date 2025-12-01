package com.example.fusion1_events.admin.notification;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

/**
 * Represents a notification in the application.
 * <p>
 * Contains information such as createdAt, eventId, eventName, notificationMessage,
 * notificationTitle, notified, read, receiverId, senderId.
 */
public class AdminNotification {
    private String id;
    private Timestamp createdAt;
    private DocumentReference eventId;
    private String eventName;
    private String notificationMessage;
    private String notificationTitle;
    private boolean notified;
    private boolean read;
    private DocumentReference receiverId;
    private DocumentReference senderID;

    // default constructor
    public AdminNotification() { }

    // getters and setters
    /**
     * Gets the ID of the notification.
     * @return the notification ID
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the ID of the notification.
     * @param id the notification ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Returns the timestamp when the notification was created.
     *
     * @return the creation timestamp
     */
    public Timestamp getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the timestamp when the notification was created.
     *
     * @param createdAt Firestore timestamp
     */
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Returns the ID of the event associated with this notification.
     *
     * @return event ID as a string
     */
    public DocumentReference getEventId() {
        return eventId;
    }

    /**
     * Sets the ID of the event associated with this notification.
     *
     * @param eventId Firestore document ID of the event
     */
    public void setEventId(DocumentReference eventId) {
        this.eventId = eventId;
    }

    /**
     * Returns the name of the event for which this notification was sent.
     *
     * @return event name
     */
    public String getEventName() {
        return eventName;
    }

    /**
     * Sets the name of the event associated with this notification.
     *
     * @param eventName name of the event
     */
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    /**
     * Returns the body text of the notification.
     *
     * @return message content
     */
    public String getNotificationMessage() {
        return notificationMessage;
    }

    /**
     * Sets the message body of the notification.
     *
     * @param notificationMessage message content
     */
    public void setNotificationMessage(String notificationMessage) {
        this.notificationMessage = notificationMessage;
    }

    /**
     * Returns the title of the notification.
     *
     * @return notification title
     */
    public String getNotificationTitle() {
        return notificationTitle;
    }

    /**
     * Sets the title of the notification.
     *
     * @param notificationTitle short descriptive title
     */
    public void setNotificationTitle(String notificationTitle) {
        this.notificationTitle = notificationTitle;
    }

    /**
     * Returns whether the notification has been successfully sent to the receiver.
     *
     * @return true if delivered, false otherwise
     */
    public boolean isNotified() {
        return notified;
    }

    /**
     * Sets the delivery status of the notification.
     *
     * @param notified true if delivered, false otherwise
     */
    public void setNotified(boolean notified) {
        this.notified = notified;
    }

    /**
     * Returns whether the receiver has opened or read the notification.
     *
     * @return true if read, false otherwise
     */
    public boolean isRead() {
        return read;
    }

    /**
     * Sets whether the receiver has read the notification.
     *
     * @param read true if read, false otherwise
     */
    public void setRead(boolean read) {
        this.read = read;
    }

    /**
     * Returns the user ID of the receiver.
     *
     * @return receiver ID as a string
     */
    public DocumentReference getReceiverId() {
        return receiverId;
    }

    /**
     * Sets the user ID of the receiver.
     *
     * @param receiverId Firestore user document ID
     */
    public void setReceiverId(DocumentReference receiverId) {
        this.receiverId = receiverId;
    }

    /**
     * Returns the user ID of the sender (organizer).
     *
     * @return sender ID as a string
     */
    public DocumentReference getSenderID() {
        return senderID;
    }

    /**
     * Sets the user ID of the sender (organizer).
     *
     * @param senderID Firestore user document ID
     */
    public void setSenderID(DocumentReference senderID) {
        this.senderID = senderID;
    }
}
