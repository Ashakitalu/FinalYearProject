package com.example.comapatientcare;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class SensorDataService extends Service {

    private static final String CHANNEL_ID = "SensorDataServiceChannel";
    private static final String TAG = "SensorDataService";
    private SensorDataListener sensorDataListener;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service created.");
        createNotificationChannel();

        sensorDataListener = new SensorDataListener(this);
        sensorDataListener.startListening();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service started.");
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Sensor Data Service")
                .setContentText("Listening for sensor data...")
                .setSmallIcon(R.drawable.ic_sensor_data)
                .build();

        startForeground(1, notification);
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Sensor Data Service Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
                Log.d(TAG, "Notification channel created.");
            } else {
                Log.e(TAG, "Failed to create notification channel.");
            }
        }
    }
}
