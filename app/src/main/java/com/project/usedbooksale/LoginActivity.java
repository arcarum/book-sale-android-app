package com.project.usedbooksale;

import android.content.Intent;
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
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private final String TAG = "LoginActivity";
    private EditText etEmail, etPassword;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // from https://www.geeksforgeeks.org/how-to-change-the-color-of-status-bar-in-an-android-app/
        this.getWindow().setStatusBarColor(getResources().getColor(R.color.dark_blue, getTheme()));

        setTitle("Login");

        mAuth = FirebaseAuth.getInstance();

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
    }

    public void onClickLogin(View view) {
        final String email = etEmail.getText().toString();
        final String password = etPassword.getText().toString();

        if (email.isEmpty()) {
            Snackbar.make(etEmail, "Enter an email", Snackbar.LENGTH_SHORT)
                    .setAnchorView(findViewById(R.id.btn_login))
                    .show();
            return;
        }
        if (password.isEmpty()) {
            Snackbar.make(etEmail, "Enter a password", Snackbar.LENGTH_SHORT)
                    .setAnchorView(findViewById(R.id.btn_login))
                    .show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        Intent mainActivityIntent = new Intent(getApplicationContext(), MainActivity.class);
                        mainActivityIntent.putExtra("email", user.getEmail());
                        startActivity(mainActivityIntent);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(getApplicationContext(), "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void onClickSignup(View view) {
        Intent signupActivityIntent = new Intent(getApplicationContext(), SignupActivity.class);
        startActivity(signupActivityIntent);
        clearFields();
    }

    private void clearFields() {
        etEmail.setText("");
        etPassword.setText("");
    }
}