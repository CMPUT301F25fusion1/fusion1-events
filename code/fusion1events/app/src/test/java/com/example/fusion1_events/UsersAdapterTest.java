package com.example.fusion1_events;

import static org.junit.Assert.*;
import org.junit.Test;

import java.util.ArrayList;

public class UsersAdapterTest {

    @Test
    public void itemCount_isCorrect() {
        ArrayList<String> users = new ArrayList<>();
        users.add("u1");
        users.add("u2");
        users.add("u3");

        UsersAdapter adapter = new UsersAdapter(
                null,   // null context is OK because we won't inflate views
                users,
                "event123",
                false
        );

        assertEquals(3, adapter.getItemCount());
    }

    @Test
    public void itemCount_zeroWhenEmpty() {
        UsersAdapter adapter = new UsersAdapter(
                null,
                new ArrayList<>(),
                "event123",
                false
        );

        assertEquals(0, adapter.getItemCount());
    }
}
