package com.example.fusion1_events;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * RecyclerView Adapter for displaying a list of events to admins.
 * <p>
 * Handles displaying the title, description, registration end date, and provides a button to view event details.
 */
public class AdminEventAdapter extends RecyclerView.Adapter<AdminEventAdapter.ViewHolder> {
    /**
     * Listener interface for handling actions on events.
     */
    public interface onEventActionListener {
        /**
         * Called when an admin wants to delete an event.
         *
         * @param event the event to delete
         */
        void onDeleteEvent(Event event);
    }
    private List<Event> events;
    private onEventActionListener listener;

    /**
     * Constructor for AdminEventAdapter.
     *
     * @param events   list of events to display
     * @param listener listener for handling event actions
     */
    public AdminEventAdapter(List<Event> events, onEventActionListener listener) {
        this.events = events;
        this.listener = listener;
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
        holder.title.setText(event.getTitle());
        holder.desc.setText(event.getDescription());
        if (event.getRegistration_end() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());
            holder.endDate.setText(sdf.format(event.getRegistration_end().toDate()));
        } else {
            holder.endDate.setText("");
        }

        // TODO: load image

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
        TextView title, desc, endDate;
        Button buttonViewDetails;
        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvEventTitle);
            desc = itemView.findViewById(R.id.tvEventDesc);
            endDate = itemView.findViewById(R.id.tvEventDate);
            buttonViewDetails = itemView.findViewById(R.id.buttonViewDetails);
        }
    }
}