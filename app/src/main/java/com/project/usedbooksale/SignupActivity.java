package com.project.usedbooksale;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private EditText etFirstName;
    private EditText etLastName;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private TextView textViewError;
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
        textViewError = findViewById(R.id.textViewError);
    }

    public void onClickSignup(View view) {
        String firstName = etFirstName.getText().toString();
        String lastName = etLastName.getText().toString();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()
                || password.isEmpty() || confirmPassword.isEmpty()) {
            textViewError.setText("Please fill all the fields.");
            textViewError.setVisibility(View.VISIBLE);
            return;
        } else if (!password.equals(confirmPassword)) {
            textViewError.setText("Passwords do not match.");
            textViewError.setVisibility(View.VISIBLE);
            return;
        } else if (password.length() < 6 && confirmPassword.length() < 6) {
            textViewError.setText("Password length must be greater than 6.");
            textViewError.setVisibility(View.VISIBLE);
            return;
        } else {
            textViewError.setVisibility(View.GONE);
        }

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
                })
                .addOnFailureListener(e -> {
                    textViewError.setText("Sign up failed. Please re-enter the correct details.");
                    textViewError.setVisibility(View.VISIBLE);
                });
    }
}