package com.example.fusion1_events;

import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static org.mockito.Mockito.*;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.firestore.DocumentSnapshot;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/*
* this test checks if the LoadAppActivity, opens correct activities as per the information of the user.
 */
@RunWith(AndroidJUnit4.class)
public class LoadAppActivityTest {

    @Before
    public void beforeTest() {
        Intents.init();
    }

    @After
    public void afterTest() {
        Intents.release();
    }

    // tests the condition where profile does not exist in document
    @Test
    public void profileDoesNotExist_opensSignUp() {

        ActivityScenario<LoadAppActivity> launchedClass =
                     ActivityScenario.launch(LoadAppActivity.class);

        launchedClass.onActivity(a -> a.profileDoesNotExist());

        intended(hasComponent(SignUpActivity.class.getName()));
        launchedClass.close();

    }

    // tests if user with entrant role is led to the EntrantHomeActivity
    @Test
    public void profileExist_Entrant() {

        DocumentSnapshot profile = mock(DocumentSnapshot.class);
        when(profile.exists()).thenReturn(true);
        when(profile.getString("role")).thenReturn("ENTRANT");
        when(profile.getString("device_id")).thenReturn("random_entrant_dev_id");

       ActivityScenario<LoadAppActivity> launchedClass = ActivityScenario.launch(LoadAppActivity.class);

       launchedClass.onActivity(a -> a.profileExists(profile));


       intended(hasComponent(EntrantHomeActivity.class.getName()));

       launchedClass.close();

    }

    // tests if user with organizer role is led to the OrganizerHomeActivity
    @Test
    public void profileExists_Organizer() {
        DocumentSnapshot profile = mock(DocumentSnapshot.class);
        when(profile.exists()).thenReturn(true);
        when(profile.getString("role")).thenReturn("ORGANIZER");
        when(profile.getString("device_id")).thenReturn("random_organizer_dev_id");

        ActivityScenario<LoadAppActivity> launchedClass = ActivityScenario.launch(LoadAppActivity.class);


        launchedClass.onActivity(a -> a.profileExists(profile));

        intended(hasComponent(OrganizerHomeActivity.class.getName()));
        launchedClass.close();
    }

    // tests if user with admin role is led to the AdminHomeActivity
    @Test
    public void profileExists_Admin() {

        DocumentSnapshot profile = mock(DocumentSnapshot.class);
        when(profile.exists()).thenReturn(true);
        when(profile.getString("role")).thenReturn("ADMIN");
        when(profile.getString("device_id")).thenReturn("random_admin_dev_id");

        ActivityScenario<LoadAppActivity> launchedClass = ActivityScenario.launch(LoadAppActivity.class);


        launchedClass.onActivity(a -> a.profileExists(profile));


        intended(hasComponent(AdminHomeActivity.class.getName()));
        launchedClass.close();
    }
}
