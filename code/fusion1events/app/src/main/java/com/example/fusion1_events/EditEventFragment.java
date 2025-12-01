package com.example.fusion1_events;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputLayout;

import java.net.URI;
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
public class EditEventFragment extends DialogFragment {


    /**
     * Interface for communicating event creation back to the host activity.
     */
    public interface EditEventDialogListener {
        /**
         * Called when a new event is created.
         *
         * @param eventsModel The event model containing event details
         * @param imageUri The URI of the selected event image, or null if no image selected
         */
        void editEvent(int position,EventsModel event, EventsModel eventsModel, Uri imageUri);
    }

    private EditEventDialogListener listener;
    private ImageButton increase;
    private ImageButton decrease;
    private ImageButton waitIncrease;
    private ImageButton waitDecrease;
    private Button addImage;
    private ImageView imagePreview;
    private EditText inputRegStartDate;
    private EditText inputRegEndDate;
    private EditText inputEventDate;
    private LinearLayout waitingListContainer ;
    private Integer peopleCount = Math.toIntExact(eventsModel.getAttendees());
    private Integer maxListCount = Math.toIntExact(eventsModel.getMaxWaitList());
    private Date regStartDate;
    private Date regEndDate;
    private Date eventDate;
    private Uri selectedImageUri;
    private static EventsModel eventsModel;
    private final ArrayList<String> selectedTags = eventsModel.getSelectedTags();
    private static int position;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private boolean geolocationRequired;

    /**
     * Creates a new instance of AddEventFragment.
     *
     * @param pos The event model (currently unused, for future expansion)
     * @return A new instance of AddEventFragment
     */
    public static EditEventFragment newInstance(EventsModel event, int pos) {
        eventsModel = event;
        position = pos;
        Bundle args = new Bundle();
        EditEventFragment fragment = new EditEventFragment();
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
        if (eventsModel.getImageUrl() != null) {
            selectedImageUri = Uri.parse(eventsModel.getImageUrl());

        }

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
        if (context instanceof EditEventDialogListener) {
            listener = (EditEventDialogListener) context;
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
        Button btnShowWaitingList = view.findViewById(R.id.btnShowWaitingList);
        LinearLayout waitingListContainer = view.findViewById(R.id.waitingListContainer);

        // Initialize views
        EditText editTitle = view.findViewById(R.id.inputTitle);
        editTitle.setText(eventsModel.getEventTitle(), TextView.BufferType.EDITABLE);

        EditText editDescription = view.findViewById(R.id.inputDescription);
        editDescription.setText(eventsModel.getEventDescription(), TextView.BufferType.EDITABLE);

        TextView editPeopleCount = view.findViewById(R.id.txtPeopleCount);
        TextView editMaxListCount = view.findViewById(R.id.txtWaitCount);

        editPeopleCount.setText(eventsModel.getAttendees().toString());
        editMaxListCount.setText(eventsModel.getMaxWaitList().toString());

        ChipGroup chipGroupTags = view.findViewById(R.id.chipGroupTags);
        TextInputLayout selectorTags = view.findViewById(R.id.tagSelector);
        AutoCompleteTextView inputTags = view.findViewById(R.id.inputTags);


        inputRegStartDate = view.findViewById(R.id.inputRegStartDate);
        inputRegStartDate.setText(dateFormat.format(eventsModel.getRegistrationStart()));
        regStartDate = eventsModel.getRegistrationStart();

        inputRegEndDate = view.findViewById(R.id.inputRegEndDate);
        inputRegEndDate.setText(dateFormat.format(eventsModel.getRegistrationEnd()));
        regEndDate = eventsModel.getRegistrationEnd();

        inputEventDate = view.findViewById(R.id.inputEventDate);
        inputEventDate.setText(dateFormat.format(eventsModel.getDate()));
        eventDate = eventsModel.getDate();

        imagePreview = view.findViewById(R.id.imagePreview);


        increase = view.findViewById(R.id.btnIncrease);
        decrease = view.findViewById(R.id.btnDecrease);
        waitIncrease = view.findViewById(R.id.btnWaitIncrease);
        waitDecrease = view.findViewById(R.id.btnWaitDecrease);
        addImage = view.findViewById(R.id.btnAddImage);

        // Set up date pickers
        setupDatePicker(inputRegStartDate, date -> regStartDate = date);
        setupDatePicker(inputRegEndDate, date -> regEndDate = date);
        setupDatePicker(inputEventDate, date -> eventDate = date);

        if (eventsModel.getImageUrl() != null ) {
            Glide.with(this).load(eventsModel.getImageUrl()).into(imagePreview);
        } else {
            imagePreview.setImageResource(R.drawable.logo_loading);
        }

        //set the keywords
        String[] tagItems = {"Chill", "Sports", "Educational"};
        boolean[] selectedFlags = {false, false, false};

        for (int i = 0; i < selectedTags.size(); i++){
            selectedFlags[i] = true;
            Chip chip = new Chip(requireContext());
            chip.setText(selectedTags.get(i));
            chip.setCloseIconVisible(true);

            chip.setOnCloseIconClickListener(view1 -> {
                int index = selectedTags.indexOf(chip.getText());
                selectedTags.remove(chip.getText());
                selectedFlags[index] = false;  // uncheck it in the dialog
                chipGroupTags.removeView(chip);
            });

            chipGroupTags.addView(chip);
        }





        inputTags.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Select Tags");

            builder.setMultiChoiceItems(tagItems, selectedFlags, (dialog, i, isChecked) -> {
                selectedFlags[i] = isChecked;
            });

            builder.setPositiveButton("OK", (dialog, which) -> {
                chipGroupTags.removeAllViews();
                selectedTags.clear();

                for (int i = 0; i < tagItems.length; i++) {
                    if (selectedFlags[i]) {
                        selectedTags.add(tagItems[i]);
                        Chip chip = new Chip(requireContext());
                        chip.setText(tagItems[i]);
                        chip.setCloseIconVisible(true);

                        chip.setOnCloseIconClickListener(view1 -> {
                            int index = selectedTags.indexOf(chip.getText());
                            selectedTags.remove(chip.getText());
                            selectedFlags[index] = false;  // uncheck it in the dialog
                            chipGroupTags.removeView(chip);
                        });

                        chipGroupTags.addView(chip);
                    }
                }
            });

            builder.setNegativeButton("Cancel", null);

            builder.show();
        });

        // Set up people count buttons
        setupPeopleCountButtons(editPeopleCount);

        setupMaxListCountButtons(editMaxListCount);

        // Set up image selection
        setupImageSelection();

        if (maxListCount > 0) {
            waitingListContainer.setVisibility(View.VISIBLE);
        }

        // setup the waiting list
        btnShowWaitingList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (waitingListContainer.getVisibility() == View.GONE) {
                    waitingListContainer.setVisibility(View.VISIBLE);
                    btnShowWaitingList.setText("Remove Waiting List Limit");
                } else {
                    waitingListContainer.setVisibility(View.GONE);
                    btnShowWaitingList.setText("Add Waiting List Limit");
                    maxListCount = 0;
                }

            }
        });


        // Build dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        return builder
                .setView(view)
                .setTitle("Edit Event")
                .setPositiveButton("Confirm", (dialog, which) -> {
                    String title = editTitle.getText().toString().trim();
                    String description = editDescription.getText().toString().trim();

                    // Validate input
                    if (!validateInput(title)) {
                        return;
                    }

                    // Create EventsModel
                    EventsModel event = createEventModel(title, description);
                    listener.editEvent(position, eventsModel, event, selectedImageUri);
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

    private void setupMaxListCountButtons(TextView editMaxListCount) {
        waitIncrease.setOnClickListener(v -> {
            maxListCount++;
            editMaxListCount.setText(maxListCount.toString());
        });

        waitDecrease.setOnClickListener(v -> {
            if (maxListCount > 0) {
                maxListCount--;
                editMaxListCount.setText(maxListCount.toString());
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
        if (peopleCount > maxListCount && maxListCount!= 0 ){
            Toast.makeText(getContext(),"Please ensure enough spots in the waiting list", Toast.LENGTH_SHORT).show();
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
                selectedTags,
                regStartDate,
                regEndDate,
                description,
                eventDate,
                Long.valueOf(peopleCount),
                0L, // signups starts at 0
                new ArrayList<>(), // Empty waiting list for new events
                null, // imageUrl will be set after upload
                eventsModel.getEventId(),
                null,
                Long.valueOf(maxListCount),
                null,
                null,
                geolocationRequired
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