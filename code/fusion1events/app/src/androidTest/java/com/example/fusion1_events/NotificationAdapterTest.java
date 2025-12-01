package com.example.fusion1_events;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import android.content.Context;
import android.view.ViewGroup;


import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@RunWith(AndroidJUnit4.class)
public class NotificationAdapterTest {



    @Before
    public void setup() {
        Intents.init();
    }

    @After
    public void teardown() {
        Intents.release();
    }

    @Test
    public void markReadButtonTest() {
        Context context = ApplicationProvider.getApplicationContext();

        String notifId = "notifID1234";
        NotificationModel model = new NotificationModel(
                notifId,
                "Test Event",
                "Test Title",
                "Test Message",
                false,
                new Timestamp(new Date()),
                mock(DocumentReference.class),
                mock(DocumentReference.class),
                mock(DocumentReference.class),
                false
        );

        List<NotificationModel> items = new ArrayList<>();
        items.add(model);


        CollectionReference notifRef = mock(CollectionReference.class);
        DocumentReference docRef = mock(DocumentReference.class);
        @SuppressWarnings("unchecked")
        Task<Void> mockTask = (Task<Void>) mock(Task.class);

        when(notifRef.document(notifId)).thenReturn(docRef);
        when(docRef.update("read", true)).thenReturn(mockTask);


        when(mockTask.addOnSuccessListener(any(OnSuccessListener.class)))
                .thenAnswer(invocation -> {
                    OnSuccessListener<Void> listener = invocation.getArgument(0);
                    listener.onSuccess(null);
                    return mockTask; // fluent API
                });

        NotificationAdapter adapter =
                new NotificationAdapter(context, items, notifRef, null);

        ViewGroup parent = new android.widget.FrameLayout(context);
        NotificationAdapter.NotificationViewHolder holder =
                adapter.onCreateViewHolder(parent, 0);

        adapter.onBindViewHolder(holder, 0);


        holder.markReadButton.performClick();


        verify(notifRef).document(notifId);
        verify(docRef).update("read", true);
        assertTrue(model.isRead());
    }


}
