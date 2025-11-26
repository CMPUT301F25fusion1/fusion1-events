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

/**
 * RecyclerView adapter for displaying images uploaded for events in the admin interface.
 * <p>
 * Each item shows the image, event title, organizer, registration end date, and a delete button.
 */
public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    /**
     * Listener interface for handling actions on images.
     */
    public interface onImageActionListener {
        /**
         * Called when the user clicks the delete button for an image.
         *
         * @param image the Image object to be deleted
         */
        void onDeleteImage(Image image);
    }

    private List<Image> images;
    private onImageActionListener listener;

    /**
     * Constructs an ImageAdapter.
     *
     * @param images   the list of images to display
     * @param listener listener to handle image actions
     */
    public ImageAdapter(List<Image> images, onImageActionListener listener) {
        this.images = images;
        this.listener = listener;
    }

    /**
     * Inflates the layout for each image item.
     *
     * @param parent   the parent ViewGroup
     * @param viewType the type of view
     * @return a ViewHolder for the image item
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_admin, parent, false);
        return new ViewHolder(v);
    }

    /**
     * Binds image data to the ViewHolder.
     *
     * @param holder   the ViewHolder to bind data to
     * @param position the position of the image in the list
     */
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

    /**
     * Returns the total number of images.
     *
     * @return the size of the images list
     */
    @Override
    public int getItemCount() { return images.size(); }

    /**
     * ViewHolder class for an individual image item.
     * <p>
     * Holds references to UI components for an image item.
     */
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
