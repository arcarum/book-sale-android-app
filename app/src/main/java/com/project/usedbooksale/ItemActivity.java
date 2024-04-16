package com.project.usedbooksale;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class ItemActivity extends AppCompatActivity {

    private Intent intent;
    private FirebaseFirestore database;
    private String timeInMilliSec;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        // get references to widgets
        TextView titleTextView = findViewById(R.id.itemTitleTextView);
        TextView dateTextView = findViewById(R.id.itemDateTextView);
        TextView descriptionTextView = findViewById(R.id.itemDescTextView);
        TextView priceTextView = findViewById(R.id.itemPriceTextView);
        TextView sellerTextView = findViewById(R.id.itemSellerTextView);

        Button removeListingButton = findViewById(R.id.removeListingButton);

        setTitle("Book Details");
        
        // get the intent
        intent = getIntent();
        
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

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (Objects.equals(auth.getCurrentUser().getEmail(), email)) {
            removeListingButton.setVisibility(View.VISIBLE);
        } else {
            removeListingButton.setVisibility(View.GONE);
        }

        database = FirebaseFirestore.getInstance();
    }

    public void onClickRemoveListing(View view) {

        new MaterialAlertDialogBuilder(this)
                .setIcon(R.drawable.alert_warning)
                .setTitle("Remove Listing")
                .setMessage("Are you sure you want to stop selling this book?")
                .setPositiveButton("Yes", (dialog, which) -> removeListing())
                .setNegativeButton("No", null)
                .show();
    }

    private void removeListing() {

        database.collection("books_on_sale").document(timeInMilliSec + email)
                .delete()
                .addOnSuccessListener(unused -> Toast.makeText(getApplicationContext(),
                        "Listing successfully deleted!",
                        Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getApplicationContext(),
                        "Error deleting the listing",
                        Toast.LENGTH_SHORT).show());

        Toast.makeText(getApplicationContext(), "Listing removed successfully", Toast.LENGTH_SHORT).show();
        finish();
    }
}