package com.example.comapatientcare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SignUpActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private EditText signupEmail, signupPassword, doctorName, doctorSpecialization, doctorContact;
    private Button signupButton;
    private TextView loginRedirectText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        db = new DatabaseHelper(this);
        signupEmail = findViewById(R.id.signup_email);
        signupPassword = findViewById(R.id.signup_password);
        doctorName = findViewById(R.id.doctor_name);
        doctorSpecialization = findViewById(R.id.doctor_specialization);
        doctorContact = findViewById(R.id.doctor_contact);
        signupButton = findViewById(R.id.signup_button);
        loginRedirectText = findViewById(R.id.loginRedirectText);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = signupEmail.getText().toString().trim();
                String pass = signupPassword.getText().toString().trim();
                String name = doctorName.getText().toString().trim();
                String specialization = doctorSpecialization.getText().toString().trim();
                String contact = doctorContact.getText().toString().trim();

                if (validateInput(email, pass, name, specialization, contact)) {
                    long id = db.addUser(email, pass, name, specialization, contact);
                    if (id > 0) {
                        Toast.makeText(SignUpActivity.this, "Signup Successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish(); // Close the sign-up activity so the user can't go back to it
                    } else {
                        Toast.makeText(SignUpActivity.this, "Signup Failed: User may already exist", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                finish(); // Close the sign-up activity to avoid returning to it
            }
        });
    }

    // Validate user input
    private boolean validateInput(String email, String password, String name, String specialization, String contact) {
        boolean isValid = true;

        if (email.isEmpty()) {
            signupEmail.setError("Email cannot be empty");
            isValid = false;
        }

        if (password.isEmpty()) {
            signupPassword.setError("Password cannot be empty");
            isValid = false;
        } else if (password.length() < 6) {
            signupPassword.setError("Password should be at least 6 characters");
            isValid = false;
        }

        if (name.isEmpty()) {
            doctorName.setError("Doctor's Name cannot be empty");
            isValid = false;
        }

        if (specialization.isEmpty()) {
            doctorSpecialization.setError("Specialization cannot be empty");
            isValid = false;
        }

        if (contact.isEmpty()) {
            doctorContact.setError("Contact Number cannot be empty");
            isValid = false;
        }

        return isValid;
    }
}
