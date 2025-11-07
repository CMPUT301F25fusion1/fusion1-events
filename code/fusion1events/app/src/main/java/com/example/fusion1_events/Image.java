package com.example.fusion1_events;

import com.google.firebase.Timestamp;

public class Image {
    private String id;
    private String imageUrl;
    private String eventTitle;
    private String organizer;
    private Timestamp registration_end;

    public Image() {}

    // getters and setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getOrganizer() {
        return organizer;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    public Timestamp getRegistration_end() {
        return registration_end;
    }

    public void setRegistration_end(Timestamp registration_end) {
        this.registration_end = registration_end;
    }
}
