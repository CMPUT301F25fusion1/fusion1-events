package com.example.fusion1_events;


import static org.junit.Assert.assertEquals;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.firestore.CollectionReference;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
/*
* The test checks if all the methods in DataReferences class are returning the correct collection from the database.
 */
public class DatabaseReferencesTest {
    @Test
    public void CollectionPathsTest() {
        CollectionReference profileRef = DatabaseReferences.getProfileDatabase();

        CollectionReference organizersRef = DatabaseReferences.getOrganizersDatabase();
        CollectionReference adminsRef    = DatabaseReferences.getAdminDatabase();
        CollectionReference eventsRef    = DatabaseReferences.getEvents();
        CollectionReference entrantsRef  = DatabaseReferences.getEntrantsDatabase();

        assertEquals("Profile", profileRef.getId());
        assertEquals("Entrants", entrantsRef.getId());
        assertEquals("Organizers", organizersRef.getId());
        assertEquals("Admins", adminsRef.getId());
        assertEquals("Events", eventsRef.getId());
    }
}
