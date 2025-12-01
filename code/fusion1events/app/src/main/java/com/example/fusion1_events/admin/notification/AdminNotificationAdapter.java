package com.example.fusion1_events.admin.notification;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fusion1_events.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * RecyclerView Adapter for displaying a list of admin notifications.
 * <p>
 * Each item shows:
 * <ul>
 *     <li>Notification title</li>
 *     <li>Associated event name</li>
 *     <li>Message preview</li>
 *     <li>Date and time of creation</li>
 * </ul>
 * Clicking an item opens {@link AdminNotificationDetailsActivity} with full details.
 */
public class AdminNotificationAdapter extends RecyclerView.Adapter<AdminNotificationAdapter.ViewHolder>{
    private List<AdminNotification> notifications;

    /**
     * Constructor for the adapter.
     *
     * @param notifications the list of AdminNotification objects to display
     */
    public AdminNotificationAdapter(List<AdminNotification> notifications) {
        this.notifications = notifications;
    }

    /**
     * Inflates the layout for a single notification item and returns a ViewHolder.
     *
     * @param parent   the parent ViewGroup
     * @param viewType type of view (not used here)
     * @return a new ViewHolder for the inflated layout
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification_admin, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Binds the data from a notification object to the UI components in the ViewHolder.
     *
     * @param holder   the ViewHolder containing item views
     * @param position the position of the notification in the list
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AdminNotification notification = notifications.get(position);

        // Populate the UI
        holder.titleText.setText(notification.getNotificationTitle());
        String eventName = "Event: " + notification.getEventName();
        holder.eventNameText.setText(eventName);

        // Message body preview
        String body = notification.getNotificationMessage();
        if (body != null) {
            String preview = body.length() > 45 ? body.substring(0, 45) + "..." : body;
            holder.previewText.setText(preview);
        }

        // Date and time formatting
        if (notification.getCreatedAt() != null) {
            Date date = notification.getCreatedAt().toDate();

            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd", Locale.getDefault());
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

            holder.dateText.setText(dateFormat.format(date));
            holder.timeText.setText(timeFormat.format(date));
        }

        // Handle item click; open details screen
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), AdminNotificationDetailsActivity.class);
            intent.putExtra("notificationId", notification.getId());
            v.getContext().startActivity(intent);
        });
    }

    /**
     * Returns the total number of notifications in the list.
     *
     * @return the size of the notifications list
     */
    @Override
    public int getItemCount() {
        return notifications.size();
    }

    /**
     * ViewHolder representing a single notification item.
     * <p>
     * Holds references to all UI components within the notification item layout.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleText, eventNameText, previewText, dateText, timeText;

        /**
         * Constructor for the ViewHolder.
         *
         * @param itemView the root view of the notification item layout
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.textNotificationTitle);
            eventNameText = itemView.findViewById(R.id.textNotificationEventName);
            previewText = itemView.findViewById(R.id.textNotificationPreview);
            dateText = itemView.findViewById(R.id.textNotificationDate);
            timeText = itemView.findViewById(R.id.textNotificationTime);
        }
    }
}
