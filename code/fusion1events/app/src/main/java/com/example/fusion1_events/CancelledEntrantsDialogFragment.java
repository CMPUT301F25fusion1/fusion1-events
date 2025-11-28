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

public class CancelledEntrantsDialogFragment extends DialogFragment {

    public static CancelledEntrantsDialogFragment newInstance(String eventId) {
        CancelledEntrantsDialogFragment fragment = new CancelledEntrantsDialogFragment();
        Bundle b = new Bundle();
        b.putString("eventId", eventId);
        fragment.setArguments(b);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.cancelled_dialog, null);

        RecyclerView recyclerView = view.findViewById(R.id.cancelledList);
        Button back = view.findViewById(R.id.btnBackCancelled);

        FirebaseFirestore.getInstance()
                .collection("Events")
                .document(getArguments().getString("eventId"))
                .get()
                .addOnSuccessListener(snapshot -> {
                    EventsModel model = snapshot.toObject(EventsModel.class);
                    UsersAdapter adapter = new UsersAdapter(
                            requireContext(),
                            model.getCancelled(),
                            model.getEventId(),
                            true
                    );
                    recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                    recyclerView.setAdapter(adapter);
                });

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(view)
                .create();

        back.setOnClickListener(v -> dismiss());
        return dialog;
    }
}

