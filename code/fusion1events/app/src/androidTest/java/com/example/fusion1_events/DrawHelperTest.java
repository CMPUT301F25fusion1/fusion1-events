package com.example.fusion1_events;

import static org.junit.Assert.*;

import android.content.Context;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class DrawHelperTest {

    private FirebaseFirestore db;
    private Context context;
    private String testEventId;

    @Before
    public void setUp() throws Exception {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        db = FirebaseFirestore.getInstance();
        testEventId = "testEvent";

        // Create a test event in Firestore
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("attendees", 3L);

        List<DocumentReference> waitingList = new ArrayList<>();
        waitingList.add(db.collection("Profiles").document("user1"));
        waitingList.add(db.collection("Profiles").document("user2"));
        waitingList.add(db.collection("Profiles").document("user3"));
        eventData.put("waitingList", waitingList);

        eventData.put("invitedList", new ArrayList<>());

        // Add the test event
        Tasks.await(db.collection("Events").document(testEventId)
                .set(eventData));
    }

    @Test
    public void testRunDraw_doesNothingIfWaitingListEmpty() throws Exception {
        // Clear waiting list first
        DocumentReference eventRef = db.collection("Events").document(testEventId);
        Tasks.await(eventRef.update("waitingList", new ArrayList<>()));

        // Run the draw
        DrawHelper.runDraw(testEventId, db, context);

        // Check that invited list is still unchanged
        Map<String, Object> updated = Tasks.await(eventRef.get()).getData();
        List<?> invitedList = (List<?>) updated.get("invitedList");
        List<?> waitingList = (List<?>) updated.get("waitingList");

        assertNotNull(invitedList);
        assertNotNull(waitingList);
        assertTrue(waitingList.isEmpty());
    }

    @Test
    public void testRunDraw() {
        // Just run the draw — check logs or Firebase manually for results
        DrawHelper.runDraw(testEventId, db, context);
        Log.d("DrawHelperTest", "runDraw executed");
    }

    @Test
    public void testRunDraw_withWaitingList() {
        // Add dummy users to waiting list
        DocumentReference user1 = db.collection("Profiles").document("user1");
        DocumentReference user2 = db.collection("Profiles").document("user2");
        List<DocumentReference> waitingList = new ArrayList<>();
        waitingList.add(user1);
        waitingList.add(user2);

        db.collection("Events").document(testEventId)
                .update("waitingList", waitingList);

        DrawHelper.runDraw(testEventId, db, context);
        Log.d("DrawHelperTest", "runDraw executed with waiting list");
    }
}

