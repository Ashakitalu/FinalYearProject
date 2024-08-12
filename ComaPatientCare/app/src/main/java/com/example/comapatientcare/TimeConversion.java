package com.example.comapatientcare;

import java.util.Calendar;

public class TimeConversion {

    // Converts hours and minutes into milliseconds
    public static long[] convertToEpochMillis(int hours, int minutes) {
        Calendar calendar = Calendar.getInstance();

        // Get current time in milliseconds
        long endMillis = calendar.getTimeInMillis();

        // Subtract the input hours and minutes to get the start time
        calendar.add(Calendar.HOUR_OF_DAY, -hours);
        calendar.add(Calendar.MINUTE, -minutes);
        long startMillis = calendar.getTimeInMillis();

        return new long[]{startMillis, endMillis};
    }
}
