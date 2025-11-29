package com.example.fusion1_events.admin;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fusion1_events.Event;
import com.example.fusion1_events.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * RecyclerView Adapter for displaying a list of events to admins.
 * <p>
 * Handles displaying the title, description, registration end date, and provides a button to view event details.
 */
public class AdminEventAdapter extends RecyclerView.Adapter<AdminEventAdapter.ViewHolder> {


    private List<Event> events;

    /**
     * Constructor for AdminEventAdapter.
     *
     * @param events   list of events to display
     */
    public AdminEventAdapter(List<Event> events) {
        this.events = events;
    }

    /**
     * Inflates the layout for each event item.
     *
     * @param parent   the parent ViewGroup
     * @param viewType the type of view
     * @return a ViewHolder for the event item
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event_admin, parent, false);
        return new ViewHolder(v);
    }

    /**
     * Binds event data to the ViewHolder.
     *
     * @param holder   the ViewHolder to bind data to
     * @param position the position of the event in the list
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = events.get(position);

        int titleLimit = 18;
        String eventTitle = event.getTitle();
        if (eventTitle != null && eventTitle.length() > titleLimit) {
            eventTitle = eventTitle.substring(0, titleLimit) + "...";
        }
        holder.title.setText(eventTitle);

        int descriptionLimit = 72;
        String eventDescription = event.getDescription();
        if (eventDescription != null && eventDescription.length() > descriptionLimit) {
            eventDescription = eventDescription.substring(0, descriptionLimit) + "...";
        }
        holder.description.setText(eventDescription);

        if (event.getRegistration_end() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());
            holder.endDate.setText(sdf.format(event.getRegistration_end().toDate()));
        } else {
            holder.endDate.setText("");
        }

        // Load image
        String imageUrl = event.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .into(holder.imageEvent);
        } else {
            holder.imageEvent.setImageResource(R.drawable.ic_admin_image_placeholder_foreground);
        }

        // View Details button opens AdminEventDetailsActivity
        holder.buttonViewDetails.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), AdminEventDetailsActivity.class);
            intent.putExtra("eventId", event.getId());
            v.getContext().startActivity(intent);
        });
    }

    /**
     * Returns the total number of events.
     *
     * @return the size of the events list
     */
    @Override public int getItemCount() { return events.size(); }

    /**
     * ViewHolder class for an individual event item.
     * <p>
     * Holds references to UI components for an event item.
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, endDate;
        ImageView imageEvent;
        Button buttonViewDetails;
        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvEventTitle);
            description = itemView.findViewById(R.id.tvEventDescription);
            endDate = itemView.findViewById(R.id.tvEventDate);
            imageEvent = itemView.findViewById(R.id.imageEvent);
            buttonViewDetails = itemView.findViewById(R.id.buttonViewDetails);
        }
    }
}