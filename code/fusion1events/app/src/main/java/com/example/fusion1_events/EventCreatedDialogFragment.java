package com.example.fusion1_events;

import androidx.appcompat.app.AlertDialog;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * DialogFragment that displays event creation confirmation with QR code.
 * Shows event details, a QR code for the event, and either the registration deadline
 * or a list of invited attendees depending on the event state.
 */
public class EventCreatedDialogFragment extends DialogFragment {

    private static final String ARG_EVENT_TITLE = "event_title";
    private EventsModel createdEvent;
    private static final String ARG_IMAGE_URI = "image_uri";

    private UsersAdapter usersAdapter;
    private String eventId;

    /**
     * Creates a new instance of EventCreatedDialogFragment.
     *
     * @param event The event that was created or is being displayed
     * @return A new instance of EventCreatedDialogFragment
     */
    public static EventCreatedDialogFragment newInstance(EventsModel event) {
        EventCreatedDialogFragment fragment = new EventCreatedDialogFragment();

        Bundle args = new Bundle();
        args.putString("eventId", event.getEventId());
        fragment.setArguments(args);

        fragment.createdEvent = event;

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
        TextView titleText = view.findViewById(R.id.eventTitle);
        ImageView poster = view.findViewById(R.id.eventPoster);
        TextView regStart = view.findViewById(R.id.registrationStartDate);
        TextView regEnd = view.findViewById(R.id.registrationEndDate);
        TextView eventDate = view.findViewById(R.id.eventDate);
        ImageView qrCode = view.findViewById(R.id.generatedQrImage);
        Button backButton = view.findViewById(R.id.btnBack);
        TextView attendeesCount= view.findViewById(R.id.attendeesCount);
        RecyclerView attendeesList = view.findViewById(R.id.attendeesList);
        TextView listTitle = view.findViewById(R.id.listTitle);

        Button btnViewCancelled = view.findViewById(R.id.btnViewCancelled);

        btnViewCancelled.setOnClickListener(v -> {
            CancelledEntrantsDialogFragment
                    .newInstance(createdEvent.getEventId(), createdEvent.getCancelled())
                    .show(getChildFragmentManager(), "cancelled_dialog");
        });



        // Generate QR code for the event
        generateQRCode(qrCode);


        // Set event title
        titleText.setText(createdEvent.getEventTitle());

        //Set the poster
        if (createdEvent.getImageUrl() != null ) {
            Glide.with(this).load(createdEvent.getImageUrl()).into(poster);
        } else {
            poster.setImageResource(R.drawable.logo_loading);
        }



        //Set the dates
        Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String regsStart = formatter.format(createdEvent.getRegistrationStart());
        String regsEnd = formatter.format(createdEvent.getRegistrationEnd());
        String regsDate = formatter.format(createdEvent.getDate());

        regStart.setText(regsDate);
        regEnd.setText(regsEnd);
        eventDate.setText(regsDate);

        //TODO: add the ability to cancel users invites
        if (createdEvent.getInvitedList().isEmpty()){
            usersAdapter = new UsersAdapter(requireContext(),createdEvent.getWaitingList(), createdEvent.getEventId(), false);
            attendeesList.setLayoutManager(new LinearLayoutManager(requireContext()));
            attendeesList.setAdapter(usersAdapter);
            listTitle.setText("Current Waiting List:");
        } else {
            usersAdapter = new UsersAdapter(requireContext(),createdEvent.getInvitedList(), createdEvent.getEventId(), true);
            attendeesList.setLayoutManager(new LinearLayoutManager(requireContext()));
            attendeesList.setAdapter(usersAdapter);
            listTitle.setText("Invited Users:");
        }


        //Set attendees
        attendeesCount.setText(String.valueOf(createdEvent.getSignups())+" attendees registered");


        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode("TEST!", BarcodeFormat.QR_CODE, 200, 200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            qrCode.setImageBitmap(bitmap);

        } catch(WriterException e) {
            throw new RuntimeException(e);
        }

        ArrayList<String> waitListTest = createdEvent.getWaitingList();
        Date registrationDeadline = createdEvent.getRegistrationEnd();


        // Build and configure dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        AlertDialog dialog = builder
                .setView(view)
                .create();

        backButton.setOnClickListener(v -> dismiss());

        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null)
            eventId = getArguments().getString("eventId");
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
}
