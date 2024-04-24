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

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class BookDetailsActivity extends AppCompatActivity {

    private String timeInMilliSec;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        // get references to widgets
        TextView titleTextView = findViewById(R.id.title_text_view);
        TextView dateTextView = findViewById(R.id.date_text_view);
        TextView descriptionTextView = findViewById(R.id.desc_text_view);
        TextView priceTextView = findViewById(R.id.price_text_view);
        TextView sellerTextView = findViewById(R.id.seller_name_text_view);
        TextView emailTextView = findViewById(R.id.email_text_view);

        Button removeListingButton = findViewById(R.id.remove_listing_button_book_details);

        setTitle("Book Details");
        
        // get the intent
        Intent intent = getIntent();
        
        // get data from the intent
        String date = intent.getStringExtra("date");
        String title = intent.getStringExtra("title");
        String description = intent.getStringExtra("desc");
        String price = intent.getStringExtra("price");
        String seller = intent.getStringExtra("name");
        email = intent.getStringExtra("email");
        timeInMilliSec = intent.getStringExtra("timeInMilliSec");

        titleTextView.setText(title);
        dateTextView.setText(date);
        descriptionTextView.setText(description);
        priceTextView.setText(price);
        sellerTextView.setText(seller);
        emailTextView.setText(email);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (Objects.equals(auth.getCurrentUser().getEmail(), email)) {
            removeListingButton.setVisibility(View.VISIBLE);
        } else {
            removeListingButton.setVisibility(View.GONE);
        }
    }

    public void onClickRemoveListingBookDetails(View view) {
        Intent removeListingIntent = new Intent(getApplicationContext(), RemoveListingActivity.class);

        String documentPath = timeInMilliSec + email;
        removeListingIntent.putExtra("documentPath", documentPath);

        startActivity(removeListingIntent);
        finish();
    }
}