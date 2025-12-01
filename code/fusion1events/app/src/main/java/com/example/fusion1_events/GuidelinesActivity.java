package com.example.fusion1_events;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Activity that displays app guidelines to entrant users.
 * Provides simple navigation options back to the home screen
 * or the user's events page.
 */
public class GuidelinesActivity extends AppCompatActivity {

    /**
     * Initializes the guidelines screen and sets up navigation click listeners.
     *
     * @param savedInstanceState Saved instance state from Android, or null if none.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guidelines);

        TextView tvHome = findViewById(R.id.tvHome);
        TextView tvYourEvents = findViewById(R.id.tvYourEvents);

        // Navigate to Event Screen
        tvHome.setOnClickListener(v -> {
            Intent intent = new Intent(GuidelinesActivity.this, EntrantHomeActivity.class);
            startActivity(intent);
        });

        // Navigate to Your Events
        tvYourEvents.setOnClickListener(v -> {
            Intent intent = new Intent(GuidelinesActivity.this, YourEventsActivity.class);
            startActivity(intent);
        });

        // The other buttons ("Scan", "Profile", "Notifications") do nothing
    }
}
