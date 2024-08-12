package com.example.comapatientcare;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class SensorDataWithStatus extends AppCompatActivity {

    private TextView textViewTemperature;
    private TextView textViewTemperatureStatus;
    private TextView textViewMotion;
    private TextView textViewMotionStatus;
    private TextView textViewUrineLevel;
    private TextView textViewBagStatus;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_data_with_status);

        // Initialize your views
        textViewTemperature = findViewById(R.id.textViewTemperature);
        textViewTemperatureStatus = findViewById(R.id.textViewTemperatureStatus);
        textViewMotion = findViewById(R.id.textViewMotion);
        textViewMotionStatus = findViewById(R.id.textViewMotionStatus);
        textViewUrineLevel = findViewById(R.id.textViewUrineLevel);
        textViewBagStatus = findViewById(R.id.textViewBagStatus);

        // Initialize DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        // Retrieve the latest sensor data from the database
        SensorData latestSensorData = databaseHelper.getLatestSensorData();  // Use the correct method name

        if (latestSensorData != null) {
            // Display the data in the corresponding TextViews
            textViewTemperature.setText(" " + latestSensorData.temperature);
            textViewTemperatureStatus.setText(" " + latestSensorData.tempStatus);
            textViewMotion.setText(" " + latestSensorData.magnitude);
            textViewMotionStatus.setText(" " + latestSensorData.motionStatus);
            textViewUrineLevel.setText(" " + latestSensorData.urineLevel);
            textViewBagStatus.setText(" " + latestSensorData.bagStatus);
        }
    }
}
