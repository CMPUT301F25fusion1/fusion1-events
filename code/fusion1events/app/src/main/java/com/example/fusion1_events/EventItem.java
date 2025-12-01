package com.example.fusion1_events;


/**
 * Represents a lightweight event item used for display in lists or previews.
 * Stores basic event information such as image URL, organizer name,
 * and event date. Designed to be Firestore-friendly with a no-argument constructor.
 */
public class EventItem {
    private String imageUrl;
    private String organizerName;
    private String date; // use "date" so getDate()/setDate() exist

    /**
     * Default constructor required for Firestore automatic data mapping.
     */
    public EventItem() {} // Needed for Firestore mapping

    /**
     * Creates a new EventItem with the provided details.
     *
     * @param imageUrl      URL of the event's image.
     * @param organizerName Name of the event organizer.
     * @param date          Date of the event as a formatted string.
     */
    public EventItem(String imageUrl, String organizerName, String date) {
        this.imageUrl = imageUrl;
        this.organizerName = organizerName;
        this.date = date;
    }

    /**
     * Returns the Cloudinary or web URL of the event image.
     *
     * @return the image URL.
     */
    // Getters
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * Returns the name of the organizer associated with this event.
     *
     * @return the organizer's name.
     */
    public String getOrganizerName() {
        return organizerName;
    }

    /**
     * Returns the event date as a formatted string.
     *
     * @return the event date.
     */
    public String getDate() {
        return date;
    }

    /**
     * Sets the image URL for the event.
     *
     * @param imageUrl the new image URL.
     */
    // Setters
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * Sets the organizer's name for this event.
     *
     * @param organizerName the new organizer name.
     */
    public void setOrganizerName(String organizerName) {
        this.organizerName = organizerName;
    }

    /**
     * Sets the date of the event as a formatted string.
     *
     * @param date the new event date.
     */
    public void setDate(String date) {
        this.date = date;
    }
}
