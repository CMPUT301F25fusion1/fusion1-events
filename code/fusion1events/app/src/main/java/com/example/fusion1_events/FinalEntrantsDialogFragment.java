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

    private void exportFinalListAsCsv() {
        if (confirmedIds == null || confirmedIds.isEmpty()) {
            // nothing to export
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        StringBuilder csvBuilder = new StringBuilder();

        // Header row - adjust columns based on your Profile fields
        csvBuilder.append("EntrantId,Name\n");

        // We'll load each profile one by one and then share when done
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

                // When all profiles are processed, share CSV
                if (remaining[0] == 0) {
                    shareCsv(csvBuilder.toString());
                }
            });
        }
    }

    private void shareCsv(String csvContent) {
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/csv");
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Final Entrant List");
        sendIntent.putExtra(Intent.EXTRA_TEXT, csvContent);

        startActivity(Intent.createChooser(sendIntent, "Export Final List as CSV"));
    }
}

