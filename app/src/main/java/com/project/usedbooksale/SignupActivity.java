package com.project.usedbooksale;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private final String TAG = "SignupActivity";
    private EditText etFirstName;
    private EditText etLastName;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // from https://www.geeksforgeeks.org/how-to-change-the-color-of-status-bar-in-an-android-app/
        this.getWindow().setStatusBarColor(getResources().getColor(R.color.dark_blue, getTheme()));

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseFirestore.getInstance();

        etFirstName = findViewById(R.id.et_first_name);
        etLastName = findViewById(R.id.et_last_name);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
    }

    public void onClickSignup(View view) {
        String firstName = etFirstName.getText().toString();
        String lastName = etLastName.getText().toString();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        if(validateUserInfo(etFirstName, "Enter first name")) return;
        if(validateUserInfo(etLastName, "Enter last name")) return;
        if(validateUserInfo(etEmail, "Enter email")) return;
        if(validateUserInfo(etPassword, "Enter password")) return;

        if (confirmPassword.isEmpty() || !password.equals(confirmPassword)) {
            Snackbar.make(etConfirmPassword, "Passwords do not match", Snackbar.LENGTH_SHORT)
                    .setAnchorView(findViewById(R.id.btn_signup))
                    .show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign up success, update UI with the signed-in user's information
                        Toast.makeText(getApplicationContext(), "Create User With Email : success\nPlease login",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        // If sign up fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                    }
                });

        CollectionReference users = mDatabase.collection("users");

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("Email", email);
        userInfo.put("FirstName", firstName);
        userInfo.put("LastName", lastName);

        users.document(email).set(userInfo);
        Toast.makeText(getApplicationContext(), "Account created successfully", Toast.LENGTH_SHORT).show();

        finish();
    }

    private boolean validateUserInfo(EditText editText, String message) {
        if (editText.getText().toString().isEmpty()) {
            Snackbar.make(editText, message, Snackbar.LENGTH_SHORT)
                    .setAnchorView(findViewById(R.id.btn_signup))
                    .show();
            return true;
        }
        return false;
    }
}