package com.example.fusion1_events;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.Date;

/**
 * DialogFragment that displays event creation confirmation with QR code.
 * Shows event details, a QR code for the event, and either the registration deadline
 * or a list of invited attendees depending on the event state.
 */
public class EventCreatedDialogFragment extends DialogFragment {

    private static final String ARG_EVENT_TITLE = "event_title";
    private static EventsModel createdEvent;
    private static final String ARG_IMAGE_URI = "image_uri";

    /**
     * Creates a new instance of EventCreatedDialogFragment.
     *
     * @param event The event that was created or is being displayed
     * @param imageUri The URI of the event image (can be null)
     * @return A new instance of EventCreatedDialogFragment
     */
    public static EventCreatedDialogFragment newInstance(EventsModel event, Uri imageUri) {
        createdEvent = event;
        EventCreatedDialogFragment fragment = new EventCreatedDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EVENT_TITLE, event.getEventTitle());
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Creates and configures the dialog to display event creation confirmation.
     * Generates a QR code for the event and displays either the registration deadline
     * or the list of invited attendees.
     *
     * @param savedInstanceState Bundle containing the dialog's previously saved state
     * @return The configured AlertDialog
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.event_created_dialog, null);
        TextView titleText = view.findViewById(R.id.inputTitle);
        ImageView qrCode = view.findViewById(R.id.generatedQrImage);
        Button backButton = view.findViewById(R.id.btnAddImage);
        TextView attendeesText = view.findViewById(R.id.attendeesText);
        ListView attendeesList = view.findViewById(R.id.attendeesList);

        // Generate QR code for the event
        generateQRCode(qrCode);

        // Set event title
        titleText.setText("Event Created!\n" + createdEvent.getEventTitle());

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

        // Display attendees or registration deadline
        displayAttendeeInformation(attendeesText, attendeesList);
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





        // Build and configure dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        AlertDialog dialog = builder
                .setView(view)
                .create();

        backButton.setOnClickListener(v -> dismiss());

        return dialog;
    }

    /**
     * Generates a QR code for the event and displays it in the provided ImageView.
     * Currently generates a test QR code with "TEST!" text.
     *
     * @param qrCodeImageView The ImageView where the QR code will be displayed
     */
    private void generateQRCode(ImageView qrCodeImageView) {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode("TEST!", BarcodeFormat.QR_CODE, 200, 200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            qrCodeImageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Displays either the registration deadline or the list of invited attendees.
     * If the final list is empty, shows the registration deadline.
     * If the final list has attendees, displays them in a ListView.
     *
     * @param attendeesText TextView to display the header text
     * @param attendeesList ListView to display the list of attendees
     */
    private void displayAttendeeInformation(TextView attendeesText, ListView attendeesList) {
        ArrayList<String> finalListTest = createdEvent.getFinalList();
        Date registrationDeadline = createdEvent.getRegistrationEnd();

        if (finalListTest.isEmpty()) {
            attendeesText.setText("registration deadline: " + registrationDeadline);
            attendeesList.setVisibility(View.GONE);
        } else {
            attendeesText.setText("Invited attendees:");
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    getContext(),
                    android.R.layout.simple_list_item_1,
                    finalListTest
            );
            attendeesList.setAdapter(adapter);
            attendeesList.setVisibility(View.VISIBLE);
        }
    }
}