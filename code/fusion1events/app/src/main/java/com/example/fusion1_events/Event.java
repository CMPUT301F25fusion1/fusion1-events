package com.example.fusion1_events;

import java.io.Serializable;
import java.util.ArrayList;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

/**
 * File: Event.java
 *
 * Role:
 * - Serves as the data model for an event in the application.
 * - Stores key event information such as title, description, dates, signup counts,
 *   attendee counts, and the waiting list.
 * - Used for serializing/deserializing Firestore documents into Java objects.
 *
 * Issues:
 * - Contains minimal validation for fields (e.g., negative signups or attendees).
 * - Assumes Firestore timestamps and document references are always present and valid.
 *
 */
public class Event implements Serializable {
    private String id;
    private String title;
    private String description;
    private String imageUrl;
    private Timestamp registration_start;
    private Timestamp registration_end;
    private Timestamp date;
    private int attendees;
    private int Signups;
    private ArrayList<DocumentReference> waitingList;
    // new
    private ArrayList<DocumentReference> invitedList;
    private ArrayList<DocumentReference> confirmed;
    private ArrayList<DocumentReference> cancelled;
    private ArrayList<String> keywords;


    public Event(){}


    public Event(int signups, int attendees, Timestamp date,String id, String description,
                 Timestamp registration_start, Timestamp registration_end, String title, ArrayList<DocumentReference> waitingList,
                 String imageUrl, ArrayList<DocumentReference> invitedList, ArrayList<DocumentReference> confirmed,
                 ArrayList<DocumentReference> cancelled, ArrayList<String> keywords) {
        this.Signups = signups;
        this.attendees = attendees;
        this.date = date;
        this.id = id;
        this.description = description;
        this.imageUrl = imageUrl;
        this.registration_start = registration_start;
        this.registration_end = registration_end;
        this.title = title;
        this.date = date;
        this.attendees = attendees;
        this.Signups = signups;
        this.waitingList = waitingList;
        this.imageUrl = imageUrl;
        // new
        this.invitedList = invitedList;
        this.confirmed = confirmed;
        this.cancelled = cancelled;
        this.keywords = keywords;
    }



    //getters and setters
    /**
     * Gets the unique ID of the event.
     *
     * @return the ID of the event
     */
    public String getId() { return id; }
    /**
     * Sets the unique ID of the event.
     *
     * @param id the new ID of the event
     */
    public void setId(String id) { this.id = id; }

    /**
     * Returns the number of users currently signed up.
     *
     * @return number of signups
     */
    public int getSignups() { return Signups; }
    /**
     * Sets the number of users currently signed up.
     *
     * @param signups number of signups
     */
    public void setSignups(int signups) { this.Signups = signups; }
    /**
     * Returns the number of attendees for the event.
     *
     * @return number of attendees
     */
    public int getAttendees() { return attendees; }

    /**
     * Sets the number of attendees for the event.
     *
     * @param attendees number of attendees
     */
    public void setAttendees(int attendees) { this.attendees = attendees; }
    /**
     * Returns the event date.
     *
     * @return event date as a Timestamp
     */
    public Timestamp getDate() { return date; }
    /**
     * Sets the event date.
     *
     * @param date the event date
     */
    public void setDate(Timestamp date) { this.date = date; }
    /**
     * Returns the event description.
     *
     * @return description of the event
     */
    public String getDescription() { return description; }
    /**
     * Sets the event description.
     *
     * @param description description of the event
     */
    public void setDescription(String description) { this.description = description; }
    /**
     * Returns the registration end time.
     *
     * @return registration end as a Timestamp
     */
    public Timestamp getRegistration_end() { return registration_end; }
    /**
     * Sets the registration end time.
     *
     * @param registration_end registration end as a Timestamp
     */
    public void setRegistration_end(Timestamp registration_end) { this.registration_end = registration_end; }
    /**
     * Returns the registration start time.
     *
     * @return registration start as a Timestamp
     */
    public Timestamp getRegistration_start() { return registration_start; }
    /**
     * Sets the registration start time.
     *
     * @param registration_start registration start as a Timestamp
     */
    public void setRegistration_start(Timestamp registration_start) { this.registration_start = registration_start; }
    /**
     * Returns the title of the event.
     *
     * @return event title
     */
    public String getTitle() { return title; }
    /**
     * Sets the event title.
     *
     * @param title event title
     */
    public void setTitle(String title) { this.title = title; }
    /**
     * Returns the waiting list for the event.
     *
     * @return list of document references representing the waiting list
     */
    public ArrayList<DocumentReference> getWaitingList() {
        return waitingList;
    }
    /**
     * Sets the waiting list for the event.
     *
     * @param waitingList list of document references representing the waiting list
     */
    public void setWaitingList(ArrayList<DocumentReference> waitingList) {
        this.waitingList = waitingList;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl){
        this.imageUrl = imageUrl;
    }

    // new code
    public ArrayList<DocumentReference> getFinaList() { return invitedList; }

    public void setFinaList(ArrayList<DocumentReference> finaList) {
        this.invitedList = finaList;
    }

    public ArrayList<DocumentReference> getConfirmed() { return confirmed; }

    public void setConfirmed(ArrayList<DocumentReference> confirmed) {
        this.confirmed = confirmed;
    }

    public ArrayList<DocumentReference> getCancelled() { return cancelled; }
    public void setCancelled(ArrayList<DocumentReference> cancelled) {
        this.cancelled = cancelled;
    }

    public ArrayList<String> getKeyword() { return keywords; }
    public void setKeyword(String keyword) {
        this.keywords = keywords;
    }
}
