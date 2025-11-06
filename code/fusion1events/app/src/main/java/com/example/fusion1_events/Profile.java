package com.example.fusion1_events;

import androidx.annotation.Nullable;

/*
 * File: Profile.java
 *
 * Role:
 * - Model for a user Profile.
 * Issues:
 * -
 *
 */
public class Profile {
    private String name;
    private String email;
    private String number;

    private String role;

    private String device_id;

    public Profile(){}
    public Profile(String name, String email, @Nullable String phone_num, String role, String device_id){
        this.name = name;
        this.email = email;
        this.number = phone_num;
        this.role = role;
        this.device_id = device_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    @Nullable public String getNumber() {
        return number;
    }

    public void setNumber(@Nullable String number) {
        this.number = number;
    }


    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }


    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }
}
