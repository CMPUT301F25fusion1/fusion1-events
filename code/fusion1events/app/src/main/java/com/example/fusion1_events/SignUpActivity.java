package com.example.fusion1_events;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;

public class SignUpActivity extends AppCompatActivity {

    private EditText signupName, signupEmail, signupNumber;
    private Spinner signupRole;
    private Button buttonSignUP;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        signupName = findViewById(R.id.signupName);
        signupEmail = findViewById(R.id.signupEmail);
        signupNumber = findViewById(R.id.signupNumber);
        signupRole = findViewById(R.id.signupRole);
        buttonSignUP = findViewById(R.id.buttonSignUP);
        buttonSignUP.setOnClickListener(v -> {

            String name = signupName.getText().toString().trim();
            String email = signupEmail.getText().toString().trim();
            String phone = signupNumber.getText().toString().trim();
            String role = signupRole.getSelectedItem().toString().trim();
            String device_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            if(name.isEmpty() || email.isEmpty() || role.isEmpty()){
                Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            Profile user = new Profile(name, email, phone, role, device_id);

            db = FirebaseFirestore.getInstance();
            db.collection("Profile").document(device_id).set(user);
            // Go to EventActivity

            Intent intent = new Intent(SignUpActivity.this, EventActivity.class);
            intent.putExtra("currentUser", user);
            startActivity(intent);
            finish();
        });

    }

}
