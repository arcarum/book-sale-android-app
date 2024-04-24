package com.project.usedbooksale;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private TextInputLayout etFirstName;
    private TextInputLayout etLastName;
    private TextInputLayout etEmail;
    private TextInputLayout etPassword;
    private TextInputLayout etConfirmPassword;
    private FirebaseAuth mAuth;
    private FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setTitle("Sign up");

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();

        etFirstName = findViewById(R.id.et_first_name);
        etLastName = findViewById(R.id.et_last_name);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
    }

    public void onClickSignup(View view) {
        String firstName = etFirstName.getEditText().getText().toString();
        String lastName = etLastName.getEditText().getText().toString();
        String email = etEmail.getEditText().getText().toString();
        String password = etPassword.getEditText().getText().toString();

        if (!verifyFields()) return;

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    CollectionReference users = database.collection("users");

                    Map<String, Object> userInfo = new HashMap<>();
                    userInfo.put("Email", email);
                    userInfo.put("FirstName", firstName);
                    userInfo.put("LastName", lastName);

                    users.document(email).set(userInfo);

                    Toast.makeText(getApplicationContext(),
                            "Account created successfully\nPlease login",
                            Toast.LENGTH_SHORT).show();

                    finish();
                });
    }

    private boolean verifyFields() {
        String firstName = etFirstName.getEditText().getText().toString();
        String lastName = etLastName.getEditText().getText().toString();
        String email = etEmail.getEditText().getText().toString();
        String password = etPassword.getEditText().getText().toString();
        String confirmPassword = etConfirmPassword.getEditText().getText().toString();
        boolean isValid = true;

        if (firstName.isEmpty()) {
            etFirstName.setError("Enter first name");
            isValid = false;
        } else {
            etFirstName.setError(null);
        }

        if (lastName.isEmpty()) {
            etLastName.setError("Enter last name");
            isValid = false;
        } else {
            etLastName.setError(null);
        }

        if (email.isEmpty()) {
            etEmail.setError("Enter an email");
            isValid = false;
        } else {
            etEmail.setError(null);
        }

        if (password.isEmpty()) {
            etPassword.setError("Enter a password");
            isValid = false;
        } else if (password.length() < 6) {
            etPassword.setError("At least 6 characters required");
            isValid = false;
        } else {
            etPassword.setError(null);
        }

        if (confirmPassword.isEmpty() || !password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            isValid = false;
        } else {
            etConfirmPassword.setError(null);
        }
        return isValid;
    }
}