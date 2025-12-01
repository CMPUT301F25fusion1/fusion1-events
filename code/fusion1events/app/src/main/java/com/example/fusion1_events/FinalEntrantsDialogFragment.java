package com.example.fusion1_events;

import android.app.Dialog;
import android.content.Intent;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

public class FinalEntrantsDialogFragment extends DialogFragment {

    /**
     * Creates a new instance of FinalEntrantsDialogFragment with the given event ID
     * and a list of confirmed entrant IDs.
     *
     * @param eventId   The ID of the event whose final entrants are being displayed.
     * @param confirmed A list of user IDs representing confirmed entrants.
     * @return A configured instance of FinalEntrantsDialogFragment.
     */
    public static FinalEntrantsDialogFragment newInstance(String eventId, ArrayList<String> confirmed) {
        FinalEntrantsDialogFragment fragment = new FinalEntrantsDialogFragment();
        Bundle b = new Bundle();
        b.putString("eventId", eventId);
        b.putStringArrayList("confirmed", confirmed);
        fragment.setArguments(b);
        return fragment;
    }

    private ArrayList<String> confirmedIds;
    private String eventId;

    /**
     * Creates and returns the dialog UI for displaying confirmed entrants.
     * Includes a RecyclerView listing all confirmed entrants and a button
     * for exporting the final list to CSV format.
     *
     * @param savedInstanceState Saved state, or null if none.
     * @return The constructed dialog.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.final_dialog, null);

        RecyclerView recyclerView = view.findViewById(R.id.finalList);
        Button back = view.findViewById(R.id.btnBackFinal);
        Button exportCsv = view.findViewById(R.id.btnExportCsv);

        confirmedIds = getArguments().getStringArrayList("confirmed");
        eventId = getArguments().getString("eventId");

        UsersAdapter adapter = new UsersAdapter(
                requireContext(),
                confirmedIds != null ? confirmedIds : new ArrayList<>(),
                eventId,
                false // NO cancel button on final list
        );
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(view)
                .create();

        back.setOnClickListener(v -> dismiss());

        exportCsv.setOnClickListener(v -> exportFinalListAsCsv());

        return dialog;
    }

    /**
     * Builds a CSV string representing the full list of confirmed entrants.
     * Retrieves each entrant's name from Firestore individually, then triggers
     * sharing once all data has been collected.
     *
     * The CSV includes columns for EntrantId and Name.
     */
    private void exportFinalListAsCsv() {
        if (confirmedIds == null || confirmedIds.isEmpty()) {
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        StringBuilder csvBuilder = new StringBuilder();

        csvBuilder.append("EntrantId,Name\n");

        final int[] remaining = {confirmedIds.size()};

        for (String entrantId : confirmedIds) {
            DocumentReference entrantRef = db.collection("Entrants").document(entrantId);
            entrantRef.get().addOnSuccessListener(documentSnapshot -> {
                String name = "Unknown";
                if (documentSnapshot.exists()) {
                    Profile profile = documentSnapshot.toObject(Profile.class);
                    if (profile != null && profile.getName() != null) {
                        name = profile.getName().replace(",", " "); // avoid comma issues in CSV
                    }
                }
                csvBuilder
                        .append(entrantId)
                        .append(",")
                        .append(name)
                        .append("\n");

                remaining[0]--;

                if (remaining[0] == 0) {
                    shareCsv(csvBuilder.toString());
                }
            });
        }
    }

    /**
     * Opens a system share dialog to export the generated CSV text.
     *
     * @param csvContent The CSV data to be shared.
     */
    private void shareCsv(String csvContent) {
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/csv");
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Final Entrant List");
        sendIntent.putExtra(Intent.EXTRA_TEXT, csvContent);

        startActivity(Intent.createChooser(sendIntent, "Export Final List as CSV"));
    }
}

