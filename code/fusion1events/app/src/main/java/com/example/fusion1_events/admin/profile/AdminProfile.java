package com.example.fusion1_events.admin.profile;


/**
 * Represents an admin profile in the application.
 * <p>
 * This class contains information such as the profile's unique ID, name, email address, role,
 * and phone number.
 * It provides standard getters and setters to access and modify the profile details.
 */
public class AdminProfile {
    private String id;
    private String name;
    private String email;
    private String role;
    private String number;

    /**
     * Default constructor for AdminProfile.
     * <p>
     * Initializes a new instance of the AdminProfile class with default values.
     */
    public AdminProfile() {}

    // getters and setters
    /**
     * Returns the profile's ID.
     *
     * @return the profile ID
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the profile's ID.
     *
     * @param id the profile ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Returns the profile's name.
     *
     * @return the profile's name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the profile's name.
     *
     * @param name the profile's name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the profile's email address.
     *
     * @return the profile's email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the profile's email address.
     *
     * @param email the profile's email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns the profile's role.
     *
     * @return the profile's role
     */
    public String getRole() {
        return role;
    }

    /**
     * Sets the profile's role.
     *
     * @param role the profile's role
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Returns the profile's phone number.
     *
     * @return the profile's phone number
     */
    public String getNumber() {
        return number;
    }

    /**
     * Sets the profile's phone number.
     *
     * @param number the profile's phone number
     */
    public void setNumber(String number) {
        this.number = number;
    }
}
