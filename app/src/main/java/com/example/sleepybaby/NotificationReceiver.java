package com.example.sleepybaby;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log; // أضف هذا لاستخدام Logcat

import androidx.core.app.NotificationCompat;

public class NotificationReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "SleepyBabyChannel";
    private static final String TAG = "NotificationReceiver"; // لسهولة تتبع السجلات

    @Override
    public void onReceive(Context context, Intent intent) {
        long childId = intent.getLongExtra("childId", -1);
        String childName = intent.getStringExtra("childName");
        String notificationType = intent.getStringExtra("notificationType"); // "sleep_time" or "wake_time"

        Log.d(TAG, "Received broadcast for child: " + childName + ", type: " + notificationType);

        if (childId == -1 || childName == null || notificationType == null) {
            Log.e(TAG, "Invalid intent extras received.");
            return;
        }

        // هنا التغيير الرئيسي: بدلاً من إظهار Notification، نقوم بتشغيل AlarmActivity
        Intent alarmIntent = new Intent(context, AlarmActivity.class);
        alarmIntent.putExtra("childName", childName);
        alarmIntent.putExtra("notificationType", notificationType);
        alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // للتأكد من أنها تفتح كنشاط جديد

        // قد تحتاج إلى إضافة هذه الأعلام لضمان ظهور النشاط حتى من الخلفية
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startActivity(alarmIntent);
        } else {
            // بالنسبة للإصدارات الأقدم، قد تحتاج إلى WAKE_LOCK بشكل صريح أو استخدام خدمة في المقدمة
            context.startActivity(alarmIntent);
        }

        // يمكنك إبقاء إشعار بسيط إذا أردت، ولكن التركيز على الـ Activity
        // أو يمكنك إزالته بالكامل إذا كان الـ Activity هو كل ما تحتاجه
        // String title;
        // String content;
        // if ("sleep_time".equals(notificationType)) {
        //     title = "وقت النوم";
        //     content = childName + " حان وقت نومه!";
        // } else {
        //     title = "وقت الاستيقاظ";
        //     content = childName + " حان وقت استيقاظه!";
        // }
        // NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
        //     .setSmallIcon(R.drawable.ic_notification) // تأكد من وجود هذا الرمز
        //     .setContentTitle(title)
        //     .setContentText(content)
        //     .setPriority(NotificationCompat.PRIORITY_HIGH) // استخدم PRIORITY_HIGH لجذب الانتباه
        //     .setCategory(NotificationCompat.CATEGORY_ALARM) // تصنيفها كمنبه
        //     .setAutoCancel(true);
        // notificationManager.notify((int) childId, builder.build());
    }
}