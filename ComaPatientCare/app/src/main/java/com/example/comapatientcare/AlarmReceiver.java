package com.example.comapatientcare;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log; // Importing Log
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import androidx.core.app.NotificationCompat;
import android.os.Build;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmReceiver"; // Define a tag for logging
    private static final String CHANNEL_ID = "AlarmChannel";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals("com.example.ACTION_TRIGGER_ALARM")) {
            Log.d(TAG, "Received alarm broadcast.");

            // Start the AlarmService
            Intent alarmServiceIntent = new Intent(context, AlarmService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(alarmServiceIntent); // For Android O and above
            } else {
                context.startService(alarmServiceIntent); // For devices below Android O
            }

            sendNotification(context);
        } else {
            Log.e(TAG, "Received invalid action: " + intent.getAction());
        }
    }

    private void sendNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create a notification channel for Android O and above
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Default Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("Alarm Triggered!")
                .setContentText("Sensor data indicates an abnormal condition.")
                .setSmallIcon(R.drawable.ic_alarm)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build();

        notificationManager.notify(1, notification);
        Log.d(TAG, "Notification sent.");
    }
}
