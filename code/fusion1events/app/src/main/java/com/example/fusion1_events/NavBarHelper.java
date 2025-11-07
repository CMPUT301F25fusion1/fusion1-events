package com.example.fusion1_events;

import android.app.Activity;
import android.content.Intent;
import android.widget.Button;
import android.widget.LinearLayout;

public class NavBarHelper {
    public static void setupNavBar(Activity activity, Class<?> currentPage) {
        LinearLayout navBar = activity.findViewById(R.id.NavBar);

        Button buttonHome = navBar.findViewById(R.id.navButtonHome);
        Button buttonEvents = navBar.findViewById(R.id.navButtonEvents);
        Button buttonNotifications = navBar.findViewById(R.id.navButtonNotifications);
        Button buttonImages = navBar.findViewById(R.id.navButtonImages);

        // home button
        buttonHome.setOnClickListener(v -> {
            if (currentPage != AdminHomeActivity.class) {
                activity.startActivity(new Intent(activity, AdminHomeActivity.class));
            }
        });

        // events button
        buttonEvents.setOnClickListener(v-> {
            if (currentPage != AdminBrowseEventsActivity.class) {
                activity.startActivity(new Intent(activity, AdminBrowseEventsActivity.class));
            }
        });

        // images button
        buttonImages.setOnClickListener(v-> {
            if (currentPage != AdminBrowseImagesActivity.class) {
                activity.startActivity(new Intent(activity, AdminBrowseImagesActivity.class));
            }
        });
    }
}
