package com.example.fusion1_events;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fusion1_events.admin.AdminHomeActivity;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.installations.FirebaseInstallations;

import java.util.HashMap;
import java.util.Map;



/**
 * File: SignUpActivity.java
 *
 * Role:<br>
 * - Collects user information such as device id, name, email, phone number, and preferred role.<br>
 * - Stores this data in the Profile collection in Firestore.<br>
 * - Depending on the selected role, also stores the same data in the Entrant, Organizer,
 *   or Admin collection.<br>
 * - Entrants also receive an allowNotification boolean field.<br>
 * - After successful sign up, navigates the user to the appropriate home screen based on role.<br>
 * <br>
 * Issues:<br>
 * - Assumes the device is online.<br>
 */

public class SignUpActivity extends AppCompatActivity {

    private EditText nameEditText, emailEditText, numEditText;
    private Spinner roleSpinner;
    private Button submitButton;

    private CollectionReference profileRef, entrantsRef, organizerRef, adminRef ;

    /**
     * <p>
     * This method initializes the UI components, obtains Firestore collection references,
     * and sets up the listener for the sign-up button. It is called when the activity is starting.
     * </p>
     *
     * @param savedInstanceState non-null
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        nameEditText = findViewById(R.id.signupName);
        emailEditText = findViewById(R.id.signupEmail);
        numEditText = findViewById(R.id.signupNumber);

        roleSpinner = findViewById(R.id.signupRole);

        submitButton = findViewById(R.id.buttonSignUP);

        profileRef = DatabaseReferences.getProfileDatabase();

        entrantsRef = DatabaseReferences.getEntrantsDatabase();
        organizerRef = DatabaseReferences.getOrganizersDatabase();
        adminRef = DatabaseReferences.getAdminDatabase();


        submitButton.setOnClickListener(v -> signUpProfile());

    }

    /**
     * <p>
     * This Method validates user input and creates a profile record in Firestore, and navigates to
     * the appropriate home activity based for selected role.
     * </p>
     */
    private void signUpProfile(){
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String number = numEditText.getText().toString().trim();

        String role = "";
        if (roleSpinner.getSelectedItem() != null){
            role = roleSpinner.getSelectedItem().toString();
        }

        if (role.equals("Select a role..")){
            role = "";
        }


        if(TextUtils.isEmpty(name)){
            Toast.makeText(this, "Please Enter a Name",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!name.matches(".*[a-zA-Z].*")) {
            Toast.makeText(this, "Name must contain letters in it.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please Enter an Email Address",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        }



        if(TextUtils.isEmpty(role)){
            Toast.makeText(this, "Please select a Role",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!TextUtils.isEmpty(number)) {
            if (!number.matches("\\d{10}")) {
                Toast.makeText(this, "Phone number must be of 10 digits", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        submitButton.setEnabled(false);

        String finalRole = role;
        FirebaseInstallations.getInstance().getId().addOnSuccessListener(device_id -> {
            Map<String, Object> profileData = new HashMap<>();

            profileData.put("name", name);
            profileData.put("email", email);

            if (!number.isEmpty()){
                profileData.put("number", number);
            }

            else {
                profileData.put("number", "NA");
            }

            profileData.put("role", finalRole);

            profileData.put("device_id", device_id);



            profileRef.document(device_id).set(profileData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Sign-Up Successful!!",
                                Toast.LENGTH_SHORT).show();
                        submitButton.setEnabled(true);

                        if (finalRole.equals("ENTRANT")) {
                            entrantsRef.document(device_id).set(profileData);


                                        entrantsRef.document(device_id)
                                                .update(
                                                        "allowNotification", true

                                                );



                            startActivity(new android.content.Intent(this,
                                    EntrantHomeActivity.class));
                            finish();
                        }

                        if (finalRole.equals("ORGANIZER")){
                            organizerRef.document(device_id).set(profileData);
                            startActivity(new android.content.Intent(this,
                                    OrganizerHomeActivity.class));
                            finish();
                        }

                        if (finalRole.equals("ADMIN")) {
                            adminRef.document(device_id).set(profileData);
                            startActivity(new android.content.Intent(this,
                                    AdminHomeActivity.class));
                            finish();
                        }

                    })

                    .addOnFailureListener(e ->{
                        Toast.makeText(this, "Sign-Up Failed",
                                Toast.LENGTH_SHORT).show();
                        submitButton.setEnabled(true);

                    });

        });




    }


}