package com.example.fusion1_events;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
/**
 * File: EventAdapter.java
 *
 * Role:
 * - Serves as the RecyclerView adapter for displaying event cards to an Entrant.
 * - Binds event data (title, description, date, image) to UI components.
 * - Handles navigation to the EventDetailActivity when an event card is clicked.
 * - Provides the ViewHolder implementation for event card layouts.
 *
 * Issues:
 * - Uses event title to query Firestore, which may cause issues if titles are not unique.
 * - Assumes Firestore queries succeed.
 * - No error handling for empty Firestore responses or failed queries.
 *
 */
public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private List<Event> eventList;
    private Context context;
    private Profile currentUser;
    /**
     * Creates an EventAdapter.
     *
     * @param context the context in which the adapter is used
     * @param eventList the list of events to display
     * @param currentUser the current user's profile
     */
    public EventAdapter(Context context, List<Event> eventList, Profile currentUser) {
        this.context = context;
        this.eventList = eventList;
        this.currentUser = currentUser;
    }
    /**
     * Inflates the event card layout and creates a new EventViewHolder.
     *
     * @param parent the parent view that will contain the new view
     * @param viewType unused view type parameter
     * @return a new EventViewHolder instance
     */
    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }
    /**
     * Binds event data to the provided EventViewHolder.
     * Sets the event title, description, date, and image.
     * Handles click events to navigate to EventDetailActivity.
     *
     * @param holder the view holder to bind data to
     * @param position the position of the event in the list
     */
    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);

        // Set the card contents
        holder.tvTitle.setText(event.getTitle());
        holder.tvDescription.setText(event.getDescription());
        Date date = event.getDate().toDate();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy h:mm a", Locale.getDefault());
        holder.tvDate.setText(sdf.format(date));
        holder.ivImage.setImageResource(R.drawable.ic_launcher_background);
        holder.itemView.setOnClickListener(v -> {
            FirebaseFirestore.getInstance()
                    .collection("Events")
                    .whereEqualTo("title", event.getTitle())
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        String eventId = querySnapshot.getDocuments().get(0).getId();
                        Intent intent = new Intent(context, EventDetailActivity.class);
                        intent.putExtra("eventId", eventId);
                        intent.putExtra("currentUser", currentUser);
                        context.startActivity(intent);
                    });
        });
    }
    /**
     * Returns the number of events in the list.
     *
     * @return the size of the event list
     */
    @Override
    public int getItemCount() {
        return eventList.size();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvTitle, tvDate, tvDescription;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivEventImage);
            tvTitle = itemView.findViewById(R.id.tvEventTitle);
            tvDate = itemView.findViewById(R.id.tvEventDate);
            tvDescription = itemView.findViewById(R.id.tvEventDescription);
        }
    }
}
