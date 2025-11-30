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

import java.util.ArrayList;

/**
 * A DialogFragment that displays a waiting list of entrants for a given event.
 * This dialog shows users who are currently waiting for an available slot,
 * using a RecyclerView to present the list. A back button is provided to close the dialog.
 */
public class WaitingEntrantsDialogFragment extends DialogFragment {

    /**
     * Creates a new instance of {@link WaitingEntrantsDialogFragment} with the
     * given event ID and list of entrants currently on the waiting list.
     *
     * @param eventId     The ID of the event associated with the waiting entrants.
     * @param waitingList The list of user identifiers who are waiting.
     * @return A configured instance of {@link WaitingEntrantsDialogFragment}.
     */
    public static WaitingEntrantsDialogFragment newInstance(String eventId, ArrayList<String> waitingList) {
        WaitingEntrantsDialogFragment fragment = new WaitingEntrantsDialogFragment();
        Bundle b = new Bundle();
        b.putString("eventId", eventId);
        b.putStringArrayList("waitingList", waitingList);
        fragment.setArguments(b);
        return fragment;
    }

    /**
     * Called to build the dialog UI. Inflates the layout, prepares the RecyclerView
     * to display the waiting list, and initializes a button to dismiss the dialog.
     *
     * @param savedInstanceState The saved state of the fragment, if any.
     * @return A dialog showing the list of entrants on the waiting list.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.waiting_dialog, null);

        RecyclerView recyclerView = view.findViewById(R.id.waitingList);
        Button back = view.findViewById(R.id.btnBackWaiting);

        ArrayList<String> waitingList = getArguments().getStringArrayList("waitingList");
        String eventId = getArguments().getString("eventId");

        UsersAdapter adapter = new UsersAdapter(
                requireContext(),
                waitingList != null ? waitingList : new ArrayList<>(),
                eventId,
                false
        );

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        back.setOnClickListener(v -> dismiss());

        return new AlertDialog.Builder(requireContext())
                .setView(view)
                .create();
    }
}

