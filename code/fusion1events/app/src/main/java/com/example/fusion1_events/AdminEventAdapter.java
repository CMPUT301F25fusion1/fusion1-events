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

public class AdminEventAdapter extends RecyclerView.Adapter<AdminEventAdapter.ViewHolder> {
    public interface onEventActionListener {
        void onDeleteEvent(Event event);
    }
    private List<Event> events;
    private onEventActionListener listener;

    public AdminEventAdapter(List<Event> events, onEventActionListener listener) {
        this.events = events;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event_admin, parent, false);
        return new ViewHolder(v);
    }

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

        // View Details button
        holder.buttonViewDetails.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), AdminEventDetailsActivity.class);
            intent.putExtra("eventId", event.getId());
            v.getContext().startActivity(intent);
        });
    }

    @Override public int getItemCount() { return events.size(); }

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