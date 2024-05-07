package com.project.usedbooksale;

import android.app.Application;
import android.content.Intent;

public class UsedBookApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Intent service = new Intent(this, UsedBookService.class);
        startService(service);
    }
}
