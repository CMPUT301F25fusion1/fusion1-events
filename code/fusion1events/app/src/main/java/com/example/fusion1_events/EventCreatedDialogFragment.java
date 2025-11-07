package com.example.fusion1_events;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Date;

public class EventCreatedDialogFragment extends DialogFragment {

    private static final String ARG_EVENT_TITLE = "event_title";
    private static EventsModel createdEvent ;
    private static final String ARG_IMAGE_URI = "image_uri";

    public static EventCreatedDialogFragment newInstance(EventsModel event, Uri imageUri) {
        createdEvent = event;
        EventCreatedDialogFragment fragment = new EventCreatedDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EVENT_TITLE, event.getEventTitle());
        fragment.setArguments(args);
        return fragment;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.event_created_dialog, null);
        TextView titleText = view.findViewById(R.id.inputTitle);
        ImageView qrCode = view.findViewById(R.id.generatedQrImage);
        Button backButton = view.findViewById(R.id.btnAddImage);
        TextView attendeesText = view.findViewById(R.id.attendeesText);
        ListView attendeesList = view.findViewById(R.id.attendeesList);


        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode("TEST!", BarcodeFormat.QR_CODE, 200, 200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            qrCode.setImageBitmap(bitmap);

        } catch(WriterException e) {
            throw new RuntimeException(e);
        }

        titleText.setText("Event Created!\n"+ createdEvent.getEventTitle());
        titleText.setTextSize(20);
        titleText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        titleText.setPadding(0, 16, 0, 16);

        ArrayList<String> finalListTest = createdEvent.getFinalList();
        Date registrationDeadline = createdEvent.getRegistrationEnd();

        if (finalListTest.isEmpty()) {
            attendeesText.setText("registration deadline: "+registrationDeadline);
        } else {
            attendeesText.setText("Invited attendees:");
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    getContext(),
                    android.R.layout.simple_list_item_1,
                    finalListTest
            );
            attendeesList.setAdapter(adapter);

        }





        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        AlertDialog dialog = builder
                .setView(view)
                .create();

        backButton.setOnClickListener(v -> dismiss());

        return dialog;
    }
}