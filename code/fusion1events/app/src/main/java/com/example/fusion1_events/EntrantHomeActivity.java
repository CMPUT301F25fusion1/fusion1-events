package com.example.fusion1_events;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
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
 * - Provides navigation to:
 *      - Home screen
 *      - Entrant's events screen
 *      - Entrant's profile screen
 * - Initializes and binds an EventAdapter to show event cards for the entrant.
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
    private List<Event> filteredEvents = new ArrayList<>();
    private Profile currentUser;
    private Button profileButton;
    private String currentSelectedInterest = null;
    private Date currentSelectedDate = null;



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

                    List<String> interests = Arrays.asList(
                            "Sports", "Chill", "Education", "Remove Filter"
                    );
                    tvInterest.setOnClickListener(v -> showInterestDropdown(v, interests));

                    tvDate.setOnClickListener(v -> {
                        if (currentSelectedDate != null) {
                            currentSelectedDate = null;
                            tvDate.setText("Date");
                            tvDate.setTextColor(Color.BLACK);
                            filteredEvents.clear();
                            filteredEvents.addAll(eventList);
                            adapter.updateList(filteredEvents);
                            return;
                        }

                        Calendar calendar = Calendar.getInstance();
                        DatePickerDialog datePickerDialog = new DatePickerDialog(
                                this,
                                (view, year, month, dayOfMonth) -> {
                                    calendar.set(year, month, dayOfMonth);
                                    currentSelectedDate = calendar.getTime();

                                    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
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
                }
            });
        });

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
                filteredEvents.clear();
                filteredEvents.addAll(eventList);
                adapter.updateList(filteredEvents);
            } else {
                ((TextView) anchorView).setText(selectedInterest);
                this.currentSelectedInterest = selectedInterest;
                filterEventsByInterest(selectedInterest);
            }

            popupWindow.dismiss();
        });
    }

    private void filterEventsByInterest(String interest) {
        filteredEvents.clear();

        for (Event e : eventList) {
            if (e.getKeywords() != null &&
                    e.getKeywords().contains(interest)) {
                filteredEvents.add(e);
            }
        }

        adapter.updateList(filteredEvents);
    }

    private void filterEventsByDate(Date selectedDate) {
        filteredEvents.clear();

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
                    filteredEvents.add(e);
                }
            }
        }

        adapter.updateList(filteredEvents);
    }
}


