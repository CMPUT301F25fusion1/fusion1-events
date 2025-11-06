package com.example.fusion1_events;

import com.google.firebase.Firebase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

/*
 * File: DatabaseReferences.java
 *
 * Role:
 * - To provide a centralized model for accessing all the collections in the firebase database.
 *
 * Issues:
 * - Assumes device is online.
 *
 */
public class DatabaseReferences {

    private static final FirebaseFirestore database = FirebaseFirestore.getInstance();

    public static FirebaseFirestore getDatabase(){
        return database;
    }

    public static CollectionReference getProfileDatabase(){
        return database.collection("Profile");
    }

    public static CollectionReference getEntrantsDatabase(){

        return database.collection("Entrants");
    }

    public static CollectionReference getOrganizersDatabase(){
        return database.collection("Organizers");
    }

    public static CollectionReference getAdminDatabase(){
        return database.collection("Admins");
    }
}
