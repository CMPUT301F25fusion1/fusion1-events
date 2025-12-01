package com.example.fusion1_events;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Date;

public class NotificationModelTest {

    @Test
    public void constructorSetsFieldsTest() {
        String id = "notifID1234";
        String eventName = "Test Event";
        String title = "Test Title";
        String message = "Test Message";
        boolean read = false;
        Timestamp createdAt = new Timestamp(new Date());
        DocumentReference eventRef = mock(DocumentReference.class);
        DocumentReference receiverRef = mock(DocumentReference.class);
        DocumentReference senderRef = mock(DocumentReference.class);
        boolean notified = true;

        NotificationModel model = new NotificationModel(
                id,
                eventName,
                title,
                message,
                read,
                createdAt,
                eventRef,
                receiverRef,
                senderRef,
                notified
        );

        assertEquals(id, model.getNotificationId());
        assertEquals(eventName, model.getEventName());
        assertEquals(title, model.getNotificationTitle());
        assertEquals(message, model.getNotificationMessage());
        assertFalse(model.isRead());
        assertEquals(createdAt, model.getCreatedAt());
        assertEquals(eventRef, model.getNotificationEventId());
        assertEquals(receiverRef, model.getReceiverId());
        assertEquals(senderRef, model.getSenderID());
        assertTrue(model.isNotified());
    }

    @Test
    public void setReadTest() {
        NotificationModel model = new NotificationModel();

        assertFalse(model.isRead());

        model.setRead(true);
        assertTrue(model.isRead());


    }

    @Test
    public void setNotifiedTest() {
        NotificationModel model = new NotificationModel();

        assertFalse(model.isNotified());

        model.setNotified(true);
        assertTrue(model.isNotified());


    }

    @Test
    public void fromSnapshotTest() {

        DocumentSnapshot doc = mock(DocumentSnapshot.class);
        DocumentReference eventRef = mock(DocumentReference.class);
        DocumentReference receiverRef = mock(DocumentReference.class);
        DocumentReference senderRef = mock(DocumentReference.class);
        Timestamp createdAt = new Timestamp(new Date());

        when(doc.getId()).thenReturn("notifID1234");
        when(doc.getString("eventName")).thenReturn("Test Event");
        when(doc.getString("notificationTitle")).thenReturn("Test Title");
        when(doc.getString("notificationMessage")).thenReturn("Test Message");

        when(doc.getBoolean("read")).thenReturn(true);
        when(doc.getTimestamp("createdAt")).thenReturn(createdAt);
        when(doc.getDocumentReference("eventId")).thenReturn(eventRef);
        when(doc.getDocumentReference("receiverId")).thenReturn(receiverRef);
        when(doc.getDocumentReference("senderID")).thenReturn(senderRef);
        when(doc.getBoolean("notified")).thenReturn(true);


        NotificationModel model = NotificationModel.fromSnapshot(doc);


        assertEquals("notifID1234", model.getNotificationId());
        assertEquals("Test Event", model.getEventName());
        assertEquals("Test Title", model.getNotificationTitle());
        assertEquals("Test Message", model.getNotificationMessage());
        assertTrue(model.isRead());
        assertEquals(createdAt, model.getCreatedAt());
        assertEquals(eventRef, model.getNotificationEventId());
        assertEquals(receiverRef, model.getReceiverId());
        assertEquals(senderRef, model.getSenderID());
        assertTrue(model.isNotified());
    }

    @Test
    public void fromSnapshot_handlesNullStringsAndBooleans() {

        DocumentSnapshot doc = mock(DocumentSnapshot.class);
        DocumentReference eventRef = mock(DocumentReference.class);
        DocumentReference receiverRef = mock(DocumentReference.class);
        DocumentReference senderRef = mock(DocumentReference.class);
        Timestamp createdAt = new Timestamp(new Date());

        when(doc.getId()).thenReturn("notifID1234");
        when(doc.getString("eventName")).thenReturn(null);
        when(doc.getString("notificationTitle")).thenReturn(null);
        when(doc.getString("notificationMessage")).thenReturn(null);

        when(doc.getBoolean("read")).thenReturn(null);

        when(doc.getTimestamp("createdAt")).thenReturn(createdAt);
        when(doc.getDocumentReference("eventId")).thenReturn(eventRef);
        when(doc.getDocumentReference("receiverId")).thenReturn(receiverRef);
        when(doc.getDocumentReference("senderID")).thenReturn(senderRef);

        when(doc.getBoolean("notified")).thenReturn(null);


        NotificationModel model = NotificationModel.fromSnapshot(doc);


        assertEquals("notifID1234", model.getNotificationId());
        assertEquals("", model.getEventName());
        assertEquals("", model.getNotificationTitle());
        assertEquals("", model.getNotificationMessage());
        assertFalse(model.isRead());
        assertEquals(createdAt, model.getCreatedAt());
        assertEquals(eventRef, model.getNotificationEventId());
        assertEquals(receiverRef, model.getReceiverId());
        assertEquals(senderRef, model.getSenderID());
        assertFalse(model.isNotified());
    }
}
