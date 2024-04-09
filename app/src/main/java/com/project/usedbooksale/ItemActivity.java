package com.project.usedbooksale;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class ItemActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        
        // get references to widgets
        TextView titleTextView = findViewById(R.id.itemTitleTextView);
        TextView dateTextView = findViewById(R.id.itemDateTextView);
        TextView descriptionTextView = findViewById(R.id.itemDescTextView);
        TextView priceTextView = findViewById(R.id.itemPriceTextView);
        TextView sellerTextView = findViewById(R.id.itemSellerTextView);
        
        // get the intent
        Intent intent = getIntent();
        
        // get data from the intent
        String date = intent.getStringExtra("date");
        String title = intent.getStringExtra("title");
        String description = intent.getStringExtra("desc");
        String price = intent.getStringExtra("price");
        String seller = intent.getStringExtra("name");

        titleTextView.setText(title);
        dateTextView.setText(date);
        descriptionTextView.setText(description);
        priceTextView.setText(price);
        sellerTextView.setText(seller);
    }
}