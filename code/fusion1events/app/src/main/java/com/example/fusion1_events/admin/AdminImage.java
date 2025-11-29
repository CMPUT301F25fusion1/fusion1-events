package com.example.fusion1_events.admin;

import com.google.firebase.Timestamp;

/**
 * Model class representing an image uploaded for an event in the admin interface.
 */
public class AdminImage {
    private String id;
    private String imageUrl;
    private String eventTitle;
    private String organizer;
    private Timestamp registration_end;

    public AdminImage() {}

    // getters and setters
    /**
     * Gets the ID of the image.
     * @return the image ID
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the ID of the image.
     * @param id the image ID
     */
    public void setId(String id) {
        this.id = id;
    }

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
     * Gets the organizer of the image.
     * @return the organizer
     */
    public String getOrganizer() {
        return organizer;
    }

    /**
     * Sets the organizer of the image.
     * @param organizer the organizer
     */
    public void setOrganizer(String organizer) {
        this.organizer = organizer;
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
