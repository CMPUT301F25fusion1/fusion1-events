package com.example.fusion1_events;

import java.io.Serializable;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.IgnoreExtraProperties;

/*
 * File: Profile.java
 *
 * Role:
 * - Model for a user Profile.
 * Issues:
 * -
 *
 */
@IgnoreExtraProperties
public class Profile implements Serializable {
    private String name;
    private String email;
    private String phone_num;

    private String role;

    private String device_id;

    public Profile() {}

    public Profile(String name, String email,String phone_num, String role, String device_id){
        this.name = name;
        this.email = email;
        this.phone_num = phone_num;
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


    public @Nullable String getPhone_num() {
        return phone_num;
    }

    public void setPhone_num(@Nullable String phone_num) {
        this.phone_num = phone_num;
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
