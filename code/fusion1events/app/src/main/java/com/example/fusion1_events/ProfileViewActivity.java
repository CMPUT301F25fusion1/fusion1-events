package com.example.fusion1_events;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fusion1_events.admin.AdminHomeActivity;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.installations.FirebaseInstallations;

import java.util.HashMap;
import java.util.Map;

/*
 * File: ProfileViewActivity.java
 *
 * Role:
 * - Let user view their current information like name, email and phone number,
 *       they provided to the app
 * - To get user's information like name, email, phone number that the
 *      user wants to update in the database
 * - Update all of this information to a collection called Profile in firebase.
 * - As per their selected role, update the same set of data to either Entrant, Admin
 *   or Organizer collection respectively.
 * - Let's the user delete their account/profile from the app.
 *
 *
 * Issues:
 * - Assumes device is online.
 *
 */

public class ProfileViewActivity extends AppCompatActivity {
    private CollectionReference profileRef, entrantsRef, organizerRef, adminRef ;
    private EditText nameProfileEditText, emailProfileEditText, numProfileEditText;

    private Button homeButton, edtButton, saveButton, cancelEdtButton, deleteButtonProfileView;

    private String preEditName, preEditEmail, preEditNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_view);

        homeButton = findViewById(R.id.buttonGoToHome);



        edtButton = findViewById(R.id.buttonEdit);
        deleteButtonProfileView = findViewById(R.id.buttonDeleteProfileView);
        saveButton = findViewById(R.id.buttonSaveEdit);
        cancelEdtButton = findViewById(R.id.buttonCancelEdit);

        profileRef = DatabaseReferences.getProfileDatabase();
        entrantsRef = DatabaseReferences.getEntrantsDatabase();
        organizerRef = DatabaseReferences.getOrganizersDatabase();
        adminRef = DatabaseReferences.getAdminDatabase();



        nameProfileEditText = findViewById(R.id.profileEditName);
        emailProfileEditText = findViewById(R.id.profileEditEmail);
        numProfileEditText = findViewById(R.id.profileEditNumber);

        editMode(false);


        FirebaseInstallations.getInstance().getId().addOnSuccessListener(deviceId -> {

            profileRef.document(deviceId).get().addOnSuccessListener(profile -> {


                preEditName = profile.getString("name");
                preEditEmail = profile.getString("email");
                preEditNum = profile.getString("number");

                nameProfileEditText.setText(preEditName);
                emailProfileEditText.setText(preEditEmail);
                numProfileEditText.setText(preEditNum);

            });


        });

        homeButton.setOnClickListener(v -> goHomeScreen());

        edtButton.setOnClickListener(v -> editMode(true));

        cancelEdtButton.setOnClickListener(v -> cancelEdit());

        saveButton.setOnClickListener(v -> saveEdit());

        deleteButtonProfileView.setOnClickListener(v -> deleteUserProfile());




    }

    private void deleteUserProfile(){
            new AlertDialog.Builder(this).setTitle("Delete Your Profile/Account.")
                    .setMessage("Are you sure you want to delete your profile/account?")
                    .setNegativeButton("Cancel", (d,w)-> {d.dismiss();
                        deleteButtonProfileView.setEnabled(true);})
                    .setPositiveButton("DELETE",(d,w)->{
                        deleteButtonProfileView.setEnabled(false);

                        FirebaseInstallations.getInstance().getId().addOnSuccessListener(deviceId -> {

                            profileRef.document(deviceId).get().addOnSuccessListener(profile -> {

                                    String role = profile.getString("role");

                                    if (role.equals("ENTRANT")) {
                                        entrantsRef.document(deviceId).delete();
                                    }
                                    if (role.equals("ORGANIZER")) {
                                        organizerRef.document(deviceId).delete();
                                    }
                                    if (role.equals("ADMIN")) {
                                        adminRef.document(deviceId).delete();
                                    }

                                    profileRef.document(deviceId).delete()
                                            .addOnSuccessListener(v->{
                                                Toast.makeText(this, "Profile/Account deleted successfully.",
                                                        Toast.LENGTH_SHORT).show();
                                                startActivity(new android.content.Intent(this, LoadAppActivity.class));
                                                finish();
                                            })
                                            .addOnFailureListener(e ->{
                                                Toast.makeText(this, "Could not delete right now!",
                                                        Toast.LENGTH_SHORT).show();
                                                deleteButtonProfileView.setEnabled(true);
                                            });
                            });
                        });
                    }).show();
    }

    private void goHomeScreen(){
        FirebaseInstallations.getInstance().getId().addOnSuccessListener(deviceId -> {

            profileRef.document(deviceId).get().addOnSuccessListener(profile -> {

                if (profile.exists()) {
                    String role = profile.getString("role");

                    if (role.equals("ENTRANT")) {
                        startActivity(new android.content.Intent(this,
                                EntrantHomeActivity.class));
                        finish();
                    }
                    if (role.equals("ORGANIZER")) {
                        startActivity(new android.content.Intent(this,
                                OrganizerHomeActivity.class));
                        finish();
                    }
                    if (role.equals("ADMIN")) {
                        startActivity(new android.content.Intent(this,
                                AdminHomeActivity.class));
                        finish();
                    }
                } else {
                    startActivity(new android.content.Intent(this,
                            SignUpActivity.class));
                    finish();
                }
            });
        });
    }


    private void editMode(boolean Mode){

        if (Mode){
            edtButton.setVisibility(View.GONE);
            deleteButtonProfileView.setVisibility(View.GONE);
            saveButton.setVisibility(View.VISIBLE);
            cancelEdtButton.setVisibility(View.VISIBLE);
        }

        else {
            edtButton.setVisibility(View.VISIBLE);
            deleteButtonProfileView.setVisibility(View.VISIBLE);
            saveButton.setVisibility(View.GONE);
            cancelEdtButton.setVisibility(View.GONE);
        }

        nameProfileEditText.setEnabled(Mode);
        emailProfileEditText.setEnabled(Mode);
        numProfileEditText.setEnabled(Mode);
    }


    private void cancelEdit(){

        nameProfileEditText.setText(preEditName);
        emailProfileEditText.setText(preEditEmail);
        numProfileEditText.setText(preEditNum);

        editMode(false);

        Toast.makeText(this, "Edit Canceled",
                Toast.LENGTH_SHORT).show();

    }

    private void saveEdit(){
        String postEditName = nameProfileEditText.getText().toString().trim();
        String postEditEmail = emailProfileEditText.getText().toString().trim();
        String postEditNum = numProfileEditText.getText().toString().trim();

        if(TextUtils.isEmpty(postEditName)){
            Toast.makeText(this, "Please Enter a Name",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(postEditEmail)){
            Toast.makeText(this, "Please Enter an Email Address",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        saveButton.setEnabled(false);

        FirebaseInstallations.getInstance().getId().addOnSuccessListener(device_id -> {
            Map<String, Object> updateProfileData = new HashMap<>();

            updateProfileData.put("name", postEditName);
            updateProfileData   .put("email", postEditEmail);

            if (!postEditNum.isEmpty()){
                updateProfileData.put("number", postEditNum);
            }

            else {
                updateProfileData.put("number", "NA");
            }


            profileRef.document(device_id).set(updateProfileData, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> {

                        profileRef.document(device_id).get().addOnSuccessListener(profile -> {

                            String role = profile.getString("role");

                            if (role.equals("ENTRANT")) {
                                entrantsRef.document(device_id).set(updateProfileData,
                                        SetOptions.merge());
                            }

                            if (role.equals("ORGANIZER")){
                                organizerRef.document(device_id).set(updateProfileData,
                                        SetOptions.merge());
                            }

                            if (role.equals("ADMIN")) {
                                adminRef.document(device_id).set(updateProfileData,
                                        SetOptions.merge());
                            }
                        });
                        Toast.makeText(this, "Edit commited Successful!!",
                                Toast.LENGTH_SHORT).show();



                        preEditName =postEditName;
                        preEditEmail = postEditEmail;

                        if (postEditNum.isEmpty()){
                            preEditNum = "NA";
                        }

                        else {
                            preEditNum = postEditNum;
                        }

                        nameProfileEditText.setText(preEditName);
                        emailProfileEditText.setText(preEditEmail);
                        numProfileEditText.setText(preEditNum);

                        editMode(false);
                        saveButton.setEnabled(true);


                    })

                    .addOnFailureListener(e ->{
                        Toast.makeText(this, "Edit Failed", Toast.LENGTH_SHORT).show();
                        saveButton.setEnabled(true);

                    });
        });




    }
}
