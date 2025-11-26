package com.example.fusion1_events;

import static org.junit.Assert.assertEquals;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class EntrantTest {
    private Event event;
    private ArrayList<DocumentReference> waitingList;
    private EventAdapter adapter;

    @Before
    public void setUpEvent() {
        waitingList = new ArrayList<>();
        Timestamp now = Timestamp.now();
        event = new Event(5, 3, now, "id", "Test event", now, now, "Title", waitingList, null, null, null, null, null);
    }

    @Test
    public void testGetters() {
        assertEquals(5, event.getSignups());
        assertEquals(3, event.getAttendees());
        assertEquals("Test event", event.getDescription());
        assertEquals("Title", event.getTitle());
        assertEquals(waitingList, event.getWaitingList());
    }

    @Test
    public void testSetters() {
        event.setSignups(10);
        event.setAttendees(8);
        event.setDescription("Updated");
        event.setTitle("New Title");
        ArrayList<DocumentReference> newList = new ArrayList<>();
        event.setWaitingList(newList);

        assertEquals(10, event.getSignups());
        assertEquals(8, event.getAttendees());
        assertEquals("Updated", event.getDescription());
        assertEquals("New Title", event.getTitle());
        assertEquals(newList, event.getWaitingList());
    }

    @Before
    public void setUpAdapter() {
        Profile profile = new Profile("Tom","tom@gmail.com","123456789","ENTRANT","123");
        List<Event> events = new ArrayList<>();
        Event e = new Event();
        e.setWaitingList(new ArrayList<>());
        events.add(e);

        // Use null for context; can't inflate layouts without Android
        adapter = new EventAdapter(null, events, profile);
    }

    @Test
    public void testItemCount() {
        assertEquals(1, adapter.getItemCount());
    }


}
