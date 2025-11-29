package com.example.fusion1_events;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;

import java.text.DateFormat;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private Context context;
    private List<NotificationModel> items;
    private CollectionReference notifRef;
    private Profile currentUser;

    public NotificationAdapter(Context context,
                               List<NotificationModel> items,
                               CollectionReference notifRef,
                               Profile currentUser) {
        this.context = context;
        this.items = items;
        this.notifRef = notifRef;
        this.currentUser = currentUser;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        NotificationModel item = items.get(position);

        holder.title.setText(item.getNotificationTitle());
        holder.message.setText(item.getNotificationMessage());

        String meta = item.getEventName();
        if (item.getCreatedAt() != null) {
            String timeString = DateFormat.getDateTimeInstance(
                    DateFormat.SHORT, DateFormat.SHORT
            ).format(item.getCreatedAt().toDate());
            meta = meta + " • " + timeString;
        }
        holder.meta.setText(meta);

        if (item.isRead()) {
            holder.itemView.setBackgroundColor(
                    ContextCompat.getColor(context, android.R.color.white));
            holder.title.setTypeface(null, Typeface.NORMAL);
            holder.markReadButton.setVisibility(View.GONE);
        } else {
            holder.itemView.setBackgroundColor(0xFFE3F2FD); // light blue unread
            holder.title.setTypeface(null, Typeface.BOLD);
            holder.markReadButton.setVisibility(View.VISIBLE);
        }

        holder.markReadButton.setOnClickListener(v -> {
            notifRef.document(item.getNotificationId())
                    .update("read", true)
                    .addOnSuccessListener(aVoid -> {
                        item.setRead(true);
                        notifyItemChanged(holder.getAdapterPosition());
                    });
        });

        holder.goToEventButton.setOnClickListener(v -> {
            DocumentReference eventRef = item.getNotificationEventId();
            if (eventRef != null) {
                String eventId = eventRef.getId();

                Intent intent = new Intent(context, EventDetailActivity.class);
                intent.putExtra("eventId", eventId);
                intent.putExtra("currentUser", currentUser); // same as other code
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView message;
        TextView meta;
        Button goToEventButton;
        Button markReadButton;

        NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textNotificationTitle);
            message = itemView.findViewById(R.id.textNotificationMessage);
            meta = itemView.findViewById(R.id.textNotificationMeta);
            goToEventButton = itemView.findViewById(R.id.buttonGoToEvent);
            markReadButton = itemView.findViewById(R.id.buttonMarkRead);
        }
    }
}
