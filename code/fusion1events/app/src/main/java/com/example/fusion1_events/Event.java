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

    public Event(){}

    public Event(String id, String title, String description, String imageUrl,
                 Timestamp registration_start, Timestamp registration_end, Timestamp date,
                 int attendees, int signups, ArrayList<DocumentReference> waitingList) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.registration_start = registration_start;
        this.registration_end = registration_end;
        this.date = date;
        this.attendees = attendees;
        this.Signups = signups;
        this.waitingList = waitingList;
    }

    //getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Timestamp getRegistration_start() { return registration_start; }
    public void setRegistration_start(Timestamp registration_start) { this.registration_start = registration_start; }

    public Timestamp getRegistration_end() { return registration_end; }
    public void setRegistration_end(Timestamp registration_end) { this.registration_end = registration_end; }

    public Timestamp getDate() { return date; }
    public void setDate(Timestamp date) { this.date = date; }

    public int getAttendees() { return attendees; }
    public void setAttendees(int attendees) { this.attendees = attendees; }

    public int getSignups() { return Signups; }
    public void setSignups(int signups) { this.Signups = signups; }

    public ArrayList<DocumentReference> getWaitingList() { return waitingList; }
    public void setWaitingList(ArrayList<DocumentReference> waitingList) { this.waitingList = waitingList; }
}
