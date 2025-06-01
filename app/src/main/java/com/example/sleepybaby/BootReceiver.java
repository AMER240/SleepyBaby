package com.example.sleepybaby;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            if (intent.getAction() != null && 
                (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) ||
                 intent.getAction().equals("android.intent.action.QUICKBOOT_POWERON"))) {
                
                Log.d(TAG, "Device booted, restoring alarms");
                DatabaseHelper dbHelper = new DatabaseHelper(context);
                
                // Tüm çocukların alarmlarını yeniden ayarla
                for (Child child : dbHelper.getAllChildren()) {
                    if (child.getSleepHour() != -1 && child.getSleepMinute() != -1) {
                        scheduleAlarm(context, child, true);
                    }
                    if (child.getWakeHour() != -1 && child.getWakeMinute() != -1) {
                        scheduleAlarm(context, child, false);
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in onReceive: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void scheduleAlarm(Context context, Child child, boolean isSleepTime) {
        try {
            android.app.AlarmManager alarmManager = 
                (android.app.AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager == null) return;

            Intent intent = new Intent(context, AlarmReceiver.class);
            intent.setAction(isSleepTime ? 
                "com.example.sleepybaby.SLEEP_TIME" : "com.example.sleepybaby.WAKE_TIME");
            intent.putExtra("child_id", child.getId());
            intent.putExtra("CHILD_NAME", child.getName());

            java.util.Calendar calendar = java.util.Calendar.getInstance();
            if (isSleepTime) {
                calendar.set(java.util.Calendar.HOUR_OF_DAY, child.getSleepHour());
                calendar.set(java.util.Calendar.MINUTE, child.getSleepMinute());
            } else {
                calendar.set(java.util.Calendar.HOUR_OF_DAY, child.getWakeHour());
                calendar.set(java.util.Calendar.MINUTE, child.getWakeMinute());
            }
            calendar.set(java.util.Calendar.SECOND, 0);

            if (calendar.before(java.util.Calendar.getInstance())) {
                calendar.add(java.util.Calendar.DAY_OF_YEAR, 1);
            }

            android.app.PendingIntent pendingIntent = android.app.PendingIntent.getBroadcast(
                context,
                child.getId() * 2 + (isSleepTime ? 0 : 1),
                intent,
                android.app.PendingIntent.FLAG_UPDATE_CURRENT | android.app.PendingIntent.FLAG_IMMUTABLE
            );

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        android.app.AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        pendingIntent
                    );
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    android.app.AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    pendingIntent
                );
            }

            Log.d(TAG, "Restored " + (isSleepTime ? "sleep" : "wake") + 
                " time alarm for child: " + child.getName());
        } catch (Exception e) {
            Log.e(TAG, "Error scheduling alarm: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 