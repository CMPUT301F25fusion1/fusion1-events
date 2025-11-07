package com.example.fusion1_events;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class GuidelinesActivity extends AppCompatActivity {
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
