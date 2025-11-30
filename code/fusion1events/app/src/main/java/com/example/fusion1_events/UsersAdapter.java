package com.example.fusion1_events;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.google.firebase.installations.FirebaseInstallations;

import java.util.ArrayList;
import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {
    private ArrayList<String> users;
    private Context context;

    private FirebaseFirestore db;
    private CollectionReference entrantsRef;

    private String eventId;
    private boolean enableCancel;

    public UsersAdapter(Context context, ArrayList<String> users, String eventId, boolean enableCancel) {
        this.context = context;
        this.users = users;
        this.eventId = eventId;
        this.enableCancel = enableCancel;
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

        String user = users.get(position);
        holder.bind(user, position);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {
        private TextView user;
        Button cancelButton;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            user = itemView.findViewById(R.id.userName);
            cancelButton = itemView.findViewById(R.id.btnCancelUser);
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

            if (!enableCancel) {
                cancelButton.setVisibility(View.GONE);
                return;
            } else {
                cancelButton.setVisibility(View.VISIBLE);
            }

            cancelButton.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos == RecyclerView.NO_POSITION) return;

                String userToCancelId = users.get(pos);

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference eventRef = db.collection("Events").document(eventId);
                // This is the DocumentReference we want to store for this entrant
                DocumentReference entrantRefToCancel =
                        db.collection("Entrants").document(userToCancelId);

                db.runTransaction(transaction -> {
                    // Get current snapshot
                    com.google.firebase.firestore.DocumentSnapshot snapshot = transaction.get(eventRef);

                    // Raw lists from Firestore – may contain DocumentReferences or legacy Strings
                    java.util.List<Object> invitedRaw =
                            (java.util.List<Object>) snapshot.get("invitedList");
                    java.util.List<Object> cancelledRaw =
                            (java.util.List<Object>) snapshot.get("cancelled");

                    // Normalize invited → List<DocumentReference>
                    java.util.List<DocumentReference> invited = new java.util.ArrayList<>();
                    if (invitedRaw != null) {
                        for (Object o : invitedRaw) {
                            if (o instanceof DocumentReference) {
                                invited.add((DocumentReference) o);
                            } else if (o instanceof String) {   // legacy: convert String ID to DocumentReference
                                invited.add(db.collection("Entrants").document((String) o));
                            }
                        }
                    }

                    // Normalize cancelled → List<DocumentReference>
                    java.util.List<DocumentReference> cancelled = new java.util.ArrayList<>();
                    if (cancelledRaw != null) {
                        for (Object o : cancelledRaw) {
                            if (o instanceof DocumentReference) {
                                cancelled.add((DocumentReference) o);
                            } else if (o instanceof String) {   // legacy: convert String ID to DocumentReference
                                cancelled.add(db.collection("Entrants").document((String) o));
                            }
                        }
                    }

                    // Remove this entrant from invited (by ID to avoid instance mismatch)
                    invited.removeIf(ref -> ref.getId().equals(userToCancelId));

                    // Add to cancelled if not already there
                    boolean alreadyCancelled = false;
                    for (DocumentReference ref : cancelled) {
                        if (ref.getId().equals(userToCancelId)) {
                            alreadyCancelled = true;
                            break;
                        }
                    }
                    if (!alreadyCancelled) {
                        cancelled.add(entrantRefToCancel);
                    }

                    // Write updated lists back as DocumentReferences
                    transaction.update(eventRef, "invitedList", invited);
                    transaction.update(eventRef, "cancelled", cancelled);

                    return null;
                }).addOnSuccessListener(unused -> {
                    users.remove(pos);
                    notifyItemRemoved(pos);

                    FirebaseInstallations.getInstance().getId().addOnSuccessListener(organizerId -> {

                        NotificationHelperClass.sendSingleCancelledNotification(
                                context,
                                eventId,
                                organizerId,
                                entrantRefToCancel
                        );

                        DrawHelper.runDraw(eventId, organizerId, FirebaseFirestore.getInstance(), context);
                    });



                });
            });
        }
    }
}