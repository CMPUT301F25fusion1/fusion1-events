package com.example.fusion1_events;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventViewHolder> {

    private ArrayList<EventsModel> events;
    private OnEventClickListener listener;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

    public interface OnEventClickListener {
        void onEventClick(EventsModel event, int position);
        void onEditClick(EventsModel event, int position);
        void onDeleteClick(EventsModel event, int position);
    }

    public EventsAdapter(ArrayList<EventsModel> events, OnEventClickListener listener) {
        this.events = events;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event_card, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        EventsModel event = events.get(position);
        holder.bind(event, position);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    class EventViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageBanner;
        private TextView textEventTitle;
        private TextView textEventDate;
        private TextView textEventAttendees;
        private ImageButton btnEventOptions;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            imageBanner = itemView.findViewById(R.id.imageBanner);
            textEventTitle = itemView.findViewById(R.id.textEventTitle);
            textEventDate = itemView.findViewById(R.id.textEventDate);
            textEventAttendees = itemView.findViewById(R.id.textEventAttendees);
            btnEventOptions = itemView.findViewById(R.id.btnEventOptions);
        }

        public void bind(EventsModel event, int position) {
            // Set title
            textEventTitle.setText(event.getEventTitle());

            // Set date range
            String dateRange = dateFormat.format(event.getRegistrationStart()) +
                    " - " + dateFormat.format(event.getDate());
            textEventDate.setText(dateRange);

            // Set attendees
            String attendeesText = "Attendees: " + event.getSignups() + " / " + event.getAttendees();
            textEventAttendees.setText(attendeesText);

            // Set banner image (you can load from URI or use placeholder)
            // For now using placeholder - implement image loading if needed
            imageBanner.setImageResource(R.drawable.ic_image_placeholder);

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
                    }
                    return false;
                });

                popupMenu.show();
            });
        }
    }
}