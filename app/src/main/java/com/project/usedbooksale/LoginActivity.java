package com.project.usedbooksale;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private final String TAG = "LoginActivity";
    private TextInputLayout etEmail, etPassword;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setTitle("Login");

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            Intent mainActivityIntent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(mainActivityIntent);
            finish();
        }

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);

        progressBar = findViewById(R.id.progressBar);
    }

    public void onClickLogin(View view) {

        progressBar.setVisibility(View.VISIBLE);

        final String email = etEmail.getEditText().getText().toString();
        final String password = etPassword.getEditText().getText().toString();

        if (email.isEmpty()) {
            etEmail.setError("Enter an email");
            progressBar.setVisibility(View.INVISIBLE);
            return;
        } else {
            etEmail.setError(null);
        }

        if (password.isEmpty()) {
            etPassword.setError("Enter a password");
            progressBar.setVisibility(View.INVISIBLE);
            return;
        } else {
            etPassword.setError(null);
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success");

                    progressBar.setVisibility(View.INVISIBLE);

                    Intent mainActivityIntent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(mainActivityIntent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure");

                    etPassword.setError("Invalid password");
                    progressBar.setVisibility(View.INVISIBLE);
                });
    }

    public void onClickSignup(View view) {
        Intent signupActivityIntent = new Intent(getApplicationContext(), SignupActivity.class);
        startActivity(signupActivityIntent);
    }
}