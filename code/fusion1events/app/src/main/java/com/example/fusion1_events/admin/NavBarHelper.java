package com.example.fusion1_events.admin;

import android.app.Activity;
import android.content.Intent;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.fusion1_events.R;

/**
 * Helper class to set up the navigation bar in the admin interface.
 */
public class NavBarHelper {
    /**
     * Sets up the navigation bar buttons for an activity.
     * Each button navigates to the corresponding admin page unless the user is already on that page.
     *
     * @param activity the current activity where the nav bar is displayed
     * @param currentPage the class of the current page to prevent re-navigation
     */
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

        // TODO: notifications
    }
}
