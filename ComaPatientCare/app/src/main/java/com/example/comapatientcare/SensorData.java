package com.example.comapatientcare;

public class SensorData {
    public float magnitude;
    public String motionStatus;
    public float temperature;
    public String tempStatus;
    public int urineLevel;
    public String bagStatus;



    public SensorData() {
        // Default constructor required for calls to DataSnapshot.getValue(SensorData.class)
    }
}
