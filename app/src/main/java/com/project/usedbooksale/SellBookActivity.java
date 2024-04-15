package com.project.usedbooksale;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SellBookActivity extends AppCompatActivity {

    private EditText etTitle;
    private EditText etPrice;
    private EditText etDescription;
    private FirebaseFirestore database;
    private String userEmail;
    private String userFullName;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sell_book);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        this.getWindow().setStatusBarColor(getResources().getColor(R.color.dark_blue, getTheme()));
        setTitle("Sell Book");

        database = FirebaseFirestore.getInstance();

        intent = getIntent();
        userEmail = intent.getStringExtra("userEmail");
        userFullName = intent.getStringExtra("userFullName");

        etTitle = findViewById(R.id.et_title);
        etPrice = findViewById(R.id.et_price);
        etDescription = findViewById(R.id.et_desciption);
    }

    public void onClickSignup(View view) {

        // Alert Dialog from https://stackoverflow.com/questions/2115758/how-do-i-display-an-alert-dialog-on-android
        new AlertDialog.Builder(this)
                .setTitle("Sell Book")
                .setMessage("Are you sure you want to sell this book?")
                .setPositiveButton("Yes", (dialog, which) -> sellBook())
                .setNegativeButton("No", null)
                .show();
    }

    private void sellBook() {
        String title = etTitle.getText().toString();
        String price = etPrice.getText().toString();
        String description = etDescription.getText().toString();

        if(validateInfo(etTitle, "Enter the title of the book")) return;
        if(validateInfo(etPrice, "Enter the price")) return;
        if(validateInfo(etDescription, "Enter the description")) return;

        CollectionReference books = database.collection("books_on_sale");

        Map<String, Object> bookInfo = new HashMap<>();
        bookInfo.put("Title", title);
        bookInfo.put("Price", price);
        bookInfo.put("Description", description);
        bookInfo.put("Name", userFullName);
        bookInfo.put("Email", userEmail);

        long date = System.currentTimeMillis();
        bookInfo.put("Date", date);

        books.document(date + userEmail).set(bookInfo);
        Toast.makeText(getApplicationContext(), "Book sold successfully", Toast.LENGTH_SHORT).show();
        
        intent.putExtra("updateDisplay", true);
        setResult(/*request code*/ 0, intent);
        finish();
    }

    private boolean validateInfo(EditText editText, String message) {
        if (editText.getText().toString().isEmpty()) {
            Snackbar.make(editText, message, Snackbar.LENGTH_SHORT)
                    .setAnchorView(findViewById(R.id.btn_signup))
                    .show();
            return true;
        }
        return false;
    }
}