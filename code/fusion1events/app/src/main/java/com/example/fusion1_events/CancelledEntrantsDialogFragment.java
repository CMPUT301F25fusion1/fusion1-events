package com.example.fusion1_events;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * A DialogFragment that displays a list of entrants who have cancelled their
 * participation in an event. It shows the cancelled users in a RecyclerView
 * and provides a back button to dismiss the dialog.
 */

public class CancelledEntrantsDialogFragment extends DialogFragment {
    /**
     * Creates a new instance of {@link CancelledEntrantsDialogFragment} with the
     * given event ID and list of cancelled entrant IDs/names.
     *
     * @param eventId   The ID of the event associated with the cancelled entrants.
     * @param cancelled The list of cancelled entrant identifiers.
     * @return A configured instance of {@link CancelledEntrantsDialogFragment}.
     */
    public static CancelledEntrantsDialogFragment newInstance(String eventId, ArrayList<String> cancelled) {
        CancelledEntrantsDialogFragment fragment = new CancelledEntrantsDialogFragment();
        Bundle b = new Bundle();
        b.putString("eventId", eventId);
        b.putStringArrayList("cancelled", cancelled);
        fragment.setArguments(b);
        return fragment;
    }

    /**
     * Called to create the dialog UI. Inflates the view, initializes the RecyclerView
     * with a list of cancelled entrants, and sets up the back button to close the dialog.
     *
     * @param savedInstanceState The saved instance state, if any.
     * @return A fully constructed Dialog displaying cancelled entrants.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.cancelled_dialog, null);

        RecyclerView recyclerView = view.findViewById(R.id.cancelledList);
        Button back = view.findViewById(R.id.btnBackCancelled);

        ArrayList<String> cancelled = getArguments().getStringArrayList("cancelled");
        String eventId = getArguments().getString("eventId");

        UsersAdapter adapter = new UsersAdapter(
                requireContext(),
                cancelled != null ? cancelled : new ArrayList<>(),
                eventId,
                false
        );
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(view)
                .create();

        back.setOnClickListener(v -> dismiss());
        return dialog;
    }
}

