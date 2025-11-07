package com.example.fusion1_events;

import java.util.ArrayList;
import java.util.Date;

public class EventsModel {
    String eventTitle;
    Date registrationStart;
    Date registrationEnd;
    String eventDescription;
    Date date;
    Long attendees;
    Long signups;
    ArrayList<String> waitingList;
    ArrayList<String> finalList;
    String imageUrl; // Store Cloudinary URL for event image
    String eventId; // Store Firestore document ID for updates/deletes

    public ArrayList<String> getFinalList() {
        return finalList;
    }

    public void setFinalList(ArrayList<String> finalList) {
        this.finalList = finalList;
    }

    public EventsModel(String eventTitle, Date registrationStart, Date registrationEnd,
                       String eventDescription, Date date, Long attendees, Long signups,
                       ArrayList<String> waitingList, String imageUrl, String eventId, ArrayList<String> finalList) {
        this.eventTitle = eventTitle;
        this.registrationStart = registrationStart;
        this.registrationEnd = registrationEnd;
        this.eventDescription = eventDescription;
        this.date = date;
        this.attendees = attendees;
        this.signups = signups;
        this.waitingList = waitingList != null ? waitingList : new ArrayList<>(); 
        this.imageUrl = imageUrl;
        this.eventId = eventId;
        this.finalList = finalList != null ? finalList : new ArrayList<>(); // ADDED: Initialize finalList with empty list if null
    }

    public ArrayList<String> getWaitingList() {
        return waitingList;
    }

    public void setWaitingList(ArrayList<String> waitingList) {
        this.waitingList = waitingList;
    }

    public void addToWaitingList(String entrantId) {
        if (!waitingList.contains(entrantId)) {
            waitingList.add(entrantId);
        }
    }

    public void removeFromWaitingList(String entrantId) {
        waitingList.remove(entrantId);
    }

    public boolean isInWaitingList(String entrantId) {
        return waitingList.contains(entrantId);
    }

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
