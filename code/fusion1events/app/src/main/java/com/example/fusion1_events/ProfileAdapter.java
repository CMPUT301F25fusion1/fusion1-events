package com.example.fusion1_events;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;

import java.util.ArrayList;
import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder> {

    private Context context;
    private List<Profile> profileList;

    public ProfileAdapter(Context context, List<Profile> profileList) {
        this.context = context;
        this.profileList = profileList;
    }

    @NonNull
    @Override
    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_profile_card, parent, false);
        return new ProfileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileViewHolder holder, int position) {
        Profile profile = profileList.get(position);

        holder.textProfileName.setText(profile.getName());
        holder.textProfileRole.setText(profile.getRole());

        // View Profile button (does nothing for now)
        holder.buttonViewProfile.setOnClickListener(v ->
                Toast.makeText(context, "View Profile: " + profile.getName(), Toast.LENGTH_SHORT).show()
        );

        // Remove button
        holder.buttonRemoveProfile.setOnClickListener(v -> removeProfile(profile, position));
    }

    private void removeProfile(Profile profile, int position) {
        String role = profile.getRole().toUpperCase();
        CollectionReference ref;

        if (role.equals("ENTRANT")) {
            ref = DatabaseReferences.getEntrantsDatabase();
        } else if (role.equals("ORGANIZER")) {
            ref = DatabaseReferences.getOrganizersDatabase();
        } else {
            Toast.makeText(context, "Unknown role for removal", Toast.LENGTH_SHORT).show();
            return;
        }

        ref.document(profile.getDevice_id()).delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Removed: " + profile.getName(), Toast.LENGTH_SHORT).show();
                    profileList.remove(position);
                    notifyItemRemoved(position);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(context, "Failed to remove " + profile.getName(), Toast.LENGTH_SHORT).show()
                );
    }

    @Override
    public int getItemCount() {
        return profileList.size();
    }

    public void updateProfiles(List<Profile> newProfiles) {
        this.profileList = newProfiles != null ? newProfiles : new ArrayList<>();
        notifyDataSetChanged();
    }

    public static class ProfileViewHolder extends RecyclerView.ViewHolder {
        TextView textProfileName, textProfileRole;
        Button buttonViewProfile, buttonRemoveProfile;

        public ProfileViewHolder(@NonNull View itemView) {
            super(itemView);
            textProfileName = itemView.findViewById(R.id.textProfileName);
            textProfileRole = itemView.findViewById(R.id.textProfileRole);
            buttonViewProfile = itemView.findViewById(R.id.buttonViewProfile);
            buttonRemoveProfile = itemView.findViewById(R.id.buttonRemoveProfile);
        }
    }
}
