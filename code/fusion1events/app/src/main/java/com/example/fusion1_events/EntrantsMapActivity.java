package com.example.fusion1_events;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

/**
 * Displays a Google Map showing the locations of entrants for a specific event.
 * <p>
 * The event ID is passed via Intent extra ("eventId"). The activity retrieves the
 * event's waiting list from Firestore, fetches each entrant's coordinates, places
 * markers on the map, and automatically adjusts the camera to fit all visible markers.
 */
public class EntrantsMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private String eventId;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * Called when the activity is first created.
     * Initializes the map fragment and sets up the back button behavior.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           being previously shut down, this contains the
     *                           data it most recently supplied.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrants_map);

        eventId = getIntent().getStringExtra("eventId");

        // Set up the map fragment
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            Log.e("MAP", "Map fragment is null");
        }

        ImageButton backButton = findViewById(R.id.buttonBack);
        backButton.setOnClickListener(v -> finish());
    }

    /**
     * Called when the Google Map is ready to be used.
     * This method configures the map appearance, handles marker interactions,
     * retrieves entrant locations from Firestore, and automatically fits all
     * markers into the camera view.
     *
     * @param googleMap The GoogleMap instance that is ready for use.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        if (eventId == null) {
            Log.e("MAP", "EventId is null");
            return;
        }

        googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        // Zoom in when clicking a marker
        googleMap.setOnMarkerClickListener(marker -> {
            googleMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                            marker.getPosition(), 15f
                    )
            );
            marker.showInfoWindow();
            return true;
        });

        // Include all markers
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();

        db.collection("Events")
                .document(eventId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (!documentSnapshot.exists()) {
                        Log.e("MAP", "Event document does not exist");
                        return;
                    }

                    List<?> waitingList =
                            (List<?>) documentSnapshot.get("waitingList");

                    if (waitingList == null || waitingList.isEmpty()) {
                        Log.d("MAP", "Waiting list is empty");
                        return;
                    }

                    // Limit number of markers to prevent ANR
                    int limit = Math.min(waitingList.size(), 10);

                    for (int i = 0; i < limit; i++) {

                        Object obj = waitingList.get(i);

                        if (obj instanceof DocumentReference) {

                            DocumentReference entrantRef = (DocumentReference) obj;

                            entrantRef.get().addOnSuccessListener(entrantDoc -> {

                                if (!entrantDoc.exists()) return;

                                Double lat = entrantDoc.getDouble("latitude");
                                Double lng = entrantDoc.getDouble("longitude");
                                String name = entrantDoc.getString("name");

                                if (lat != null && lng != null) {

                                    LatLng location = new LatLng(lat, lng);

                                    googleMap.addMarker(
                                            new MarkerOptions()
                                                    .position(location)
                                                    .title(name != null ? name : "Entrant")
                                    );

                                    boundsBuilder.include(location);
                                }

                            }).addOnFailureListener(e ->
                                    Log.e("MAP", "Error fetching entrant document", e)
                            );
                        }
                    }

                    // Move camera after markers load
                    new Handler().postDelayed(() -> {
                        try {
                            LatLngBounds bounds = boundsBuilder.build();
                            int padding = 100;

                            googleMap.animateCamera(
                                    CameraUpdateFactory.newLatLngBounds(bounds, padding)
                            );

                        } catch (Exception e) {
                            Log.e("MAP", "Not enough locations to move camera", e);
                        }
                    }, 800);

                })
                .addOnFailureListener(e ->
                        Log.e("MAP", "Error loading event document", e)
                );
    }
}