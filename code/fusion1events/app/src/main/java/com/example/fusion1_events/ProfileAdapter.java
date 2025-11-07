package com.example.fusion1_events;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Adapter for displaying a list of admin profiles in a RecyclerView.
 * Provides buttons for deleting or viewing a profile's details.
 */
public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder> {
    /**
     * Listener interface for profile actions.
     */
    public interface onProfileActionListener {
        /**
         * Called when a profile should be deleted.
         * @param profile the profile to delete
         */
        void onDeleteProfile(AdminProfile profile);

        /**
         * Called when a profile's details should be viewed.
         * @param profile the profile to view
         */
        void onViewProfile(AdminProfile profile);
    }
    private List<AdminProfile> profiles;
    private onProfileActionListener listener;

    /**
     * Constructor for ProfileAdapter.
     * @param profiles list of admin profiles to display
     * @param listener listener for handling profile actions
     */
    public ProfileAdapter(List<AdminProfile> profiles, onProfileActionListener listener) {
        this.profiles = profiles;
        this.listener = listener;
    }

    /**
     * Inflates the layout for each profile item.
     *
     * @param parent   the parent ViewGroup
     * @param viewType the type of view
     * @return a ViewHolder for the profile item
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_profile_admin, parent, false);
        return new ViewHolder(v);
    }

    /**
     * Binds profile data to the ViewHolder.
     *
     * @param holder   the ViewHolder to bind data to
     * @param position the position of the profile in the list
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AdminProfile profile = profiles.get(position);
        holder.name.setText(profile.getName());
        holder.role.setText(profile.getRole());

        holder.buttonDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteProfile(profile);
        });

        holder.buttonView.setOnClickListener(v -> {
            if (listener != null) listener.onViewProfile(profile);
        });
    }

    /**
     * Returns the total number of profiles.
     *
     * @return the size of the profiles list
     */
    @Override public int getItemCount() { return profiles.size(); }

    /**
     * ViewHolder class for an individual profile item.
     * <p>
     * Holds references to UI components for an profile item.
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, role;
        Button buttonDelete, buttonView;
        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvProfileName);
            role = itemView.findViewById(R.id.tvProfileRole);
            buttonDelete = itemView.findViewById(R.id.buttonDeleteProfile);
            buttonView = itemView.findViewById(R.id.buttonViewProfile);
        }
    }
}
