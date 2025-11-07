package com.example.fusion1_events;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class EventCreatedDialogFragment extends DialogFragment {

    private static final String ARG_EVENT_TITLE = "event_title";
    //private static final String ARG_IMAGE_URI = "image_uri";
    private static final String ARG_EVENT_ID = "event_id";

    public static EventCreatedDialogFragment newInstance(String eventTitle, String eventId) {
        EventCreatedDialogFragment fragment = new EventCreatedDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EVENT_TITLE, eventTitle);
        // imageUri parameter kept for backwards compatibility but not used
        args.putString(ARG_EVENT_ID, eventId);
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

        // Get arguments
        String eventTitle = getArguments() != null ? getArguments().getString(ARG_EVENT_TITLE) : "Event Created!";
        String eventId = getArguments() != null ? getArguments().getString(ARG_EVENT_ID) : null;

        /*MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode("TEST!", BarcodeFormat.QR_CODE, 200, 200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            qrCode.setImageBitmap(bitmap);

        } catch(WriterException e) {
            throw new RuntimeException(e);
        }*/

        // Set the title
        titleText.setText(eventTitle + " Created!");
        titleText.setTextSize(20);
        titleText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        titleText.setPadding(0, 16, 0, 16);

        // Generate QR that encodes the Firestore document id (eventId)
        if (eventId != null) {
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            try {
                BitMatrix bitMatrix = multiFormatWriter.encode(eventId, BarcodeFormat.QR_CODE, 400, 400);
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                qrCode.setImageBitmap(bitmap);
            } catch (WriterException e) {
                // If QR generation fails, keep placeholder image (existing behavior)
                e.printStackTrace();
                qrCode.setImageResource(R.drawable.ic_launcher_background);
            }
        } else {
            // keep placeholder if eventId missing
            qrCode.setImageResource(R.drawable.ic_launcher_background);
        }

        // Build the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        AlertDialog dialog = builder
                .setView(view)
                .create();

        // Set up back button to dismiss dialog
        backButton.setOnClickListener(v -> dismiss());

        return dialog;
    }
}