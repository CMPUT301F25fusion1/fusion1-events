package com.example.fusion1_events;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.installations.FirebaseInstallations;

import java.util.HashMap;
import java.util.Map;

public class ProfileViewActivity extends AppCompatActivity {
    private CollectionReference profileRef, entrantsRef, organizerRef, adminRef ;
    private EditText nameProfileEditText, emailProfileEditText, numProfileEditText;

    private Button homeButton, edtButton, saveButton, cancelEdtButton;

    private String preEditName, preEditEmail, preEditNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_view);

        homeButton = findViewById(R.id.buttonGoToHome);



        edtButton = findViewById(R.id.buttonEdit);
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




    }

    private void goHomeScreen(){
        FirebaseInstallations.getInstance().getId().addOnSuccessListener(deviceId -> {

            profileRef.document(deviceId).get().addOnSuccessListener(profile -> {

                if (profile.exists()) {
                    String role = profile.getString("role");

                    if (role.equals("ENTRANT")) {
                        startActivity(new android.content.Intent(this, EntrantHomeActivity.class));
                        finish();
                    }
                    if (role.equals("ORGANIZER")) {
                        startActivity(new android.content.Intent(this, OrganizerHomeActivity.class));
                        finish();
                    }
                    if (role.equals("ADMIN")) {
                        startActivity(new android.content.Intent(this, AdminHomeActivity.class));
                        finish();
                    }
                } else {
                    startActivity(new android.content.Intent(this, SignUpActivity.class));
                    finish();
                }
            });
        });
    }


    private void editMode(boolean Mode){

        if (Mode){
            edtButton.setVisibility(View.GONE);
            saveButton.setVisibility(View.VISIBLE);
            cancelEdtButton.setVisibility(View.VISIBLE);
        }

        else {
            edtButton.setVisibility(View.VISIBLE);
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

        Toast.makeText(this, "Edit Canceled", Toast.LENGTH_SHORT).show();

    }

    private void saveEdit(){
        String postEditName = nameProfileEditText.getText().toString().trim();
        String postEditEmail = emailProfileEditText.getText().toString().trim();
        String postEditNum = numProfileEditText.getText().toString().trim();

        if(TextUtils.isEmpty(postEditName)){
            Toast.makeText(this, "Please Enter a Name", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(postEditEmail)){
            Toast.makeText(this, "Please Enter an Email Address", Toast.LENGTH_SHORT).show();
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

            profileRef.document(device_id).get().addOnSuccessListener(profile -> {

                if (profile.exists()) {
                    String role = profile.getString("role");
                }
            });

            profileRef.document(device_id).set(updateProfileData, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> {

                        profileRef.document(device_id).get().addOnSuccessListener(profile -> {

                            String role = profile.getString("role");

                            if (role.equals("ENTRANT")) {
                                entrantsRef.document(device_id).set(updateProfileData, SetOptions.merge());
                            }

                            if (role.equals("ORGANIZER")){
                                organizerRef.document(device_id).set(updateProfileData, SetOptions.merge());
                            }

                            if (role.equals("ADMIN")) {
                                adminRef.document(device_id).set(updateProfileData, SetOptions.merge());
                            }
                        });
                        Toast.makeText(this, "Edit commited Successful!!", Toast.LENGTH_SHORT).show();



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
