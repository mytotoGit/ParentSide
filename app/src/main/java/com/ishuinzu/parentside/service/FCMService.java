package com.ishuinzu.parentside.service;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ishuinzu.parentside.MainActivity;
import com.ishuinzu.parentside.R;
import com.ishuinzu.parentside.app.Preferences;
import com.ishuinzu.parentside.ui.SignupActivity;

public class FCMService extends FirebaseMessagingService {
    private static final String TAG = "FCMService";
    private static final String CHANNEL_ID = "personal_notifications";
    private static final int NOTIFICATION_ID = 100;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        showNotification(remoteMessage.getData().get("body"), remoteMessage.getData().get("title"));
    }

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);
        Preferences.getInstance(FCMService.this).updateFCMToken(token);
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private void showNotification(String message, String title) {
        // Create Notification Channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Personal Notifications";
            String description = "This includes all the personal notification for the application.";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationChannel.setDescription(description);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        //Creating Pending Intent
        Intent intent = new Intent(FCMService.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("TITLE", title);
        intent.putExtra("MESSAGE", message);
        PendingIntent pendingIntent = PendingIntent.getActivity(FCMService.this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        //Creating Notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(FCMService.this, CHANNEL_ID);
        builder.setSmallIcon(R.drawable.img_logo);
        builder.setContentTitle(title);
        builder.setContentText(message);
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setAutoCancel(true);
        builder.setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(FCMService.this);
        notificationManagerCompat.notify(NOTIFICATION_ID, builder.build());
    }
}