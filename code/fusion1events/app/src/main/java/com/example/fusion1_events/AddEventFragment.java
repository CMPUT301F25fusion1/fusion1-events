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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * DialogFragment for adding a new event.
 * Provides a form interface for entering event details including title, description,
 * dates, attendee count, and an optional event image.
 */
public class AddEventFragment extends DialogFragment {

    /**
     * Interface for communicating event creation back to the host activity.
     */
    public interface AddEventDialogListener {
        /**
         * Called when a new event is created.
         *
         * @param eventsModel The event model containing event details
         * @param imageUri The URI of the selected event image, or null if no image selected
         */
        void addEvent(EventsModel eventsModel, Uri imageUri);
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

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    /**
     * Creates a new instance of AddEventFragment.
     *
     * @param eventsModel The event model (currently unused, for future expansion)
     * @return A new instance of AddEventFragment
     */
    public static AddEventFragment newInstance(EventsModel eventsModel) {
        Bundle args = new Bundle();
        AddEventFragment fragment = new AddEventFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Called when the fragment is first created.
     * Initializes the image picker launcher for selecting event images.
     *
     * @param savedInstanceState Bundle containing the fragment's previously saved state
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize image picker
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

    /**
     * Called when the fragment is attached to its host activity.
     * Verifies that the host activity implements AddEventDialogListener.
     *
     * @param context The context to which the fragment is being attached
     * @throws RuntimeException if the context does not implement AddEventDialogListener
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof AddEventDialogListener) {
            listener = (AddEventDialogListener) context;
        } else {
            throw new RuntimeException("Must implement AddEventDialogListener");
        }
    }

    /**
     * Creates and configures the dialog for adding a new event.
     * Sets up all input fields, date pickers, image selection, and validation logic.
     *
     * @param savedInstanceState Bundle containing the dialog's previously saved state
     * @return The configured AlertDialog
     */
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
        setupPeopleCountButtons(editPeopleCount);

        // Set up image selection
        setupImageSelection();

        // Build dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        return builder
                .setView(view)
                .setTitle("Add Event")
                .setPositiveButton("Add", (dialog, which) -> {
                    String title = editTitle.getText().toString().trim();
                    String description = editDescription.getText().toString().trim();

                    // Validate input
                    if (!validateInput(title)) {
                        return;
                    }

                    // Create EventsModel
                    EventsModel event = createEventModel(title, description);
                    listener.addEvent(event, selectedImageUri);
                })
                .setNegativeButton("Cancel", null)
                .create();
    }

    /**
     * Sets up the increase and decrease buttons for attendee count.
     *
     * @param editPeopleCount The TextView displaying the current count
     */
    private void setupPeopleCountButtons(TextView editPeopleCount) {
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
    }

    /**
     * Sets up the image selection button and launches the image picker.
     */
    private void setupImageSelection() {
        addImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        });
    }

    /**
     * Validates the event input fields.
     *
     * @param title The event title to validate
     * @return true if validation passes, false otherwise
     */
    private boolean validateInput(String title) {
        if (title.isEmpty()) {
            Toast.makeText(getContext(), "Please enter event title", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (regStartDate == null || regEndDate == null || eventDate == null) {
            Toast.makeText(getContext(), "Please select all dates", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * Creates an EventsModel from the input form data.
     *
     * @param title The event title
     * @param description The event description
     * @return A new EventsModel with the provided data
     */
    private EventsModel createEventModel(String title, String description) {
        return new EventsModel(
                title,
                regStartDate,
                regEndDate,
                description,
                eventDate,
                Long.valueOf(peopleCount),
                0L, // signups starts at 0
                new ArrayList<>(), // Empty waiting list for new events
                null, // imageUrl will be set after upload
                null,
                null
        );
    }

    /**
     * Sets up a date picker for an EditText field.
     * When the EditText is clicked, displays a DatePickerDialog and formats the selected date.
     *
     * @param editText The EditText to attach the date picker to
     * @param listener The listener to be notified when a date is selected
     */
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
     * Internal interface for date selection callbacks.
     */
    private interface DateSelectedListener {
        /**
         * Called when a date is selected from the DatePickerDialog.
         *
         * @param date The selected date
         */
        void onDateSelected(Date date);
    }
}