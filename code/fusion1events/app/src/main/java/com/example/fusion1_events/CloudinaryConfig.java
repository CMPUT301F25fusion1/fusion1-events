package com.example.fusion1_events;

import android.content.Context;

import com.cloudinary.android.MediaManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration class for initializing Cloudinary MediaManager.
 * Provides a singleton pattern to ensure Cloudinary is initialized only once
 * with the necessary credentials and settings.
 *
 * WARNING: This class contains hardcoded API credentials which should be moved
 * to a secure configuration file or environment variables in production.
 */
public class CloudinaryConfig {
    private static boolean isInitialized = false;

    /**
     * Cloudinary cloud name for the account.
     */
    private static final String CLOUD_NAME = "dcrljb6wh";

    /**
     * Cloudinary API key for authentication.
     * WARNING: Should be stored securely, not hardcoded.
     */
    private static final String API_KEY = "219232955685724";

    /**
     * Cloudinary API secret for authentication.
     * WARNING: Should be stored securely, not hardcoded.
     */
    private static final String API_SECRET = "J6ysw9oa1n4rIVVdmGTh43LW4Yc";

    /**
     * Initializes the Cloudinary MediaManager with the configured credentials.
     * This method uses a singleton pattern to ensure initialization happens only once.
     * Subsequent calls to this method will be ignored if already initialized.
     *
     * The configuration includes:
     * - Cloud name for the Cloudinary account
     * - API key and secret for authentication
     * - Secure flag set to true to use HTTPS
     *
     * @param context The Android application context needed for MediaManager initialization
     */
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

    /**
     * Checks whether Cloudinary MediaManager has been initialized.
     *
     * @return true if MediaManager has been initialized, false otherwise
     */
    public static boolean isInitialized() {
        return isInitialized;
    }

    /**
     * Resets the initialization state.
     * This method is primarily intended for testing purposes to allow re-initialization.
     * Use with caution in production code.
     */
    static void resetForTesting() {
        isInitialized = false;
    }
}