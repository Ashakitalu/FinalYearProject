package com.example.comapatientcare;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "patient_care.db";
    private static final int DATABASE_VERSION = 3;

    // Sensor table constants
    public static final String TABLE_SENSOR = "sensor";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_MAGNITUDE = "magnitude";
    public static final String COLUMN_MOTION_STATUS = "motion_status";
    public static final String COLUMN_TEMPERATURE = "temperature";
    public static final String COLUMN_TEMP_STATUS = "temp_status";
    public static final String COLUMN_URINE_LEVEL = "urine_level";
    public static final String COLUMN_BAG_STATUS = "bag_status";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    // Users table constants
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USER_ID = "_id";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_DOCTOR_NAME = "doctor_name";
    public static final String COLUMN_SPECIALIZATION = "specialization";
    public static final String COLUMN_CONTACT = "contact";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_SENSOR + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_MAGNITUDE + " TEXT, " +
                COLUMN_MOTION_STATUS + " TEXT, " +
                COLUMN_TEMPERATURE + " TEXT, " +
                COLUMN_TEMP_STATUS + " TEXT, " +
                COLUMN_URINE_LEVEL + " TEXT, " +
                COLUMN_BAG_STATUS + " TEXT, " +
                COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP);");  // Timestamp column

        db.execSQL("CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_EMAIL + " TEXT UNIQUE, " +
                COLUMN_PASSWORD + " TEXT, " +
                COLUMN_DOCTOR_NAME + " TEXT, " +
                COLUMN_SPECIALIZATION + " TEXT, " +
                COLUMN_CONTACT + " TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SENSOR);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // Save sensor data with timestamp
    public long saveSensorData(String magnitude, String motionStatus, String temperature, String tempStatus, String urineLevel, String bagStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_MAGNITUDE, magnitude);
        values.put(COLUMN_MOTION_STATUS, motionStatus);
        values.put(COLUMN_TEMPERATURE, temperature);
        values.put(COLUMN_TEMP_STATUS, tempStatus);
        values.put(COLUMN_URINE_LEVEL, urineLevel);
        values.put(COLUMN_BAG_STATUS, bagStatus);

        // Set the timestamp explicitly in the local timezone
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getDefault()); // Set to device's default timezone
        String currentDateAndTime = sdf.format(new Date());
        values.put(COLUMN_TIMESTAMP, currentDateAndTime);

        long id = db.insert(TABLE_SENSOR, null, values);
        db.close();
        return id;
    }

    // Add user to the database
    public long addUser(String email, String password, String doctorName, String specialization, String contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_DOCTOR_NAME, doctorName);
        values.put(COLUMN_SPECIALIZATION, specialization);
        values.put(COLUMN_CONTACT, contact);

        long id = db.insert(TABLE_USERS, null, values);
        db.close();
        return id;
    }

    // Get all sensor data, ordered by timestamp descending
    public Cursor getAllSensorData() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_SENSOR, null, null, null, null, null, COLUMN_TIMESTAMP + " DESC");
    }

    // Get sensor data within a specific time range
    public Cursor getSensorDataInRange(long startTimeMillis, long endTimeMillis) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Convert milliseconds to date string in the format 'yyyy-MM-dd HH:mm:ss'
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getDefault());

        String startTime = sdf.format(new Date(startTimeMillis));
        String endTime = sdf.format(new Date(endTimeMillis));

        String query = "SELECT * FROM " + TABLE_SENSOR +
                " WHERE " + COLUMN_TIMESTAMP + " BETWEEN ? AND ?" +
                " ORDER BY " + COLUMN_TIMESTAMP + " DESC";

        return db.rawQuery(query, new String[]{startTime, endTime});
    }

    // Authenticate user credentials
    public boolean authenticateUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " +
                COLUMN_EMAIL + "=? AND " + COLUMN_PASSWORD + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{email, password});
        boolean isAuthenticated = (cursor.getCount() > 0);
        cursor.close();
        return isAuthenticated;
    }

    public SensorData getLatestSensorData() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_SENSOR, null, null, null, null, null, COLUMN_TIMESTAMP + " DESC", "1");

        if (cursor != null && cursor.moveToFirst()) {
            SensorData sensorData = new SensorData();

            int magnitudeIndex = cursor.getColumnIndex(COLUMN_MAGNITUDE);
            int motionStatusIndex = cursor.getColumnIndex(COLUMN_MOTION_STATUS);
            int temperatureIndex = cursor.getColumnIndex(COLUMN_TEMPERATURE);
            int tempStatusIndex = cursor.getColumnIndex(COLUMN_TEMP_STATUS);
            int urineLevelIndex = cursor.getColumnIndex(COLUMN_URINE_LEVEL);
            int bagStatusIndex = cursor.getColumnIndex(COLUMN_BAG_STATUS);

            // Check if the column indexes are valid (i.e., not -1)
            if (magnitudeIndex != -1) {
                sensorData.magnitude = cursor.getFloat(magnitudeIndex);
            }
            if (motionStatusIndex != -1) {
                sensorData.motionStatus = cursor.getString(motionStatusIndex);
            }
            if (temperatureIndex != -1) {
                sensorData.temperature = cursor.getFloat(temperatureIndex);
            }
            if (tempStatusIndex != -1) {
                sensorData.tempStatus = cursor.getString(tempStatusIndex);
            }
            if (urineLevelIndex != -1) {
                sensorData.urineLevel = cursor.getInt(urineLevelIndex);
            }
            if (bagStatusIndex != -1) {
                sensorData.bagStatus = cursor.getString(bagStatusIndex);
            }

            cursor.close();
            return sensorData;
        }

        if (cursor != null) {
            cursor.close();
        }
        return null;
    }
}
