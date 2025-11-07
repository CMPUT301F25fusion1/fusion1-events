package com.example.fusion1_events;

import java.util.ArrayList;
import java.util.Date;

public class EventsModel {
    String eventTitle;
    Date registrationStart;
    Date registrationEnd;
    String eventDescription;
    Date date;
    Long signups;
    Long attendees;
    ArrayList<String> waitingList; // ADDED: Store entrant IDs from waiting list
    String imageUrl; // Store Cloudinary URL for event image
    String eventId; // Store Firestore document ID for updates/deletes

    // MODIFIED: Constructor now includes waitingList parameter
    public EventsModel(String eventTitle, Date registrationStart, Date registrationEnd,
                       String eventDescription, Date date, Long attendees, Long signups,
                       ArrayList<String> waitingList, String imageUrl, String eventId) {
        this.eventTitle = eventTitle;
        this.registrationStart = registrationStart;
        this.registrationEnd = registrationEnd;
        this.eventDescription = eventDescription;
        this.date = date;
        this.attendees = attendees;
        this.signups = signups;
        this.waitingList = waitingList != null ? waitingList : new ArrayList<>(); // ADDED: Initialize waitingList with empty list if null
        this.imageUrl = imageUrl;
        this.eventId = eventId;
    }

    // ADDED: Getter for waitingList
    public ArrayList<String> getWaitingList() {
        return waitingList;
    }

    // ADDED: Setter for waitingList
    public void setWaitingList(ArrayList<String> waitingList) {
        this.waitingList = waitingList;
    }

    // ADDED: Add entrant to waiting list
    public void addToWaitingList(String entrantId) {
        if (!waitingList.contains(entrantId)) {
            waitingList.add(entrantId);
        }
    }

    // ADDED: Remove entrant from waiting list
    public void removeFromWaitingList(String entrantId) {
        waitingList.remove(entrantId);
    }

    // ADDED: Check if entrant is in waiting list
    public boolean isInWaitingList(String entrantId) {
        return waitingList.contains(entrantId);
    }

    // ADDED: Get waiting list size
    public int getWaitingListSize() {
        return waitingList.size();
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public Date getRegistrationStart() {
        return registrationStart;
    }

    public void setRegistrationStart(Date registrationStart) {
        this.registrationStart = registrationStart;
    }

    public Date getRegistrationEnd() {
        return registrationEnd;
    }

    public void setRegistrationEnd(Date registrationEnd) {
        this.registrationEnd = registrationEnd;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Long getAttendees() {
        return attendees;
    }

    public void setAttendees(Long attendees) {
        this.attendees = attendees;
    }

    public Long getSignups() {
        return signups;
    }

    public void setSignups(Long signups) {
        this.signups = signups;
    }
}