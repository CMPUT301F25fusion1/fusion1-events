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

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private List<Event> eventList;
    private Context context;
    private Profile currentUser;

    public EventAdapter(Context context, List<Event> eventList, Profile currentUser) {
        this.context = context;
        this.eventList = eventList;
        this.currentUser = currentUser;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);

        // Set the card contents
        holder.tvTitle.setText(event.getName());
        holder.tvDate.setText(event.getDate());
        holder.tvDescription.setText(event.getDescription());
        holder.ivImage.setImageResource(
                event.getImageResId() != 0 ? event.getImageResId() : R.drawable.ic_launcher_background
        );
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EventDetailActivity.class);
            intent.putExtra("event", event);
            intent.putExtra("currentUser", currentUser);
            context.startActivity(intent);
        });
    }
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
