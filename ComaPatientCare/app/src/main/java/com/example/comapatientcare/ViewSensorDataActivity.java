package com.example.comapatientcare;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ViewSensorDataActivity extends AppCompatActivity {

    private static final String TAG = "ViewSensorDataActivity";
    private DatabaseHelper dbHelper;
    private EditText editTextHours;
    private EditText editTextMinutes;
    private Button buttonFetchData;
    private TextView textViewSensorData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_sensor_data);

        dbHelper = new DatabaseHelper(this);
        editTextHours = findViewById(R.id.editTextHours);
        editTextMinutes = findViewById(R.id.editTextMinutes);
        buttonFetchData = findViewById(R.id.buttonFetchData);
        textViewSensorData = findViewById(R.id.textViewSensorData);

        buttonFetchData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get number of hours and minutes from input fields
                String hoursInput = editTextHours.getText().toString().trim();
                String minutesInput = editTextMinutes.getText().toString().trim();

                Log.d(TAG, "Button clicked. Hours input: " + hoursInput + ", Minutes input: " + minutesInput);

                if (isValidInput(hoursInput) && isValidInput(minutesInput)) {
                    int hours = Integer.parseInt(hoursInput);
                    int minutes = Integer.parseInt(minutesInput);

                    Log.d(TAG, "Valid input. Hours: " + hours + ", Minutes: " + minutes);

                    // Use TimeConversion to get the time range
                    long[] timeRange = TimeConversion.convertToEpochMillis(hours, minutes);
                    long startTime = timeRange[0];
                    long endTime = timeRange[1];

                    Log.d(TAG, "Fetching data from " + startTime + " to " + endTime);
                    fetchAndDisplayData(startTime, endTime);
                } else {
                    Log.d(TAG, "Invalid input. Hours or minutes are not valid.");
                    textViewSensorData.setText("Invalid input. Please enter valid numbers.");
                }
            }
        });
    }

    private boolean isValidInput(String input) {
        try {
            int value = Integer.parseInt(input);
            return value >= 0;
        } catch (NumberFormatException e) {
            Log.d(TAG, "NumberFormatException: " + e.getMessage());
            return false;
        }
    }

    private void fetchAndDisplayData(long startTime, long endTime) {
        Cursor cursor = dbHelper.getSensorDataInRange(startTime, endTime);
        StringBuilder dataBuilder = new StringBuilder();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String magnitude = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MAGNITUDE));
                String motionStatus = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MOTION_STATUS));
                String temperature = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TEMPERATURE));
                String tempStatus = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TEMP_STATUS));
                String urineLevel = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_URINE_LEVEL));
                String bagStatus = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BAG_STATUS));
                String timestamp = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TIMESTAMP));

                dataBuilder.append("Timestamp: ").append(timestamp).append("\n")
                        .append("Magnitude: ").append(magnitude).append("\n")
                        .append("Motion Status: ").append(motionStatus).append("\n")
                        .append("Temperature: ").append(temperature).append("\n")
                        .append("Temp Status: ").append(tempStatus).append("\n")
                        .append("Urine Level: ").append(urineLevel).append("\n")
                        .append("Bag Status: ").append(bagStatus).append("\n\n");
            } while (cursor.moveToNext());

            cursor.close();
        } else {
            Log.d(TAG, "No data found for the specified time range.");
            dataBuilder.append("No data found for the specified time range.");
        }
        textViewSensorData.setText(dataBuilder.toString()); // Display data in TextView
    }
}
