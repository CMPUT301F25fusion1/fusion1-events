package com.example.fusion1_events;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Simple local unit test for GuidelinesActivity.
 * This test runs on the JVM (not an Android device).
 */
public class GuidelinesActivityTest {

    private GuidelinesActivity guidelinesActivity;

    @Before
    public void setUp() {
        // We can't call Android lifecycle methods in a local test,
        // but we can still instantiate the class.
        guidelinesActivity = new GuidelinesActivity();
    }

    @Test
    public void testGuidelinesActivityNotNull() {
        assertNotNull("GuidelinesActivity should be created", guidelinesActivity);
    }

    @Test
    public void testClassNameIsCorrect() {
        assertEquals("com.example.fusion1_events.GuidelinesActivity",
                guidelinesActivity.getClass().getName());
    }
}
