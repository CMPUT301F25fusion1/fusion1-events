package com.example.fusion1_events;

public class EventItem {
    private String imageUrl;
    private String organizerName;
    private String date; // use "date" so getDate()/setDate() exist

    public EventItem() {} // Needed for Firestore mapping

    public EventItem(String imageUrl, String organizerName, String date) {
        this.imageUrl = imageUrl;
        this.organizerName = organizerName;
        this.date = date;
    }

    // Getters
    public String getImageUrl() {
        return imageUrl;
    }

    public String getOrganizerName() {
        return organizerName;
    }

    public String getDate() {
        return date;
    }

    // Setters
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setOrganizerName(String organizerName) {
        this.organizerName = organizerName;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
