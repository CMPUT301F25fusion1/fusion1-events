package com.example.fusion1_events;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {
    private ArrayList<String> users;
    private Context context;

    private FirebaseFirestore db;
    private CollectionReference entrantsRef;

    public UsersAdapter(Context context, ArrayList<String> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.attendee_item, parent, false);
        db = FirebaseFirestore.getInstance();
        entrantsRef = db.collection("Entrants");
        return new UserViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {

        String user = users.get(position) ;
        holder.bind(user, position);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {
        private TextView user;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            user = itemView.findViewById(R.id.userName);
        }
        public void bind(String userName, int position) {
            // Set attendees
            entrantsRef.document(userName).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Profile currentProfile = documentSnapshot.toObject(Profile.class);
                            if (currentProfile != null) {
                                user.setText(currentProfile.getName());
                            }
                        } else {
                            user.setText("Unknown User");
                        }
                    })
                    .addOnFailureListener(e -> {
                        user.setText("Error loading user");
                    });
        }

}}
