package com.example.fusion1_events;

import static org.junit.Assert.*;
import org.junit.Test;

import java.util.ArrayList;

public class DialogFragmentFactoryTests {

    @Test
    public void testFinalEntrantsDialogFragment_newInstance() {
        ArrayList<String> list = new ArrayList<>();
        list.add("A");

        FinalEntrantsDialogFragment frag =
                FinalEntrantsDialogFragment.newInstance("event1", list);

        assertNotNull(frag.getArguments());
        assertEquals("event1", frag.getArguments().getString("eventId"));
        assertEquals(1, frag.getArguments().getStringArrayList("confirmed").size());
    }

    @Test
    public void testCancelledEntrantsDialogFragment_newInstance() {
        ArrayList<String> list = new ArrayList<>();
        list.add("UserX");

        CancelledEntrantsDialogFragment frag =
                CancelledEntrantsDialogFragment.newInstance("event7", list);

        assertNotNull(frag.getArguments());
        assertEquals("event7", frag.getArguments().getString("eventId"));
        assertEquals(1, frag.getArguments().getStringArrayList("cancelled").size());
    }

    @Test
    public void testWaitingEntrantsDialogFragment_newInstance() {
        ArrayList<String> list = new ArrayList<>();
        list.add("WaitingUser");

        WaitingEntrantsDialogFragment frag =
                WaitingEntrantsDialogFragment.newInstance("eventABC", list);

        assertNotNull(frag.getArguments());
        assertEquals("eventABC", frag.getArguments().getString("eventId"));
        assertEquals(1, frag.getArguments().getStringArrayList("waitingList").size());
    }
}
