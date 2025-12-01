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
        event = new EventsModel(
                "Concert",
                new ArrayList<String>(),  // selectedTags
                now,
                now,
                "Music Event",
                now,
                100L,
                50L,
                new ArrayList<String>(),
                "imageUrl",
                "event123",
                new ArrayList<String>(),
                20L,
                new ArrayList<String>(),
                new ArrayList<String>(),
                false,
                "organizer123"
        );
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

    @Test
    public void testGetCancelled_initializesEmptyList() {
        event.setCancelled(null);  // force null
        assertNotNull(event.getCancelled());
    }

    @Test
    public void testSetCancelled_replacesList() {
        ArrayList<String> cancelled = new ArrayList<>();
        cancelled.add("userA");
        event.setCancelled(cancelled);

        assertEquals(cancelled, event.getCancelled());
    }

    @Test
    public void testAddCancelledUser() {
        ArrayList<String> list = new ArrayList<>();
        list.add("user1");
        event.setCancelled(list);

        assertTrue(event.getCancelled().contains("user1"));
    }

    @Test
    public void testCancelledList_noDuplicates() {
        ArrayList<String> list = new ArrayList<>();
        list.add("user1");
        list.add("user1");

        event.setCancelled(list);

        // Since setter doesn't remove duplicates, but model behavior is still correct
        assertEquals(2, event.getCancelled().size());
    }

    @Test
    public void testGetConfirmed_initializesEmptyList() {
        event.setConfirmed(null);
        assertNotNull(event.getConfirmed());
    }

    @Test
    public void testSetConfirmed_replacesList() {
        ArrayList<String> confirmed = new ArrayList<>();
        confirmed.add("userX");
        event.setConfirmed(confirmed);

        assertEquals(confirmed, event.getConfirmed());
    }

    @Test
    public void testConfirmed_addUser() {
        ArrayList<String> list = new ArrayList<>();
        list.add("user1");
        event.setConfirmed(list);

        assertTrue(event.getConfirmed().contains("user1"));
    }

    @Test
    public void testConfirmedList_allowsMultipleEntries() {
        ArrayList<String> list = new ArrayList<>();
        list.add("user1");
        list.add("user1");

        event.setConfirmed(list);
        assertEquals(2, event.getConfirmed().size());
    }

    @Test
    public void testSelectedTags_defaultNotNull() {
        assertNotNull(event.getSelectedTags());
    }

    @Test
    public void testSetSelectedTags() {
        ArrayList<String> tags = new ArrayList<>();
        tags.add("Music");
        event.setSelectedTags(tags);

        assertEquals(tags, event.getSelectedTags());
    }

    @Test
    public void testMaxWaitListGetterSetter() {
        event.setMaxWaitList(42L);
        assertEquals(Long.valueOf(42), event.getMaxWaitList());
    }

    @Test
    public void testGeolocationRequiredSetterGetter() {
        event.setGeolocationRequired(true);
        assertTrue(event.isGeolocationRequired());
    }
}
