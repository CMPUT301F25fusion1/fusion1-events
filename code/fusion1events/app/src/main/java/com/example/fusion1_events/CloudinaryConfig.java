package com.example.fusion1_events;

import android.content.Context;

import com.cloudinary.android.MediaManager;

import java.util.HashMap;
import java.util.Map;

// ADDED: Configuration class for Cloudinary
public class CloudinaryConfig {
    private static boolean isInitialized = false;

    private static final String CLOUD_NAME = "dcrljb6wh";
    private static final String API_KEY = "219232955685724";
    private static final String API_SECRET = "J6ysw9oa1n4rIVVdmGTh43LW4Yc";


    public static void init(Context context) {
        if (!isInitialized) {
            Map<String, String> config = new HashMap<>();
            config.put("cloud_name", CLOUD_NAME);
            config.put("api_key", API_KEY);
            config.put("api_secret", API_SECRET);
            config.put("secure", "true"); // Use HTTPS

            MediaManager.init(context, config);
            isInitialized = true;
        }
    }

    public static boolean isInitialized() {
        return isInitialized;
    }
}
