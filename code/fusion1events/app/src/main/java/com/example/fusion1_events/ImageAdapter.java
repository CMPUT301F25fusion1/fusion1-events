package com.example.fusion1_events;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import com.bumptech.glide.Glide;


public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    public interface onImageActionListener {
        void onDeleteImage(Image image);
    }

    private List<Image> images;
    private onImageActionListener listener;

    public ImageAdapter(List<Image> images, onImageActionListener listener) {
        this.images = images;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_admin, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Image image = images.get(position);

        // Load image from URL using Glide
        Glide.with(holder.imageView.getContext())
                .load(image.getImageUrl())
                .placeholder(R.drawable.ic_admin_image_placeholder_foreground) // optional placeholder
                .into(holder.imageView);

        holder.title.setText(image.getEventTitle());
        holder.organizer.setText("Uploaded by: " + image.getOrganizer());

        SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());
        if (image.getRegistration_end() != null)
            holder.regEnd.setText("Registration Ends: " + sdf.format(image.getRegistration_end().toDate()));
        else
            holder.regEnd.setText("Registration Ends: N/A");

        holder.buttonDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteImage(image);
        });
    }

    @Override
    public int getItemCount() { return images.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView title, organizer, regEnd;
        Button buttonDelete;

        ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewEvent);
            title = itemView.findViewById(R.id.tvImageTitle);
            organizer = itemView.findViewById(R.id.tvOrganizer);
            regEnd = itemView.findViewById(R.id.tvRegEnd);
            buttonDelete = itemView.findViewById(R.id.buttonDeleteImage);
        }
    }
}
