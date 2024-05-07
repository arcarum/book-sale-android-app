package com.project.usedbooksale;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
    private String date;
    private String title;
    private String description;
    private String price;
    private String seller;
    private String category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setTitle("Book Details");

        titleTextView = findViewById(R.id.title_text_view);
        sellerTextView = findViewById(R.id.seller_name_text_view);
        dateTextView = findViewById(R.id.date_text_view);
        priceTextView = findViewById(R.id.price_text_view);
        descriptionTextView = findViewById(R.id.desc_text_view);
        categoryTextView = findViewById(R.id.category_text_view);

        Intent intent = getIntent();

        date = intent.getStringExtra("date");
        title = intent.getStringExtra("title");
        description = intent.getStringExtra("desc");
        price = intent.getStringExtra("price");
        seller = intent.getStringExtra("name");
        category = intent.getStringExtra("category");
        email = intent.getStringExtra("email");
        timeInMilliSec = intent.getStringExtra("timeInMilliSec");

        database = FirebaseFirestore.getInstance();

        updateDisplay();

        Button removeListingButton = findViewById(R.id.remove_listing_button_book_details);
        saveButton = findViewById(R.id.save_button_book_details);
        editButton = findViewById(R.id.edit_button_book_details);

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

    private void updateDisplay() {
        TextView emailTextView = findViewById(R.id.email_text_view);
        titleTextView.setText(title);
        dateTextView.setText(date);
        descriptionTextView.setText(description);
        priceTextView.setText(price);
        sellerTextView.setText(seller);
        categoryTextView.setText(category);
        emailTextView.setText(email);
    }

    public void onClickEditBookDetails(View view) {
        Button exitButton = findViewById(R.id.exit_button_book_details);

        setEditable(true);
        saveButton.setEnabled(true);
        editButton.setVisibility(View.GONE);
        exitButton.setVisibility(View.VISIBLE);
        updateDisplay();
    }

    public void onClickExitBookDetails(View view) {
        Button exitButton = findViewById(R.id.exit_button_book_details);

        setEditable(false);
        saveButton.setEnabled(false);
        editButton.setVisibility(View.VISIBLE);
        exitButton.setVisibility(View.GONE);

        updateDisplay();
    }

    public void onClickSaveBookDetails(View view) {

        if (String.valueOf(titleTextView.getText()).isEmpty()) {
            titleTextView.setError("Enter the title");
            return;
        } else {
            titleTextView.setError(null);
        }

        if (String.valueOf(priceTextView.getText()).isEmpty()) {
            priceTextView.setError("Enter the price");
            return;
        } else {
            priceTextView.setError(null);
        }

        if (String.valueOf(descriptionTextView.getText()).isEmpty()) {
            descriptionTextView.setError("Enter the description");
            return;
        } else {
            descriptionTextView.setError(null);
        }


        CollectionReference books = database.collection("books_on_sale");

        title = String.valueOf(titleTextView.getText());
        price = String.valueOf(priceTextView.getText());
        description = String.valueOf(descriptionTextView.getText());

        Map<String, Object> bookInfo = new HashMap<>();
        bookInfo.put("Title", title);
        bookInfo.put("Price", price);
        bookInfo.put("Description", description);

        books.document(timeInMilliSec + email).update(bookInfo);

        Button exitButton = findViewById(R.id.exit_button_book_details);

        setEditable(false);
        saveButton.setEnabled(false);
        editButton.setVisibility(View.VISIBLE);
        exitButton.setVisibility(View.GONE);

        Toast.makeText(this, "New book details saved successfully.", Toast.LENGTH_SHORT).show();
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
        titleTextView.setError(null);

        priceTextView.setFocusableInTouchMode(editable);
        priceTextView.setClickable(editable);
        priceTextView.clearFocus();
        priceTextView.clearComposingText();
        priceTextView.setError(null);

        descriptionTextView.setFocusableInTouchMode(editable);
        descriptionTextView.setClickable(editable);
        descriptionTextView.clearFocus();
        descriptionTextView.clearComposingText();
        descriptionTextView.setError(null);

        categoryTextView.setEnabled(!editable);
        sellerTextView.setEnabled(!editable);
        dateTextView.setEnabled(!editable);
    }

    // from https://stackoverflow.com/questions/2197741/how-to-send-emails-from-my-android-application
    public void onEmailClick(View view) {

        // set email intent
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Interested in Purchasing " + title);

        // Get logged in user name
        database.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    String fName = (String) documentSnapshot.get("FirstName");
                    String lName = (String) documentSnapshot.get("LastName");
                    String userFullName = fName + " " + lName;

                    intent.putExtra(Intent.EXTRA_TEXT, "Dear " + seller + "," +
                            "\n\n I am interested in purchasing " + title + " from you at the cost of " + price + "." +
                            "\n\n Best Regards," +
                            "\n " + userFullName);
                    try {
                        startActivity(Intent.createChooser(intent, "Send mail..."));
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(getApplicationContext(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}