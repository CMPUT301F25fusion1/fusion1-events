package com.example.fusion1_events;

import java.io.Serializable;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.IgnoreExtraProperties;

/**
 * File: Profile.java
 * <p>
 * Role: Represents a user's profile. A Profile stores the user information which includes name, email,
 * phone number, role, and device ID. This class is a Model Class ,and is used to grab and store user
 * information from the firestore database. The information will be used for different functionality
 * throughout the app.
 * </p>
 */
@IgnoreExtraProperties
public class Profile implements Serializable {
    private String name;
    private String email;
    private String phone_num;

    private String role;

    private String device_id;

    /**
     * Default constructor of Profile
     */
    public Profile() {}

    /**
     * Constructs a Profile object with the given attributes.
     *
     * @param name      the user's full name
     * @param email     the user's email address
     * @param phone_num the user's phone number
     * @param role      the user's role either "Entrant", "Organizer" or "Admin"
     * @param device_id the unique device ID of this profile
     */
    public Profile(String name, String email,String phone_num, String role, String device_id){
        this.name = name;
        this.email = email;
        this.phone_num = phone_num;
        this.role = role;
        this.device_id = device_id;
    }

    /**
     * Returns the user's name.
     *
     * @return the name of the user
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the user's name.
     *
     * @param name a string representing the user's name
     */

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the user's email address.
     *
     * @return the user's email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the user's email address.
     *
     * @param email a string in email pattern
     */
    public void setEmail(String email) {
        this.email = email;
    }


    /**
     * Returns the user's phone number.
     *
     * @return the phone number of a user (string or null)
     */
    public @Nullable String getPhone_num() {
        return phone_num;
    }

    /**
     * Sets the user's phone number.
     *
     * @param phone_num a string/null representing the phone number of the user
     */
    public void setPhone_num(@Nullable String phone_num) {
        this.phone_num = phone_num;
    }


    /**
     * Returns the user's role .
     *
     * @return the role string (Entrant, Organizer, Admin)
     */
    public String getRole() {
        return role;
    }

    /**
     * Sets the user's role.
     *
     * @param role a role string representing the user's role
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Returns the device ID of a profile.
     *
     * @return the device ID string
     */
    public String getDevice_id() {
        return device_id;
    }

    /**
     * Sets the device ID of a profile.
     *
     * @param device_id a unique identifier of the profile
     */
    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }
}
