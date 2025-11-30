package com.example.fusion1_events.admin;

import com.google.firebase.Timestamp;

/**
 * Model class representing an image uploaded for an event in the admin interface.
 */
public class AdminImage {
    private String eventId;
    private String imageUrl;
    private String eventTitle;
    private Timestamp registration_end;

    public AdminImage() { }

    public AdminImage(String eventId, String imageUrl, String eventTitle) {
        this.eventId = eventId;
        this.imageUrl = imageUrl;
        this.eventTitle = eventTitle;
    }

    // getters and setters
    /**
     * Gets the ID of the image.
     * @return the image ID
     */
    public String getEventId() {
        return eventId;
    }

    /**
     * Sets the ID of the image.
     * @param eventId the image ID
     */
    public void setEventId(String eventId) { this.eventId = eventId;}

    /**
     * Gets the URL of the image.
     * @return the image URL
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * Sets the URL of the image.
     * @param imageUrl the image URL
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * Gets the title of the associated event.
     * @return the event title
     */
    public String getEventTitle() {
        return eventTitle;
    }

    /**
     * Sets the title of the associated event.
     * @param eventTitle the event title
     */
    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    /**
     * Gets the registration end timestamp for the event.
     * @return the registration end timestamp
     */
    public Timestamp getRegistration_end() {
        return registration_end;
    }

    /**
     * Sets the registration end timestamp for the event.
     * @param registration_end the registration end timestamp
     */
    public void setRegistration_end(Timestamp registration_end) {
        this.registration_end = registration_end;
    }
}
