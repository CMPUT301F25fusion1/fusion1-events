package com.example.fusion1_events;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder> {
    public interface onProfileActionListener {
        void onDeleteProfile(AdminProfile profile);
        void onViewProfile(AdminProfile profile);
    }
    private List<AdminProfile> profiles;
    private onProfileActionListener listener;

    public ProfileAdapter(List<AdminProfile> profiles, onProfileActionListener listener) {
        this.profiles = profiles;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_profile_admin, parent, false);
        return new ViewHolder(v);
    }

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

    @Override public int getItemCount() { return profiles.size(); }

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
