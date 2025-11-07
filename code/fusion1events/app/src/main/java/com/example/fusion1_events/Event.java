package com.example.fusion1_events;

import java.io.Serializable;
import java.util.ArrayList;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
/**
 * Represents an event with details such as signups, attendees, date, description,
 * registration period, title, and a waiting list of users.
 */
public class Event implements Serializable {
    private int Signups;
    private int attendees;
    private Timestamp date;
    private String description;
    private Timestamp registration_end;
    private Timestamp registration_start;
    private String title;
    private ArrayList<DocumentReference> waitingList;

    public Event(){}

    public Event(int signups, int attendees, Timestamp date, String description,
                 Timestamp registration_start, Timestamp registration_end, String title, ArrayList<DocumentReference> waitingList) {
        this.Signups = signups;
        this.attendees = attendees;
        this.date = date;
        this.description = description;
        this.registration_start = registration_start;
        this.registration_end = registration_end;
        this.title = title;
        this.waitingList = waitingList;
    }

    //getters and setters
    public int getSignups() { return Signups; }
    public void setSignups(int signups) { this.Signups = signups; }

    public int getAttendees() { return attendees; }
    public void setAttendees(int attendees) { this.attendees = attendees; }

    public Timestamp getDate() { return date; }
    public void setDate(Timestamp date) { this.date = date; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Timestamp getRegistration_end() { return registration_end; }
    public void setRegistration_end(Timestamp registration_end) { this.registration_end = registration_end; }

    public Timestamp getRegistration_start() { return registration_start; }
    public void setRegistration_start(Timestamp registration_start) { this.registration_start = registration_start; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public ArrayList<DocumentReference> getWaitingList() {
        return waitingList;
    }
    public void setWaitingList(ArrayList<DocumentReference> waitingList) {
        this.waitingList = waitingList;
    }
}
