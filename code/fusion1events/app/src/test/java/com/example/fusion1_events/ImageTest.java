package com.example.fusion1_events;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.google.firebase.Timestamp;

import org.junit.Test;

public class ImageTest {
    @Test
    public void testImageGettersAndSetters() {
        Image image = new Image();
        Timestamp timestamp = Timestamp.now();

        image.setId("img001");
        image.setImageUrl("https://example.com/image.png");
        image.setEventTitle("Event Title");
        image.setOrganizer("Org_001");
        image.setRegistration_end(timestamp);

        assertEquals("img001", image.getId());
        assertEquals("https://example.com/image.png", image.getImageUrl());
        assertEquals("Event Title", image.getEventTitle());
        assertEquals("Org_001", image.getOrganizer());
        assertEquals(timestamp, image.getRegistration_end());
    }

    @Test
    public void testImageDefaults() {
        Image image = new Image();
        assertNull(image.getId());
        assertNull(image.getImageUrl());
        assertNull(image.getEventTitle());
        assertNull(image.getOrganizer());
        assertNull(image.getRegistration_end());
    }
}
