package com.project.usedbooksale;

import android.app.Application;
import android.content.Intent;

import com.google.android.material.color.DynamicColors;

public class UsedBookApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DynamicColors.applyToActivitiesIfAvailable(this);
        Intent service = new Intent(this, UsedBookService.class);
        startService(service);
    }
}
