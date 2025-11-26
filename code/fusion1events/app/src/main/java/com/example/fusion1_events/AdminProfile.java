package com.example.fusion1_events;

/**
 * Represents an admin profile in the application.
 * <p>
 * Contains information such as ID, name, email, role, and phone number.
 */
public class AdminProfile {
    private String id;
    private String name;
    private String email;
    private String role;
    private String number;

    // default constructor
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
     * Returns the admin's name.
     *
     * @return the admin's name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the admin's name.
     *
     * @param name the admin's name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the admin's email address.
     *
     * @return the admin's email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the admin's email address.
     *
     * @param email the admin's email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns the admin's role.
     *
     * @return the admin's role
     */
    public String getRole() {
        return role;
    }

    /**
     * Sets the admin's role.
     *
     * @param role the admin's role
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Returns the admin's contact number.
     *
     * @return the admin's phone number
     */
    public String getNumber() {
        return number;
    }

    /**
     * Sets the admin's contact number.
     *
     * @param number the admin's phone number
     */
    public void setNumber(String number) {
        this.number = number;
    }
}
