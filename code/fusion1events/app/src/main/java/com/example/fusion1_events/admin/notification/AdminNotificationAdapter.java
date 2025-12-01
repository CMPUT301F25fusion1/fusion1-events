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

public class AdminNotificationAdapter extends RecyclerView.Adapter<AdminNotificationAdapter.ViewHolder>{
    private List<AdminNotification> notifications;

    public AdminNotificationAdapter(List<AdminNotification> notifications) {
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification_admin, parent, false);
        return new ViewHolder(view);
    }

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

        // Date + time formatting
        if (notification.getCreatedAt() != null) {
            Date date = notification.getCreatedAt().toDate();

            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd", Locale.getDefault());
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

            holder.dateText.setText(dateFormat.format(date));
            holder.timeText.setText(timeFormat.format(date));
        }

        // Handle item click → open details screen
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), AdminNotificationDetailsActivity.class);
            intent.putExtra("notificationId", notification.getId());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    /**
     * ViewHolder representing a single notification item.
     * Holds references to UI components inside item_admin_notification.xml
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleText, eventNameText, previewText, dateText, timeText;

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
