package com.example.fusion1_events.admin;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fusion1_events.R;
import com.example.fusion1_events.admin.event.AdminBrowseEventsActivity;
import com.example.fusion1_events.admin.image.AdminBrowseImagesActivity;
import com.example.fusion1_events.admin.notification.AdminBrowseNotificationsActivity;
import com.example.fusion1_events.admin.profile.AdminBrowseProfilesActivity;


/**
 * Home screen activity for admins.
 * <p>
 * Provides buttons to navigate to different admin management screens:
 * events, images, profiles, and notifications.
 */
public class AdminHomeActivity extends AppCompatActivity {
    // Buttons for navigating to different admin screens
    Button buttonBrowseEvents, buttonBrowseImages, buttonViewNotifications, buttonBrowseProfiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        buttonBrowseEvents = findViewById(R.id.buttonBrowseEvents);
        buttonBrowseImages = findViewById(R.id.buttonBrowseImages);
        buttonViewNotifications = findViewById(R.id.buttonViewNotifications);
        buttonBrowseProfiles = findViewById(R.id.buttonBrowseProfiles);

        // Set click listeners to navigate to respective activities
        buttonBrowseEvents.setOnClickListener(v ->
                startActivity(new Intent(this, AdminBrowseEventsActivity.class)));

        buttonBrowseProfiles.setOnClickListener(v ->
                startActivity(new Intent(this, AdminBrowseProfilesActivity.class)));

        buttonBrowseImages.setOnClickListener(v ->
                startActivity(new Intent(this, AdminBrowseImagesActivity.class)));

        buttonViewNotifications.setOnClickListener(v ->
                startActivity(new Intent(this, AdminBrowseNotificationsActivity.class)));
    }
}
