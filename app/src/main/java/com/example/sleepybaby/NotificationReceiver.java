package com.example.sleepybaby;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class NotificationReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "SleepyBabyChannel";
    
    @Override
    public void onReceive(Context context, Intent intent) {
        long childId = intent.getLongExtra("childId", -1);
        String childName = intent.getStringExtra("childName");
        String notificationType = intent.getStringExtra("notificationType");
        
        if (childId == -1 || childName == null || notificationType == null) {
            return;
        }
        
        android.app.NotificationManager notificationManager = (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        
        String title;
        String content;
        
        if ("sleep_time".equals(notificationType)) {
            title = "Uyku Zamanı";
            content = childName + " için uyku zamanı geldi!";
        } else if ("wake_time".equals(notificationType)) {
            title = "Uyanma Zamanı";
            content = childName + " için uyanma zamanı geldi!";
        } else {
            return;
        }
        
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true);
        
        notificationManager.notify((int) childId, builder.build());
    }
} 