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
        TextView titleTextView = findViewById(R.id.titleTextView);
        TextView dateTextView = findViewById(R.id.dateTextView);
        TextView descriptionTextView = findViewById(R.id.descriptionTextView);
        
        // get the intent
        Intent intent = getIntent();
        
        // get data from the intent
        String date = intent.getStringExtra("date");
        String title = intent.getStringExtra("title");
        String description = intent.getStringExtra("description");
        
        // display data on the widgets
        dateTextView.setText(date);
        titleTextView.setText(title);
        descriptionTextView.setText(description);
    }
}