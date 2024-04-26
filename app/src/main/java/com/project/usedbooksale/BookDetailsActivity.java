package com.project.usedbooksale;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BookDetailsActivity extends AppCompatActivity {

    private String timeInMilliSec;
    private String email;
    private TextInputEditText titleTextView;
    private TextInputEditText sellerTextView;
    private TextInputEditText dateTextView;
    private TextInputEditText priceTextView;
    private TextInputEditText descriptionTextView;
    private TextInputEditText categoryTextView;
    private FirebaseFirestore database;
    private Button saveButton;
    private Button editButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        titleTextView = findViewById(R.id.title_text_view);
        sellerTextView = findViewById(R.id.seller_name_text_view);
        dateTextView = findViewById(R.id.date_text_view);
        priceTextView = findViewById(R.id.price_text_view);
        descriptionTextView = findViewById(R.id.desc_text_view);
        categoryTextView = findViewById(R.id.category_text_view);

        database = FirebaseFirestore.getInstance();

        updateDisplay();
    }

    private void updateDisplay() {

        TextView emailTextView = findViewById(R.id.email_text_view);

        Button removeListingButton = findViewById(R.id.remove_listing_button_book_details);
        saveButton = findViewById(R.id.save_button_book_details);
        editButton = findViewById(R.id.edit_button_book_details);

        setTitle("Book Details");

        // get the intent
        Intent intent = getIntent();

        // get data from the intent
        String date = intent.getStringExtra("date");
        String title = intent.getStringExtra("title");
        String description = intent.getStringExtra("desc");
        String price = intent.getStringExtra("price");
        String seller = intent.getStringExtra("name");
        String category = intent.getStringExtra("category");
        email = intent.getStringExtra("email");
        timeInMilliSec = intent.getStringExtra("timeInMilliSec");

        titleTextView.setText(title);
        dateTextView.setText(date);
        descriptionTextView.setText(description);
        priceTextView.setText(price);
        sellerTextView.setText(seller);
        categoryTextView.setText(category);
        emailTextView.setText(email);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (Objects.equals(auth.getCurrentUser().getEmail(), email)) {
            removeListingButton.setVisibility(View.VISIBLE);
            saveButton.setVisibility(View.VISIBLE);
            editButton.setVisibility(View.VISIBLE);
        } else {
            removeListingButton.setVisibility(View.GONE);
            saveButton.setVisibility(View.GONE);
            editButton.setVisibility(View.GONE);
        }
    }

    public void onClickEditBookDetails(View view) {
        TextInputEditText title = findViewById(R.id.title_text_view);
        Button saveButton = findViewById(R.id.save_button_book_details);
        Button editButton = findViewById(R.id.edit_button_book_details);
        Button exitButton = findViewById(R.id.exit_button_book_details);

        if (!title.isClickable()) {
            setEditable(true);
            saveButton.setEnabled(true);
            editButton.setVisibility(View.GONE);
            exitButton.setVisibility(View.VISIBLE);
        } else {
            setEditable(false);
            saveButton.setEnabled(false);
            editButton.setVisibility(View.VISIBLE);
            exitButton.setVisibility(View.GONE);
        }
        updateDisplay();
    }

    public void onClickExitBookDetails(View view) {
        Button saveButton = findViewById(R.id.save_button_book_details);
        Button editButton = findViewById(R.id.edit_button_book_details);
        Button exitButton = findViewById(R.id.exit_button_book_details);

        setEditable(false);
        saveButton.setEnabled(false);
        editButton.setVisibility(View.VISIBLE);
        exitButton.setVisibility(View.GONE);

        updateDisplay();
    }

    public void onClickSaveBookDetails(View view) {

        CollectionReference books = database.collection("books_on_sale");

        Map<String, Object> bookInfo = new HashMap<>();
        bookInfo.put("Title", String.valueOf(titleTextView.getText()));
        bookInfo.put("Price", String.valueOf(priceTextView.getText()));
        bookInfo.put("Description", String.valueOf(descriptionTextView.getText()));

        books.document(timeInMilliSec + email).update(bookInfo);

        Button exitButton = findViewById(R.id.exit_button_book_details);

        setEditable(false);
        saveButton.setEnabled(false);
        editButton.setVisibility(View.VISIBLE);
        exitButton.setVisibility(View.GONE);
    }

    public void onClickRemoveListingBookDetails(View view) {
        Intent removeListingIntent = new Intent(getApplicationContext(), RemoveListingActivity.class);

        String documentPath = timeInMilliSec + email;
        removeListingIntent.putExtra("documentPath", documentPath);

        startActivity(removeListingIntent);
        finish();
    }

    private void setEditable(boolean editable) {
        titleTextView.setFocusableInTouchMode(editable);
        titleTextView.setClickable(editable);
        titleTextView.clearFocus();
        titleTextView.clearComposingText();

        priceTextView.setFocusableInTouchMode(editable);
        priceTextView.setClickable(editable);
        priceTextView.clearFocus();
        priceTextView.clearComposingText();

        descriptionTextView.setFocusableInTouchMode(editable);
        descriptionTextView.setClickable(editable);
        descriptionTextView.clearFocus();
        descriptionTextView.clearComposingText();

        sellerTextView.setEnabled(!editable);
        dateTextView.setEnabled(!editable);
    }
}