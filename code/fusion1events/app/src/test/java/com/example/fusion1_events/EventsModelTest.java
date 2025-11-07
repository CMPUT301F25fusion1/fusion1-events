package com.example.fusion1_events;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;

public class EventsModelTest {

    private EventsModel event;

    @Before
    public void setup() {
        Date now = new Date();
        event = new EventsModel("Concert", now, now, "Music Event", now,
                100L, 80L, new ArrayList<>(), null, "event123", new ArrayList<>());
    }

    @Test
    public void testAddToWaitingList() {
        event.addToWaitingList("user1");
        assertTrue(event.isInWaitingList("user1"));
    }

    @Test
    public void testRemoveFromWaitingList() {
        event.addToWaitingList("user2");
        event.removeFromWaitingList("user2");
        assertFalse(event.isInWaitingList("user2"));
    }

    @Test
    public void testNoDuplicateEntries() {
        event.addToWaitingList("user3");
        event.addToWaitingList("user3");
        assertEquals(1, event.getWaitingListSize());
    }
}
