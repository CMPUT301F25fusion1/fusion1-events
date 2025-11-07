package com.example.fusion1_events;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ProfileTest {
    @Test
    public void constructorTest(){
        Profile mockProfile = new Profile("Tom","tom@gmail.com"
                ,"123456789","ENTRANT","abcde");

        assertEquals("Tom", mockProfile.getName());
        assertEquals("tom@gmail.com", mockProfile.getEmail());
        assertEquals("123456789", mockProfile.getPhone_num());
        assertEquals("ENTRANT", mockProfile.getRole());
        assertEquals("abcde",mockProfile.getDevice_id());



    }
    @Test
    public void setterGetterTest(){
        Profile mockProfile2 = new Profile();

        mockProfile2.setName("Alan");
        mockProfile2.setEmail("alan@yahoo.com");
        mockProfile2.setPhone_num("+1 123 4567");
        mockProfile2.setRole("ADMIN");
        mockProfile2.setDevice_id("AbDeGf");

        assertEquals("Alan", mockProfile2.getName());
        assertEquals("alan@yahoo.com", mockProfile2.getEmail());
        assertEquals("+1 123 4567", mockProfile2.getPhone_num());
        assertEquals("ADMIN", mockProfile2.getRole());
        assertEquals("AbDeGf",mockProfile2.getDevice_id());

    }
}
