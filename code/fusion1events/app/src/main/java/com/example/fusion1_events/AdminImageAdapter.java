package com.example.fusion1_events;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class AdminImageAdapter extends RecyclerView.Adapter<AdminImageAdapter.ViewHolder> {

    private final Context context;
    private final List<EventItem> eventList;

    public AdminImageAdapter(Context context, List<EventItem> eventList) {
        this.context = context;
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EventItem event = eventList.get(position);

        //holder.textOrganizerName.setText(event.getOrganizerName() != null ? event.getOrganizerName() : "Unknown Organizer");
        holder.textEventDate.setText(event.getDate() != null ? event.getDate() : "No date");

        Glide.with(context)
                .load(event.getImageUrl())
                .placeholder(R.drawable.placeholder_image) // shows while loading
                .error(R.drawable.placeholder_image)       // shows if load fails
                .into(holder.adminImageView);

        holder.buttonRemove.setOnClickListener(v ->
                Toast.makeText(context, "Remove feature coming soon!", Toast.LENGTH_SHORT).show()
        );
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView adminImageView;
        TextView textOrganizerName, textEventDate;
        Button buttonRemove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            adminImageView = itemView.findViewById(R.id.adminImageView);
            //textOrganizerName = itemView.findViewById(R.id.textOrganizerName);
            textEventDate = itemView.findViewById(R.id.textEventDate);
            buttonRemove = itemView.findViewById(R.id.buttonRemove);
        }
    }
}
