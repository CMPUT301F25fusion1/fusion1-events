package com.example.fusion1_events.admin.image;

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
import com.example.fusion1_events.R;

/**
 * RecyclerView adapter for displaying images uploaded for events in the admin interface.
 * <p>
 * Each item shows the image, event title, registration end date, and a delete button.
 * The adapter communicates user actions via the {@link onImageActionListener}.
 */
public class AdminImageAdapter extends RecyclerView.Adapter<AdminImageAdapter.ViewHolder> {
    /**
     * Listener interface for handling actions on images.
     */
    public interface onImageActionListener {
        /**
         * Called when the user clicks the delete button for an image.
         *
         * @param image the Image object to be deleted
         */
        void onDeleteImage(AdminImage image);
    }

    private List<AdminImage> images;
    private onImageActionListener listener;

    /**
     * Constructs an ImageAdapter.
     *
     * @param images   the list of images to display
     * @param listener listener to handle image actions
     */
    public AdminImageAdapter(List<AdminImage> images, onImageActionListener listener) {
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
        AdminImage image = images.get(position);

        // Load image from URL using Glide
        Glide.with(holder.imageView.getContext())
                .load(image.getImageUrl())
                .into(holder.imageView);

        holder.title.setText(image.getEventTitle());

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
        TextView title, regEnd;
        Button buttonDelete;

        ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewEvent);
            title = itemView.findViewById(R.id.tvImageTitle);
            regEnd = itemView.findViewById(R.id.tvRegEnd);
            buttonDelete = itemView.findViewById(R.id.buttonDeleteImage);
        }
    }
}
