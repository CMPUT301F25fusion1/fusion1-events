package com.example.fusion1_events;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddEventFragment extends DialogFragment {

    interface AddEventDialogListener {
        void addEvent(EventsModel eventsModel);
    }

    private AddEventDialogListener listener;
    private Button increase;
    private Button decrease;
    private Button addImage;
    private ImageView imagePreview;
    private EditText inputRegStartDate;
    private EditText inputRegEndDate;
    private EditText inputEventDate;

    private Integer peopleCount = 0;
    private Date regStartDate;
    private Date regEndDate;
    private Date eventDate;
    private Uri selectedImageUri;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

    // Image picker launcher (kept for UI preview only)
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    public static AddEventFragment newInstance(EventsModel eventsModel) {
        Bundle args = new Bundle();
        AddEventFragment fragment = new AddEventFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize image picker (kept for UI preview only)
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        if (imagePreview != null && selectedImageUri != null) {
                            imagePreview.setImageURI(selectedImageUri);
                        }
                    }
                }
        );
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof AddEventDialogListener) {
            listener = (AddEventDialogListener) context;
        } else {
            throw new RuntimeException("Must implement AddEventDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.add_event_dialog, null);

        // Initialize views
        EditText editTitle = view.findViewById(R.id.inputTitle);
        EditText editDescription = view.findViewById(R.id.inputDescription);
        TextView editPeopleCount = view.findViewById(R.id.txtPeopleCount);
        inputRegStartDate = view.findViewById(R.id.inputRegStartDate);
        inputRegEndDate = view.findViewById(R.id.inputRegEndDate);
        inputEventDate = view.findViewById(R.id.inputEventDate);
        imagePreview = view.findViewById(R.id.imagePreview);

        increase = view.findViewById(R.id.btnIncrease);
        decrease = view.findViewById(R.id.btnDecrease);
        addImage = view.findViewById(R.id.btnAddImage);

        // Set up date pickers
        setupDatePicker(inputRegStartDate, date -> regStartDate = date);
        setupDatePicker(inputRegEndDate, date -> regEndDate = date);
        setupDatePicker(inputEventDate, date -> eventDate = date);

        // Set up people count buttons
        increase.setOnClickListener(v -> {
            peopleCount++;
            editPeopleCount.setText(peopleCount.toString());
        });

        decrease.setOnClickListener(v -> {
            if (peopleCount > 0) {
                peopleCount--;
                editPeopleCount.setText(peopleCount.toString());
            }
        });

        // Set up image picker (kept for UI preview only)
        addImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        });

        // Build dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        return builder
                .setView(view)
                .setTitle("Add Event")
                .setPositiveButton("Add", (dialog, which) -> {
                    String title = editTitle.getText().toString().trim();
                    String description = editDescription.getText().toString().trim();

                    // Validate input
                    if (title.isEmpty()) {
                        Toast.makeText(getContext(), "Please enter event title", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (regStartDate == null || regEndDate == null || eventDate == null) {
                        Toast.makeText(getContext(), "Please select all dates", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // Create EventsModel (no poster field)
                    EventsModel event = new EventsModel(
                            title,
                            regStartDate,
                            regEndDate,
                            description,
                            eventDate,
                            Long.valueOf(peopleCount),
                            0L // signups starts at 0
                    );

                    // Pass event to listener (no image URI)
                    listener.addEvent(event);
                })
                .setNegativeButton("Cancel", null)
                .create();
    }


    private void setupDatePicker(EditText editText, DateSelectedListener listener) {
        editText.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    requireContext(),
                    (view, year, month, dayOfMonth) -> {
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(year, month, dayOfMonth);
                        Date date = selectedDate.getTime();

                        // Update the EditText
                        editText.setText(dateFormat.format(date));

                        // Notify listener
                        listener.onDateSelected(date);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );

            datePickerDialog.show();
        });
    }

    /**
     * Interface for date selection callback
     */
    private interface DateSelectedListener {
        void onDateSelected(Date date);
    }
}