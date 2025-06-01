package com.example.sleepybaby.logic;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.example.sleepybaby.Child;
import com.example.sleepybaby.NotificationReceiver;
import com.example.sleepybaby.R;

import java.util.Calendar;

public class SleepNotificationManager {
    private static final String CHANNEL_ID = "SleepyBabyChannel";
    private static final int NOTIFICATION_ID = 1;
    
    private Context context;
    private NotificationManager notificationManager;
    private AlarmManager alarmManager;
    
    public SleepNotificationManager(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        this.alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        createNotificationChannel();
    }
    
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "SleepyBaby Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Uyku takibi bildirimleri");
            notificationManager.createNotificationChannel(channel);
        }
    }
    
    // Uyku zamanı bildirimi ayarla
    public void scheduleSleepTimeNotification(Child child, int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        
        // Eğer belirlenen zaman geçmişse, bir sonraki güne ayarla
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra("childId", child.getId());
        intent.putExtra("childName", child.getName());
        intent.putExtra("notificationType", "sleep_time");
        
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
            context,
            (int) child.getId(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.getTimeInMillis(),
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        );
    }
    
    // Uyanma zamanı bildirimi ayarla
    public void scheduleWakeTimeNotification(Child child, int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        
        // Eğer belirlenen zaman geçmişse, bir sonraki güne ayarla
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra("childId", child.getId());
        intent.putExtra("childName", child.getName());
        intent.putExtra("notificationType", "wake_time");
        
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
            context,
            (int) (child.getId() + 1000), // Farklı bir request code kullan
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.getTimeInMillis(),
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        );
    }
    
    // Bildirimleri iptal et
    public void cancelNotifications(Child child) {
        Intent sleepIntent = new Intent(context, NotificationReceiver.class);
        PendingIntent sleepPendingIntent = PendingIntent.getBroadcast(
            context,
            (int) child.getId(),
            sleepIntent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        Intent wakeIntent = new Intent(context, NotificationReceiver.class);
        PendingIntent wakePendingIntent = PendingIntent.getBroadcast(
            context,
            (int) (child.getId() + 1000),
            wakeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        alarmManager.cancel(sleepPendingIntent);
        alarmManager.cancel(wakePendingIntent);
    }
} 