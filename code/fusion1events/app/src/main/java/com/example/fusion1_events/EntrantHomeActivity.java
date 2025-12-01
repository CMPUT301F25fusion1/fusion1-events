package com.example.fusion1_events;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.installations.FirebaseInstallations;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * File: EntrantHomeActivity.java
 *
 * Role:
 * - Acts as the home screen for users with the Entrant role.
 * - Retrieves the current user’s profile using their device ID.
 * - Displays a list of all available events from Firestore.
 * - Allows user to filter events by interests or by date.
 * - Provides navigation to:
 *      - Home screen
 *      - Entrant's events screen
 *      - Entrant's profile screen
 *      - Entrant's notification screen
 * - Initializes and binds an EventAdapter to show event cards for the entrant.
 * - Notify user of any notification that they have not interacted with yet.
 *
 * Issues:
 * - Assumes successful retrieval of the Firebase Installation ID.
 * - Assumes device is online for Firestore operations.
 * - No explicit error handling for missing profiles or failed Firestore queries.
 */
public class EntrantHomeActivity extends AppCompatActivity {

    private CollectionReference profileRef;
    private CollectionReference eventsRef;
    private RecyclerView eventsRecyclerView;
    private EventAdapter adapter;
    private List<Event> eventList;
    private List<Event> filterByInterest = new ArrayList<>();
    private List<Event> filterByDate = new ArrayList<>();
    private Profile currentUser;
    private Button profileButton;
    private String currentSelectedInterest = null;
    private Date currentSelectedDate = null;

    private CollectionReference notifRef;
    private CollectionReference entrantsRef;

    private LinearLayout notificationBanner;
    private TextView notificationBannerText;
    private Button notificationBannerViewButton;
    private Button notificationBannerCloseButton;


    private List<DocumentSnapshot> pendingNotifications = new ArrayList<>();
    private int currentNotifIndex = 0;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_event_screen);

        profileRef = DatabaseReferences.getProfileDatabase();
        eventsRef = DatabaseReferences.getEvents();
        eventsRecyclerView = findViewById(R.id.events_recycler_view);
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventList = new ArrayList<>();

        notifRef = DatabaseReferences.getNotificationDatabase();
        entrantsRef = DatabaseReferences.getEntrantsDatabase();


        notificationBanner = findViewById(R.id.notificationBanner);
        notificationBannerText = findViewById(R.id.notificationBannerText);
        notificationBannerViewButton = findViewById(R.id.notificationBannerViewButton);
        notificationBannerCloseButton = findViewById(R.id.notificationBannerCloseButton);

        Button lotteryGuidelinesBtn = findViewById(R.id.lottery_guidelines_btn);
        lotteryGuidelinesBtn.setOnClickListener(v -> {
            Intent intent = new Intent(EntrantHomeActivity.this, GuidelinesActivity.class);
            startActivity(intent);
        });

        FirebaseInstallations.getInstance().getId().addOnSuccessListener(deviceId -> {

            profileRef.document(deviceId).get().addOnSuccessListener(profile -> {

                if (profile.exists()) {
                    currentUser = profile.toObject(Profile.class);

                    TextView tvHome = findViewById(R.id.tvHome);
                    TextView tvYourEvents = findViewById(R.id.tvYourEvents);
                    TextView tvYourProfile = findViewById(R.id.tvYourProfile);
                    TextView tvInterest = findViewById(R.id.tvInterest);
                    TextView tvDate = findViewById(R.id.tvDate);
                    ImageView tvDetailImage = findViewById(R.id.ivDetailImage);
                    TextView tvNotifications = findViewById(R.id.tvNotifications);


                    tvYourProfile.setOnClickListener(v -> {
                        Intent intent = new Intent(EntrantHomeActivity.this, ProfileViewActivity.class);
                        startActivity(intent);
                    });

                    tvHome.setOnClickListener(v -> {
                        Intent intent = new Intent(EntrantHomeActivity.this, EntrantHomeActivity.class);
                        intent.putExtra("currentUser", currentUser);
                        startActivity(intent);
                    });

                    tvYourEvents.setOnClickListener(v -> {
                        Intent intent = new Intent(EntrantHomeActivity.this, YourEventsActivity.class);
                        intent.putExtra("currentUser", currentUser);
                        startActivity(intent);
                    });

                    tvNotifications.setOnClickListener(v -> {
                        Intent intent = new Intent(EntrantHomeActivity.this, NotificationsActivity.class);
                        intent.putExtra("currentUser", currentUser);
                        startActivity(intent);
                    });




                    List<String> interests = Arrays.asList(
                            "Chill \uD83E\uDD1F", "Sports \uD83C\uDFC0", "Educational \uD83C\uDFC0",
                            "Seasonal ☃\uFE0F","Party \uD83C\uDF89", "Remove Filter"
                    );
                    tvInterest.setOnClickListener(v -> showInterestDropdown(v, interests));

                    tvDate.setOnClickListener(v -> {
                        if (tvDate.getText() == "Clear filter") {
                            currentSelectedDate = null;
                            tvDate.setText("Date");
                            tvDate.setTextColor(Color.BLACK);
                            filterByDate.clear();
                            if (currentSelectedInterest != null) {
                                filterByInterest = filterEventsByInterest(currentSelectedInterest);
                                filterByDate.addAll(filterByInterest);
                            } else {
                                filterByDate.addAll(eventList);
                            }
                            adapter.updateList(filterByDate);
                            return;
                        }

                        Calendar calendar = Calendar.getInstance();
                        DatePickerDialog datePickerDialog = new DatePickerDialog(
                                this,
                                (view, year, month, dayOfMonth) -> {
                                    calendar.set(year, month, dayOfMonth);
                                    currentSelectedDate = calendar.getTime();

                                    tvDate.setText("Clear filter");
                                    tvDate.setTextColor(Color.RED);

                                    filterEventsByDate(currentSelectedDate);
                                },
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)
                        );
                        datePickerDialog.show();
                    });

                    adapter = new EventAdapter(this, eventList, currentUser);
                    eventsRecyclerView.setAdapter(adapter);

                    eventsRef
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                for (DocumentSnapshot doc : queryDocumentSnapshots) {
                                    Event event = doc.toObject(Event.class);
                                    eventList.add(event);
                                }
                                adapter.notifyDataSetChanged();
                            });
                    loadNotificationsOnce(deviceId);
                }
            });
        });

    }

    private void loadNotificationsOnce(String deviceId) {
        DocumentReference entrantRef = entrantsRef.document(deviceId);

        notifRef.whereEqualTo("receiverId", entrantRef)
                .whereEqualTo("read", false)
                .whereEqualTo("notified", false)
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    if (querySnapshot == null || querySnapshot.isEmpty()) {
                        hideBanner();
                        return;
                    }

                    pendingNotifications.clear();
                    pendingNotifications.addAll(querySnapshot.getDocuments());

                    // sort by createdAt (newest first)
                    pendingNotifications.sort((d1, d2) -> {
                        com.google.firebase.Timestamp t1 = d1.getTimestamp("createdAt");
                        com.google.firebase.Timestamp t2 = d2.getTimestamp("createdAt");

                        if (t1 == null && t2 == null) return 0;
                        if (t1 == null) return 1;
                        if (t2 == null) return -1;
                        return t2.compareTo(t1); // descending
                    });

                    currentNotifIndex = 0;
                    showNextNotificationFromQueue();
                })
                .addOnFailureListener(e -> {
                    hideBanner();
                });
    }

    private void showNextNotificationFromQueue() {
        if (pendingNotifications == null ||
                pendingNotifications.isEmpty() ||
                currentNotifIndex >= pendingNotifications.size()) {

            hideBanner();
            return;
        }

        DocumentSnapshot doc = pendingNotifications.get(currentNotifIndex);

        String notifId = doc.getId();
        String title = doc.getString("notificationTitle");
        String message = doc.getString("notificationMessage");
        DocumentReference eventRef = doc.getDocumentReference("eventId");

        if (title == null) title = "Notification";
        if (message == null) message = "";


        notifRef.document(notifId).update("notified", true);

        showNotificationBanner(title, message, eventRef);
    }




    private void showInterestDropdown(View anchorView, List<String> interests) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_interest_list, null);

        ListView listView = popupView.findViewById(R.id.listInterests);

        ArrayAdapter<String> listAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                interests
        ) {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                TextView tv = (TextView) super.getView(position, convertView, parent);

                if (position == interests.size() - 1) {
                    tv.setTextColor(Color.RED);
                } else {
                    tv.setTextColor(Color.BLACK);
                }

                return tv;
            }
        };
        listView.setAdapter(listAdapter);

        PopupWindow popupWindow = new PopupWindow(
                popupView,
                anchorView.getWidth(),
                LinearLayout.LayoutParams.WRAP_CONTENT,
                true
        );

        popupWindow.showAsDropDown(anchorView);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedInterest = interests.get(position);

            if (selectedInterest.equals("Remove Filter")) {
                this.currentSelectedInterest = null;
                ((TextView) anchorView).setText("Interest");
                filterByInterest.clear();
                if (currentSelectedDate != null){
                    filterByDate = filterEventsByDate(currentSelectedDate);
                    filterByInterest.addAll(filterByDate);
                } else {
                    filterByInterest.addAll(eventList);
                }
                adapter.updateList(filterByInterest);
            } else {
                ((TextView) anchorView).setText(selectedInterest);
                this.currentSelectedInterest = selectedInterest;
                filterEventsByInterest(selectedInterest);
            }

            popupWindow.dismiss();
        });
    }

    private List<Event> filterEventsByInterest(String interest) {
        filterByInterest.clear();

        for (Event e : eventList) {
            if (e.getKeywords() != null &&
                    e.getKeywords().contains(interest)) {
                if (currentSelectedDate == null || filterByDate.contains(e)){
                    filterByInterest.add(e);
                }
            }
        }

        adapter.updateList(filterByInterest);
        return filterByInterest;
    }

    private List<Event> filterEventsByDate(Date selectedDate) {
        filterByDate.clear();

        for (Event e : eventList) {
            Date eventDate = e.getDate().toDate();
            if (eventDate != null) {
                Calendar c1 = Calendar.getInstance();
                c1.setTime(eventDate);
                Calendar c2 = Calendar.getInstance();
                c2.setTime(selectedDate);

                if (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
                        c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH) &&
                        c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH)) {
                    if (currentSelectedInterest == null || filterByInterest.contains(e)){
                        filterByDate.add(e);
                    }
                }
            }
        }

        adapter.updateList(filterByDate);
        return filterByDate;
    }

    private void showNotificationBanner(String title,
                                        String message,
                                        DocumentReference eventRef) {

        String formatted = "<b>" + title + "</b><br>" + message;
        notificationBannerText.setText(Html.fromHtml(formatted));

        notificationBannerViewButton.setOnClickListener(v -> {
            if (eventRef != null) {
                String eventId = eventRef.getId();
                Intent intent = new Intent(this, EventDetailActivity.class);
                intent.putExtra("eventId", eventId);
                intent.putExtra("currentUser", currentUser);
                startActivity(intent);
            }
            currentNotifIndex++;
            showNextNotificationFromQueue();
        });

        notificationBannerCloseButton.setOnClickListener(v -> {
            currentNotifIndex++;
            showNextNotificationFromQueue();
        });

        showBanner();
    }


    private void showBanner() {
        notificationBanner.setVisibility(View.VISIBLE);
        notificationBanner.setAlpha(0f);

        notificationBanner.post(() -> {
            float startY = -notificationBanner.getHeight() * 2f;
            notificationBanner.setTranslationY(startY);

            notificationBanner.animate()
                    .translationY(0f)
                    .alpha(1f)
                    .setDuration(300)
                    .start();
        });
    }
    private void hideBanner() {
        notificationBanner.setVisibility(View.GONE);
        notificationBanner.setTranslationY(0f);
        notificationBanner.setAlpha(1f);
    }
}


