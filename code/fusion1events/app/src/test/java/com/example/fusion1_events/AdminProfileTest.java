package com.example.fusion1_events;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class AdminProfileTest {
    @Test
    public void testAdminProfileGettersAndSetters() {
        AdminProfile profile = new AdminProfile();

        profile.setId("123");
        profile.setName("AdminName");
        profile.setEmail("username@server.domain");
        profile.setRole("ADMIN");
        profile.setNumber("1234567890");

        assertEquals("123", profile.getId());
        assertEquals("AdminName", profile.getName());
        assertEquals("username@server.domain", profile.getEmail());
        assertEquals("ADMIN", profile.getRole());
        assertEquals("1234567890", profile.getNumber());
    }

    @Test
    public void testAdminProfileDefaults() {
        AdminProfile profile = new AdminProfile();
        assertNull(profile.getId());
        assertNull(profile.getName());
        assertNull(profile.getEmail());
        assertNull(profile.getRole());
        assertNull(profile.getNumber());
    }
}
