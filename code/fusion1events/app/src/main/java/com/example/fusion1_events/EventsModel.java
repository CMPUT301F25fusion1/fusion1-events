package com.example.fusion1_events;

import java.util.Date;

public class EventsModel {
    String eventTitle;
    Date registrationStart;
    Date registrationEnd;
    String eventDescription;
    Date date;
    Long signups;

    // Constructor without poster field
    public EventsModel(String eventTitle, Date registrationStart, Date registrationEnd,
                       String eventDescription, Date date, Long attendees, Long signups) {
        this.eventTitle = eventTitle;
        this.registrationStart = registrationStart;
        this.registrationEnd = registrationEnd;
        this.eventDescription = eventDescription;
        this.date = date;
        this.attendees = attendees;
        this.signups = signups;
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