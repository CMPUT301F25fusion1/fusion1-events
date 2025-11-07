package com.example.fusion1_events;
// change to admin_home
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class AdminHomeActivity extends AppCompatActivity {
    Button buttonBrowseEvents, buttonBrowseImages, buttonViewNotifications, buttonBrowseProfiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        buttonBrowseEvents = findViewById(R.id.buttonBrowseEvents);
        buttonBrowseImages = findViewById(R.id.buttonBrowseImages);
        buttonViewNotifications = findViewById(R.id.buttonViewNotifications);
        buttonBrowseProfiles = findViewById(R.id.buttonBrowseProfiles);

        buttonBrowseEvents.setOnClickListener(v ->
                startActivity(new Intent(this, AdminBrowseEventsActivity.class)));

        buttonBrowseProfiles.setOnClickListener(v ->
                startActivity(new Intent(this, AdminBrowseProfilesActivity.class)));

        buttonBrowseImages.setOnClickListener(v ->
                startActivity(new Intent(this, AdminBrowseImagesActivity.class)));

    }
}
