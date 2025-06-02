package com.example.sleepybaby;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.content.SharedPreferences;
import androidx.core.app.NotificationCompat;
import android.util.Log;

public class NotificationReceiver extends BroadcastReceiver {
    private static final String TAG = "NotificationReceiver";
    private static final String CHANNEL_ID = "SleepyBabyChannel";
    private static final String PREFS_NAME = "SleepyBabyPrefs";
    private static final String KEY_SLEEP_NOTIFICATION = "sleep_notification_";
    private static final String KEY_WAKE_NOTIFICATION = "wake_notification_";
    
    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "SleepyBaby Bildirimleri",
                NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Uyku ve uyanma zamanı bildirimleri");
            channel.enableLights(true);
            channel.enableVibration(true);
            
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
                Log.d(TAG, "Notification channel created");
            }
        }
    }
    
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            String action = intent.getAction();
            if (action == null) {
                Log.e(TAG, "Action is null");
                return;
            }

            int childId = intent.getIntExtra("child_id", -1);
            String childName = intent.getStringExtra("CHILD_NAME");
            
            if (childId == -1 || childName == null) {
                Log.e(TAG, "Invalid child data: id=" + childId + ", name=" + childName);
                return;
            }
            
            Log.d(TAG, "Received notification: " + action + " for child: " + childName);
            
            // Create notification channel
            createNotificationChannel(context);
            
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            
            String title;
            String content;
            boolean shouldShowQualityDialog = false;
            
            if ("com.example.sleepybaby.SLEEP_TIME".equals(action)) {
                title = "Uyku Zamanı";
                content = childName + " için uyku zamanı geldi!";
                prefs.edit().putLong(KEY_SLEEP_NOTIFICATION + childId, System.currentTimeMillis()).apply();
                Log.d(TAG, "Sleep notification stored for child: " + childName);
            } else if ("com.example.sleepybaby.WAKE_TIME".equals(action)) {
                title = "Uyanma Zamanı";
                content = childName + " için uyanma zamanı geldi!";
                prefs.edit().putLong(KEY_WAKE_NOTIFICATION + childId, System.currentTimeMillis()).apply();
                Log.d(TAG, "Wake notification stored for child: " + childName);
                
                // Check if sleep notification was received
                long sleepTime = prefs.getLong(KEY_SLEEP_NOTIFICATION + childId, 0);
                Log.d(TAG, "Sleep time stored: " + sleepTime);
                
                if (sleepTime > 0) {
                    Log.d(TAG, "Both notifications received, will show quality dialog for child: " + childName);
                    shouldShowQualityDialog = true;
                    
                    // Clear the stored times
                    prefs.edit()
                        .remove(KEY_SLEEP_NOTIFICATION + childId)
                        .remove(KEY_WAKE_NOTIFICATION + childId)
                        .apply();
                    Log.d(TAG, "Cleared notification timestamps for child: " + childName);
                } else {
                    Log.d(TAG, "Sleep notification not found for child: " + childName);
                }
            } else {
                Log.e(TAG, "Unknown action: " + action);
                return;
            }
            
            // Create intent to open ChildDetailActivity when notification is clicked
            Intent clickIntent = new Intent(context, ChildDetailActivity.class);
            clickIntent.putExtra("child_id", childId);
            if (shouldShowQualityDialog) {
                clickIntent.setAction("com.example.sleepybaby.SHOW_SLEEP_QUALITY");
            }
            clickIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                childId,
                clickIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
            
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
            
            if (notificationManager != null) {
                notificationManager.notify(childId, builder.build());
                Log.d(TAG, "Notification shown: " + title);
            } else {
                Log.e(TAG, "NotificationManager is null");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in onReceive: " + e.getMessage(), e);
        }
    }
} 
