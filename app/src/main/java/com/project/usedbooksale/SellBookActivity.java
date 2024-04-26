package com.project.usedbooksale;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SellBookActivity extends AppCompatActivity {
    private TextInputLayout etTitleLayout;
    private TextInputLayout etPriceLayout;
    private TextInputLayout etDescriptionLayout;
    private FirebaseFirestore database;
    private String userEmail;
    private String userFullName;
    private Intent intent;
    private AutoCompleteTextView categorySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_book);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setTitle("Sell Book");

        database = FirebaseFirestore.getInstance();

        intent = getIntent();
        userEmail = intent.getStringExtra("userEmail");
        userFullName = intent.getStringExtra("userFullName");

        etTitleLayout = findViewById(R.id.et_title_layout);
        etPriceLayout = findViewById(R.id.et_price_layout);
        etDescriptionLayout = findViewById(R.id.et_desciption_layout);

        categorySpinner = findViewById(R.id.spinner_category);
        categorySpinner.setText((CharSequence) categorySpinner.getAdapter().getItem(0), false);
    }

    public void onClickSignup(View view) {
        String title = etTitleLayout.getEditText().getText().toString();
        String price = etPriceLayout.getEditText().getText().toString();
        String description = etDescriptionLayout.getEditText().getText().toString();

        if (title.isEmpty()) {
            etTitleLayout.setError("Enter the title");
            return;
        } else {
            etTitleLayout.setError(null);
        }

        if (price.isEmpty()) {
            etPriceLayout.setError("Enter the price");
            return;
        } else {
            etPriceLayout.setError(null);
        }

        if (description.isEmpty()) {
            etDescriptionLayout.setError("Enter the description");
            return;
        } else {
            etDescriptionLayout.setError(null);
        }

        // Alert Dialog from https://stackoverflow.com/questions/2115758/how-do-i-display-an-alert-dialog-on-android
        new MaterialAlertDialogBuilder(this)
                .setIcon(R.drawable.alert_info)
                .setTitle("Sell Book")
                .setMessage("Are you sure you want to sell this book?")
                .setPositiveButton("Yes", (dialog, which) -> sellBook())
                .setNegativeButton("No", null)
                .show();
    }

    private void sellBook() {
        String title = etTitleLayout.getEditText().getText().toString();
        String price = etPriceLayout.getEditText().getText().toString();
        String description = etDescriptionLayout.getEditText().getText().toString();

        CollectionReference books = database.collection("books_on_sale");

        Map<String, Object> bookInfo = new HashMap<>();
        bookInfo.put("Title", title);
        bookInfo.put("Price", price);
        bookInfo.put("Description", description);
        bookInfo.put("Name", userFullName);
        bookInfo.put("Email", userEmail);
        bookInfo.put("Category", categorySpinner.getEditableText().toString());

        long date = System.currentTimeMillis();
        bookInfo.put("Date", date);

        books.document(date + userEmail).set(bookInfo);
        Toast.makeText(getApplicationContext(), "Book sold successfully", Toast.LENGTH_SHORT).show();
        
        intent.putExtra("updateDisplay", true);
        setResult(/*request code*/ 0, intent);
        finish();
    }
}