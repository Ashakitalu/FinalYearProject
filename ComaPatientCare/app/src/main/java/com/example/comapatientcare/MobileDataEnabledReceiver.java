package com.example.comapatientcare;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.util.Log;

public class MobileDataEnabledReceiver extends BroadcastReceiver {
    private static final String TAG = "MobileDataEnabledReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                Network network = connectivityManager.getActiveNetwork();
                if (network != null) {
                    NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(network);
                    if (networkCapabilities != null && networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        // Mobile data is connected
                        Log.d(TAG, "Mobile data is connected. Starting SensorDataService.");
                        startSensorDataService(context);
                    }
                }
            } else {
                NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                if (networkInfo != null && networkInfo.isConnected()) {
                    // Mobile data is connected
                    Log.d(TAG, "Mobile data is connected. Starting SensorDataService.");
                    startSensorDataService(context);
                }
            }
        }
    }

    private void startSensorDataService(Context context) {
        Intent serviceIntent = new Intent(context, SensorDataService.class);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent);
        } else {
            context.startService(serviceIntent);
        }
    }
}
