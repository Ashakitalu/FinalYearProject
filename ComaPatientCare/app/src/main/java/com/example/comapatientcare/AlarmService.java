package com.example.comapatientcare;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class AlarmService extends Service {
    private static final String TAG = "AlarmService";
    private static final String CHANNEL_ID = "AlarmServiceChannel";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        Log.d(TAG, "AlarmService created.");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "AlarmService started.");
        playAlarmSound();

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Sensor Alarm")
                .setContentText("Abnormal sensor data detected!")
                .setSmallIcon(R.drawable.ic_alarm)
                .build();
        startForeground(1, notification);

        new Handler().postDelayed(this::stopSelf, 5000);

        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void playAlarmSound() {
        try {
            Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            if (alarmUri == null) {
                alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            }
            Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), alarmUri);

            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            int originalMode = audioManager.getRingerMode();
            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);

            if (ringtone != null) {
                ringtone.play();
                Log.d(TAG, "Ringtone started playing.");
            }

            new Handler().postDelayed(
                    () -> {
                        if (ringtone != null && ringtone.isPlaying()) {
                            ringtone.stop();
                            Log.d(TAG, "Ringtone stopped playing.");
                        }
                        audioManager.setRingerMode(originalMode);
                        Log.d(TAG, "Ringer mode restored.");
                    },
                    5000
            );
        } catch (Exception e) {
            Log.e(TAG, "Error playing alarm sound", e);
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Alarm Service Channel",
                    NotificationManager.IMPORTANCE_HIGH
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
