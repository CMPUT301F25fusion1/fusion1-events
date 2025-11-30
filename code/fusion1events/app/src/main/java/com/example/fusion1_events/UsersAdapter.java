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

/**
 * RecyclerView Adapter used to display a list of users (entrants) for an event.
 * Optionally provides the ability to cancel/remove a user from the event's invited list.
 */
public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {
    private ArrayList<String> users;
    private Context context;

    private FirebaseFirestore db;
    private CollectionReference entrantsRef;

    private String eventId;
    private boolean enableCancel;

    /**
     * Creates a new UsersAdapter for displaying user entries.
     *
     * @param context      The context in which the adapter is used.
     * @param users        List of user IDs to display.
     * @param eventId      The ID of the event associated with the users.
     * @param enableCancel Whether the cancel/remove action button should be shown.
     */
    public UsersAdapter(Context context, ArrayList<String> users, String eventId, boolean enableCancel) {
        this.context = context;
        this.users = users;
        this.eventId = eventId;
        this.enableCancel = enableCancel;
    }

    /**
     * Inflates and creates a new ViewHolder for user items.
     *
     * @param parent   The parent ViewGroup.
     * @param viewType The type of view (unused here).
     * @return A new {@link UserViewHolder} instance.
     */
    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.attendee_item, parent, false);
        db = FirebaseFirestore.getInstance();
        entrantsRef = db.collection("Entrants");
        return new UserViewHolder(view);

    }

    /**
     * Binds user data to the ViewHolder for a given position.
     *
     * @param holder   The ViewHolder to bind data to.
     * @param position The position of the item in the list.
     */
    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {

        String user = users.get(position);
        holder.bind(user, position);
    }

    /**
     * Returns the total number of users in the list.
     *
     * @return Number of users.
     */
    @Override
    public int getItemCount() {
        return users.size();
    }

    /**
     * ViewHolder representing a single user/entrant item in the RecyclerView.
     * Handles loading user info and performing cancellation logic when enabled.
     */
    class UserViewHolder extends RecyclerView.ViewHolder {
        private TextView user;
        Button cancelButton;

        /**
         * Constructs a new ViewHolder for an attendee item.
         *
         * @param itemView The item view layout.
         */
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            user = itemView.findViewById(R.id.userName);
            cancelButton = itemView.findViewById(R.id.btnCancelUser);
        }

        /**
         * Binds a user to this ViewHolder and sets up UI behaviors.
         * Loads the user's profile information from Firestore and optionally
         * sets up the "cancel" button to remove them from the event.
         *
         * @param userName The ID of the user to bind.
         * @param position The user's position in the list.
         */
        public void bind(String userName, int position) {

            //Load and display user profile details
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
                DocumentReference entrantRefToCancel =
                        db.collection("Entrants").document(userToCancelId);

                db.runTransaction(transaction -> {
                    com.google.firebase.firestore.DocumentSnapshot snapshot = transaction.get(eventRef);
                    java.util.List<Object> invitedRaw =
                            (java.util.List<Object>) snapshot.get("invitedList");
                    java.util.List<Object> cancelledRaw =
                            (java.util.List<Object>) snapshot.get("cancelled");

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

                    invited.removeIf(ref -> ref.getId().equals(userToCancelId));

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