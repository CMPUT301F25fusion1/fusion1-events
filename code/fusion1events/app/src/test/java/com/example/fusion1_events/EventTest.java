package com.example.fusion1_events;

import com.google.firebase.Timestamp;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Date;

public class EventTest {
    @Test
    public void testGettersAndSetters() {
        Event event = new Event();
        event.setId("e1");
        event.setTitle("Test Event");
        event.setDescription("This is the description for the test event.");
        Timestamp timestamp = new Timestamp(new Date());
        event.setRegistration_end(timestamp);

        assertEquals("e1", event.getId());
        assertEquals("Test Event", event.getTitle());
        assertEquals("This is the description for the test event.", event.getDescription());
        assertEquals(timestamp, event.getRegistration_end());
    }

    @Test
    public void testEventDefaults() {
        Event event = new Event();

        assertNull(event.getId());
        assertNull(event.getTitle());
        assertNull(event.getDescription());
        assertNull(event.getRegistration_end());
    }
}
