package com.example.sleepybaby;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.WindowManager;
import androidx.core.app.NotificationCompat;

public class AlarmNotification {
    private static final String TAG = "AlarmNotification";
    private static final String CHANNEL_ID = "alarm_channel";
    private static final int NOTIFICATION_ID = 1;
    private Context context;
    private NotificationManager notificationManager;

    public AlarmNotification(Context context) {
        this.context = context;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                CharSequence name = "منبه";
                String description = "إشعارات المنبه";
                NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    name,
                    NotificationManager.IMPORTANCE_HIGH
                );
                
                // تكوين القناة
                channel.setDescription(description);
                channel.enableLights(true);
                channel.setLightColor(Color.RED);
                channel.enableVibration(true);
                channel.setVibrationPattern(new long[]{1000, 1000, 1000, 1000});
                channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                channel.setBypassDnd(true);
                channel.setShowBadge(true);
                
                // إعداد خصائص الصوت
                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build();
                channel.setSound(Settings.System.DEFAULT_ALARM_ALERT_URI, audioAttributes);

                notificationManager.createNotificationChannel(channel);
                Log.d(TAG, "Notification channel created successfully");
            } catch (Exception e) {
                Log.e(TAG, "Error creating notification channel: " + e.getMessage(), e);
            }
        }
    }

    public void showAlarmNotification(String childName) {
        try {
            // إنشاء Intent للنشاط
            Intent intent = new Intent(context, ActiveAlarmActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | 
                          Intent.FLAG_ACTIVITY_CLEAR_TOP |
                          Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            
            intent.putExtra("CHILD_NAME", childName);
            
            PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 
                0, 
                intent, 
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
            );

            // إنشاء الإشعار
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm) // استخدام أيقونة المنبه الافتراضية
                .setContentTitle("وقت الاستيقاظ!")
                .setContentText("حان وقت استيقاظ " + childName)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setAutoCancel(false)
                .setOngoing(true)
                .setContentIntent(pendingIntent)
                .setFullScreenIntent(pendingIntent, true)
                .setVibrate(new long[]{1000, 1000, 1000, 1000})
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSound(Settings.System.DEFAULT_ALARM_ALERT_URI);

            Notification notification = builder.build();
            notification.flags |= Notification.FLAG_INSISTENT; // تكرار الصوت

            notificationManager.notify(NOTIFICATION_ID, notification);
            Log.d(TAG, "Alarm notification shown for child: " + childName);
        } catch (Exception e) {
            Log.e(TAG, "Error showing notification: " + e.getMessage(), e);
        }
    }

    public void cancelNotification() {
        try {
            notificationManager.cancel(NOTIFICATION_ID);
            Log.d(TAG, "Notification cancelled");
        } catch (Exception e) {
            Log.e(TAG, "Error cancelling notification: " + e.getMessage(), e);
        }
    }
}
