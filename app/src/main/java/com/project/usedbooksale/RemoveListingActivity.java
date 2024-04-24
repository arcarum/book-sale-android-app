package com.project.usedbooksale;

import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;

public class RemoveListingActivity extends AppCompatActivity {

    private FirebaseFirestore database;
    private String documentPath;
    private TextView priceTextView;
    private TextInputLayout priceEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_listing);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setTitle("Remove Listing");

        documentPath = getIntent().getStringExtra("documentPath");

        priceTextView = findViewById(R.id.remove_listing_price_text_view);
        priceEditText = findViewById(R.id.remove_listing_et_price);

        AutoCompleteTextView reasonForRemovingSpinner = findViewById(R.id.spinner);

        reasonForRemovingSpinner.setText("I sold this product");

        reasonForRemovingSpinner.setOnItemClickListener((parent, view, position, id) -> {
            if (position == 0) {
                priceTextView.setVisibility(View.VISIBLE);
                priceEditText.setVisibility(View.VISIBLE);
            } else {
                priceTextView.setVisibility(View.GONE);
                priceEditText.setVisibility(View.GONE);
            }
        });

        database = FirebaseFirestore.getInstance();
    }

    public void onClickRemoveListing(View view) {

        if (priceEditText.getVisibility() == View.VISIBLE && priceEditText.getEditText().getText().toString().isEmpty()) {
            priceEditText.setError("Enter the amount you sold the item");
            return;
        } else {
            priceEditText.setError(null);
        }

        new MaterialAlertDialogBuilder(this)
                .setIcon(R.drawable.alert_warning)
                .setTitle("Remove Listing")
                .setMessage("Are you sure you want to stop selling this book?")
                .setPositiveButton("Yes", (dialog, which) -> removeListing())
                .setNegativeButton("No", null)
                .show();
    }

    private void removeListing() {

        database.collection("books_on_sale").document(documentPath)
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