package com.example.sleepybaby;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && 
            intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Log.d(TAG, "Device boot completed, restoring alarms");
            
            // إعادة ضبط جميع المنبهات النشطة
            AppAlarmManager alarmManager = AppAlarmManager.getInstance(context);
            alarmManager.restoreActiveAlarms(context);
        }
    }
} 