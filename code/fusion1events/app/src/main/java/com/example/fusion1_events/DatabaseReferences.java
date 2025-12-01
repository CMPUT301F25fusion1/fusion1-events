package com.example.fusion1_events;

import com.google.firebase.Firebase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * File: DatabaseReferences.java
 * Role:<br>
 * - Provides centralized access to all Firestore collections used in the application.<br>
 * - Ensures consistent reference names and reduces duplication across activities.<br>
 * <br>
 * Issues:<br>
 * - Assumes the device is online; Firestore references are not checked for connectivity.<br>
 */
public class DatabaseReferences {

    private static final FirebaseFirestore database = FirebaseFirestore.getInstance();

    /**
     * Returns the root Firestore database instance.
     *
     * @return the FirebaseFirestore instance used by the application
     */
    public static FirebaseFirestore getDatabase(){
        return database;
    }

    /**
     * Returns the Profile collection reference.
     *
     * @return CollectionReference for the "Profile" collection
     */
    public static CollectionReference getProfileDatabase(){
        return database.collection("Profile");
    }

    /**
     * Returns the Entrants collection reference.
     *
     * @return CollectionReference for the "Entrants" collection
     */
    public static CollectionReference getEntrantsDatabase(){

        return database.collection("Entrants");
    }

    /**
     * Returns the Organizers collection reference.
     *
     * @return CollectionReference for the "Organizers" collection
     */
    public static CollectionReference getOrganizersDatabase(){
        return database.collection("Organizers");
    }

    /**
     * Returns the Admins collection reference.
     *
     * @return CollectionReference for the "Admins" collection
     */
    public static CollectionReference getAdminDatabase(){
        return database.collection("Admins");
    }

    /**
     * Returns the Events collection reference.
     *
     * @return CollectionReference for the "Events" collection
     */
    public static CollectionReference getEvents() { return database.collection("Events");}

    /**
     * Returns the Notifications collection reference.
     *
     * @return CollectionReference for the "Notifications" collection
     */
    public static CollectionReference getNotificationDatabase(){
        return database.collection("Notifications");
    }
}
