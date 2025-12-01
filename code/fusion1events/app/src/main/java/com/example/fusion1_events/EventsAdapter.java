package com.example.fusion1_events;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
/**
 * Adapter for displaying a list of EventsModel objects in a RecyclerView.
 *
 * Each item shows the event title, registration dates, attendees, and a banner image.
 * It also provides an options menu for editing or deleting events and supports click callbacks
 * via the OnEventClickListener interface.
 */
public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventViewHolder> {

    private ArrayList<EventsModel> events;
    private OnEventClickListener listener;
    private Context context;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

    /**
     * Listener interface for handling interactions with event list items.
     * Provides callbacks for viewing, editing, deleting, and viewing sample entrants.
     */
    public interface OnEventClickListener {
        /**
         * Called when an event card is tapped.
         *
         * @param event    The selected event.
         * @param position The event's position in the list.
         */
        void onEventClick(EventsModel event, int position);

        /**
         * Called when the user selects "Edit" from an event's options menu.
         *
         * @param event    The selected event.
         * @param position The event's position in the list.
         */
        void onEditClick(EventsModel event, int position);

        /**
         * Called when the user selects "Delete" from an event's options menu.
         *
         * @param event    The selected event.
         * @param position The event's position in the list.
         */
        void onDeleteClick(EventsModel event, int position);

        /**
         * Called when the user selects "Sample Entrants" from an event's options menu.
         *
         * @param event    The selected event.
         * @param position The event's position in the list.
         */
        void onSampleClick(EventsModel event, int position);

    }

    /**
     * Creates a new EventsAdapter for displaying organizer events.
     *
     * @param context  The context where the adapter is used.
     * @param events   The list of events to bind.
     * @param listener Callback listener for item interactions.
     */
    public EventsAdapter(Context context, ArrayList<EventsModel> events, OnEventClickListener listener) {
        this.context = context;
        this.events = events;
        this.listener = listener;
    }

    /**
     * Inflates the layout for individual event cards.
     *
     * @param parent   Parent ViewGroup.
     * @param viewType Item view type.
     * @return A new EventViewHolder instance.
     */
    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event_card, parent, false);
        return new EventViewHolder(view);
    }

    /**
     * Binds event data to a specific ViewHolder.
     *
     * @param holder   The ViewHolder to populate.
     * @param position The position of the event in the list.
     */
    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        EventsModel event = events.get(position);
        holder.bind(event, position);
    }

    /**
     * Returns the total number of events displayed in the RecyclerView.
     *
     * @return Total number of events.
     */
    @Override
    public int getItemCount() {
        return events.size();
    }

    /**
     * ViewHolder class that represents a single event card inside the RecyclerView.
     * Handles binding event data and configuring the options menu.
     */
    class EventViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageBanner;
        private TextView textEventTitle;
        private TextView textEventDate;
        private TextView textEventAttendees;
        private ImageButton btnEventOptions;

        /**
         * Initializes UI components for the event card.
         *
         * @param itemView The root view of the event card layout.
         */
        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            imageBanner = itemView.findViewById(R.id.imageBanner);
            textEventTitle = itemView.findViewById(R.id.textEventTitle);
            textEventDate = itemView.findViewById(R.id.textEventDate);
            textEventAttendees = itemView.findViewById(R.id.textEventAttendees);
            btnEventOptions = itemView.findViewById(R.id.btnEventOptions);
        }

        /**
         * Binds event details to the UI elements in the event card, sets up click listeners,
         * loads the banner image, and configures the popup menu for edit/delete/sample actions.
         *
         * @param event    The event to display.
         * @param position The event's position in the list.
         */
        public void bind( EventsModel event, int position) {
            // Set title
            textEventTitle.setText(event.getEventTitle());

            // Set date range
            String dateRange = dateFormat.format(event.getRegistrationStart()) +
                    " - " + dateFormat.format(event.getDate());
            textEventDate.setText(dateRange);

            // Set attendees
            String attendeesText = "Attendees: " + event.getSignups() + " / " + event.getAttendees();//TODO: make the signups live
            textEventAttendees.setText(attendeesText);

            // Set banner image (you can load from URI or use placeholder)
            // For now using placeholder - implement image loading if needed
            if (event.getImageUrl() != null ) {
                Glide.with(context).load(event.getImageUrl()).into(imageBanner);
            } else {
                imageBanner.setImageResource(R.drawable.logo_loading);
            }


            // Set click listener for the entire card
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEventClick(event, position);
                }
            });

            // Set up options menu
            btnEventOptions.setOnClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                popupMenu.getMenuInflater().inflate(R.menu.menu_event_options, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(item -> {
                    int itemId = item.getItemId();
                    if (itemId == R.id.action_edit) {
                        if (listener != null) {
                            listener.onEditClick(event, position);
                        }
                        return true;
                    } else if (itemId == R.id.action_delete) {
                        if (listener != null) {
                            listener.onDeleteClick(event, position);
                        }
                        return true;
                    } else if (itemId == R.id.action_sample) {
                        if (listener != null) {
                            listener.onSampleClick(event, position);
                        }
                        return true;
                    }

                    return false;
                });

                popupMenu.show();
            });
        }
    }
}