package com.project.usedbooksale;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.usedbooksale.ui.home.HomeFragment;

public class UsedBookService extends Service {

    private String TAG = "UsedBookService";
    private FirebaseFirestore db;

    @Override
    public void onCreate() {
        Log.d(TAG, "Service created");

        db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("books_on_sale");
        collectionReference.addSnapshotListener((value, error) -> {

            if (error != null) {
                Log.w(TAG, "Listen failed.", error);
                return;
            }

            if (value != null && !value.getDocumentChanges().isEmpty()) {
                sendNotification();
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service started");
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "Service bound - not used!");
        return null;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Service destroyed");
    }

    private void sendNotification()
    {

        // create the intent for the notification
        Intent notificationIntent = new Intent(this, HomeFragment.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // create the pending intent
        int flags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, flags);

        // create the variables for the notification
        int icon = R.drawable.ic_menu_home;
        CharSequence contentTitle = getText(R.string.app_name);
        CharSequence contentText = "New Listings Available";

        NotificationChannel notificationChannel =
                new NotificationChannel("Channel_ID", "My Notifications", NotificationManager.IMPORTANCE_DEFAULT);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.createNotificationChannel(notificationChannel);


        // create the notification and set its data
        Notification notification = new NotificationCompat
                .Builder(this, "Channel_ID")
                .setSmallIcon(icon)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setChannelId("Channel_ID")
                .build();

        final int NOTIFICATION_ID = 1;
        manager.notify(NOTIFICATION_ID, notification);
    }
}