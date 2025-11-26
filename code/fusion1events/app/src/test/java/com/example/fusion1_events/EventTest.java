package com.example.fusion1_events;

import static org.junit.Assert.assertEquals;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;

public class EventTest {

    private Event event;
    private Timestamp now;
    private ArrayList<DocumentReference> waitingList;

    @Before
    public void setUp() {
        now = new Timestamp(new Date());
        waitingList = new ArrayList<>();
        event = new Event(100, 50, now, "event1", "A test event", now, now, "Test Event", waitingList, "http://example.com/image.png");
    }

    @Test
    public void testGetters() {
        assertEquals("event1", event.getId());
        assertEquals("Test Event", event.getTitle());
        assertEquals("A test event", event.getDescription());
        assertEquals("http://example.com/image.png", event.getImageUrl());
        assertEquals(now, event.getRegistration_start());
        assertEquals(now, event.getRegistration_end());
        assertEquals(now, event.getDate());
        assertEquals(50, event.getAttendees());
        assertEquals(100, event.getSignups());
        assertEquals(waitingList, event.getWaitingList());
    }

    @Test
    public void testSetters() {
        Timestamp newTime = new Timestamp(new Date(System.currentTimeMillis() + 10000));
        ArrayList<DocumentReference> newWaitingList = new ArrayList<>();

        event.setId("event2");
        event.setTitle("New Test Event");
        event.setDescription("An updated test event");
        event.setImageUrl("http://example.com/new_image.png");
        event.setRegistration_start(newTime);
        event.setRegistration_end(newTime);
        event.setDate(newTime);
        event.setAttendees(75);
        event.setSignups(125);
        event.setWaitingList(newWaitingList);

        assertEquals("event2", event.getId());
        assertEquals("New Test Event", event.getTitle());
        assertEquals("An updated test event", event.getDescription());
        assertEquals("http://example.com/new_image.png", event.getImageUrl());
        assertEquals(newTime, event.getRegistration_start());
        assertEquals(newTime, event.getRegistration_end());
        assertEquals(newTime, event.getDate());
        assertEquals(75, event.getAttendees());
        assertEquals(125, event.getSignups());
        assertEquals(newWaitingList, event.getWaitingList());
    }

    @Test
    public void testEmptyConstructor() {
        Event emptyEvent = new Event();
        assertEquals(null, emptyEvent.getId());
        assertEquals(null, emptyEvent.getTitle());
        assertEquals(null, emptyEvent.getDescription());
        assertEquals(null, emptyEvent.getImageUrl());
        assertEquals(null, emptyEvent.getRegistration_start());
        assertEquals(null, emptyEvent.getRegistration_end());
        assertEquals(null, emptyEvent.getDate());
        assertEquals(0, emptyEvent.getAttendees());
        assertEquals(0, emptyEvent.getSignups());
        assertEquals(null, emptyEvent.getWaitingList());
    }
}
