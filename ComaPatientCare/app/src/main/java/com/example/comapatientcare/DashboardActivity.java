package com.example.comapatientcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DashboardActivity extends AppCompatActivity {

    private Button btnShowData;
    private Button btnDataStatuses; // New button for Data Statuses
    private EditText editTextBodyTemperature;
    private EditText editTextBodyMotion;
    private EditText editTextUrineLevel;
    private TextView textViewDataStatus;

    private static final String TAG = "DashboardActivity";
    private DatabaseHelper databaseHelper;
    private SensorData sensorData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize your views
        editTextBodyTemperature = findViewById(R.id.editTextBodyTemperature);
        editTextBodyMotion = findViewById(R.id.editTextBodyMotion);
        editTextUrineLevel = findViewById(R.id.editTextUrineLevel);
        textViewDataStatus = findViewById(R.id.textViewDataStatus);

        // Initialize DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        // Reference to Firebase Database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ComaPatient/data");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                sensorData = dataSnapshot.getValue(SensorData.class);
                if (sensorData != null) {
                    Log.d(TAG, "Temperature: " + sensorData.temperature);
                    Log.d(TAG, "Motion: " + sensorData.magnitude);
                    Log.d(TAG, "Urine Level: " + sensorData.urineLevel);

                    editTextBodyTemperature.setText(String.valueOf(sensorData.temperature));
                    editTextBodyMotion.setText(String.valueOf(sensorData.magnitude));
                    editTextUrineLevel.setText(String.valueOf(sensorData.urineLevel));

                    long id = databaseHelper.saveSensorData(
                            String.valueOf(sensorData.magnitude),
                            sensorData.motionStatus,
                            String.valueOf(sensorData.temperature),
                            sensorData.tempStatus,
                            String.valueOf(sensorData.urineLevel),
                            sensorData.bagStatus
                    );
                    Log.d(TAG, "Sensor data saved with ID: " + id);
                } else {
                    Log.e(TAG, "SensorData is null!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Database error: " + databaseError.getMessage());
            }
        });

        btnShowData = findViewById(R.id.btnShowData);
        btnDataStatuses = findViewById(R.id.btnDataStatuses); // Initialize new button

        btnShowData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start ViewSensorDataActivity
                Intent intent = new Intent(DashboardActivity.this, ViewSensorDataActivity.class);
                startActivity(intent);
            }
        });

        btnDataStatuses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start SensorDataWithStatusActivity
                Intent intent = new Intent(DashboardActivity.this, SensorDataWithStatus.class);
                startActivity(intent);
            }
        });
    }
}
