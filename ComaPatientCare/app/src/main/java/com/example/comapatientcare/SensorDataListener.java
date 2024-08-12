package com.example.comapatientcare;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SensorDataListener {
    private static final String TAG = "SensorDataListener";
    private Context context;
    private DatabaseReference databaseReference;

    public SensorDataListener(Context context) {
        this.context = context;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("ComaPatient/data");
    }

    public void startListening() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "Data change detected.");
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    SensorData sensorData = snapshot.getValue(SensorData.class);
                    Log.d(TAG, "Received sensor data: " + sensorData);

                    if (sensorData != null) {
                        if ("Abnormal".equals(sensorData.tempStatus) ||
                                "Patient1: motion detected".equals(sensorData.motionStatus) ||
                                "Leg bag is full! please empty it.".equals(sensorData.bagStatus)) {
                            Log.d(TAG, "Triggering alarm broadcast.");
                            sendAlarmBroadcast();
                        } else {
                            Log.d(TAG, "No condition met for alarm.");
                        }
                    } else {
                        Log.e(TAG, "SensorData is null.");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Database error: " + databaseError.getMessage());
            }
        });
    }

    private void sendAlarmBroadcast() {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.setAction("com.example.ACTION_TRIGGER_ALARM");
        Log.d(TAG, "Sending broadcast for alarm.");
        context.sendBroadcast(intent);
    }
}
