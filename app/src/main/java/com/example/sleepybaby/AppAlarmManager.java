package com.example.sleepybaby;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;
import android.content.Intent;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import java.util.Calendar;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class AppAlarmManager {
    private static AppAlarmManager instance;
    private final AlarmDatabase dbHelper;
    private final SQLiteDatabase database;
    private static final String TAG = "AppAlarmManager";

    private AppAlarmManager(Context context) {
        dbHelper = new AlarmDatabase(context);
        database = dbHelper.getWritableDatabase();
    }

    public static AppAlarmManager getInstance(Context context) {
        if (instance == null) {
            instance = new AppAlarmManager(context.getApplicationContext());
        }
        return instance;
    }

    public long addAlarm(Context context, int childId, int hour, int minute) {
        Log.d(TAG, "Adding alarm for child " + childId + " at " + hour + ":" + minute);
        
        // إضافة المنبه إلى قاعدة البيانات
        ContentValues values = new ContentValues();
        values.put(AlarmDatabase.COLUMN_CHILD_ID, childId);
        values.put(AlarmDatabase.COLUMN_HOUR, hour);
        values.put(AlarmDatabase.COLUMN_MINUTE, minute);
        values.put(AlarmDatabase.COLUMN_ENABLED, 1);
        
        try {
            long alarmId = database.insert(AlarmDatabase.TABLE_ALARMS, null, values);
            if (alarmId != -1) {
                // إعداد المنبه في نظام Android
                if (setAlarm(context, alarmId, hour, minute)) {
                    Log.d(TAG, "Alarm set successfully with ID: " + alarmId);
                    return alarmId;
                } else {
                    Log.e(TAG, "Failed to set alarm in system");
                    // حذف المنبه من قاعدة البيانات إذا فشل إعداده
                    database.delete(AlarmDatabase.TABLE_ALARMS, 
                        AlarmDatabase.COLUMN_ID + " = ?", 
                        new String[]{String.valueOf(alarmId)});
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error adding alarm: " + e.getMessage(), e);
        }
        
        return -1;
    }

    private boolean setAlarm(Context context, long alarmId, int hour, int minute) {
        try {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager == null) {
                Log.e(TAG, "AlarmManager is null");
                return false;
            }

            // İzinleri kontrol et
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (!alarmManager.canScheduleExactAlarms()) {
                    Log.e(TAG, "SCHEDULE_EXACT_ALARM permission not granted");
                    return false;
                }
            }

            Intent intent = new Intent(context, AlarmReceiver.class);
            intent.setAction("com.example.sleepybaby.ALARM_TRIGGER");
            intent.putExtra("ALARM_ID", alarmId);
            intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            
            // جلب اسم الطفل
            String childName = getChildName(alarmId);
            intent.putExtra("CHILD_NAME", childName);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                (int) alarmId,
                intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
            );

            // حساب وقت المنبه
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            // إذا كان الوقت قد مر، اضبط المنبه لليوم التالي
            if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                Log.d(TAG, "Alarm time has passed, setting for next day: " + calendar.getTime().toString());
            }

            long triggerTime = calendar.getTimeInMillis();
            Log.d(TAG, "Setting alarm for: " + calendar.getTime().toString());

            // Her zaman setAlarmClock kullan
            alarmManager.setAlarmClock(
                new AlarmManager.AlarmClockInfo(triggerTime, pendingIntent),
                pendingIntent
            );
            Log.d(TAG, "Alarm set successfully using setAlarmClock");
            
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error setting alarm: " + e.getMessage(), e);
            return false;
        }
    }

    private String getChildName(long alarmId) {
        String childName = "طفل";
        Cursor cursor = null;
        try {
            cursor = database.query(AlarmDatabase.TABLE_ALARMS,
                new String[]{AlarmDatabase.COLUMN_CHILD_ID},
                AlarmDatabase.COLUMN_ID + " = ?",
                new String[]{String.valueOf(alarmId)},
                null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                int childId = cursor.getInt(cursor.getColumnIndexOrThrow(AlarmDatabase.COLUMN_CHILD_ID));
                cursor.close();

                cursor = database.query(ChildDatabase.TABLE_CHILDREN,
                    new String[]{ChildDatabase.COLUMN_NAME},
                    ChildDatabase.COLUMN_ID + " = ?",
                    new String[]{String.valueOf(childId)},
                    null, null, null);

                if (cursor != null && cursor.moveToFirst()) {
                    childName = cursor.getString(cursor.getColumnIndexOrThrow(ChildDatabase.COLUMN_NAME));
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting child name: " + e.getMessage(), e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return childName;
    }

    public void toggleAlarm(Context context, long alarmId, boolean enabled) {
        try {
            ContentValues values = new ContentValues();
            values.put(AlarmDatabase.COLUMN_ENABLED, enabled ? 1 : 0);
            database.update(AlarmDatabase.TABLE_ALARMS, values,
                AlarmDatabase.COLUMN_ID + " = ?",
                new String[]{String.valueOf(alarmId)});

            if (enabled) {
                Cursor cursor = database.query(AlarmDatabase.TABLE_ALARMS,
                    new String[]{AlarmDatabase.COLUMN_HOUR, AlarmDatabase.COLUMN_MINUTE},
                    AlarmDatabase.COLUMN_ID + " = ?",
                    new String[]{String.valueOf(alarmId)},
                    null, null, null);
                
                if (cursor != null && cursor.moveToFirst()) {
                    int hour = cursor.getInt(cursor.getColumnIndexOrThrow(AlarmDatabase.COLUMN_HOUR));
                    int minute = cursor.getInt(cursor.getColumnIndexOrThrow(AlarmDatabase.COLUMN_MINUTE));
                    setAlarm(context, alarmId, hour, minute);
                    cursor.close();
                }
            } else {
                cancelAlarm(context, alarmId);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in toggleAlarm: " + e.getMessage(), e);
        }
    }

    private void cancelAlarm(Context context, long alarmId) {
        try {
            Intent intent = new Intent(context, AlarmReceiver.class);
            intent.setAction("com.example.sleepybaby.ALARM_TRIGGER");
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                (int) alarmId,
                intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
            );
            
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                alarmManager.cancel(pendingIntent);
                pendingIntent.cancel();
                Log.d(TAG, "Alarm cancelled for ID: " + alarmId);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error cancelling alarm: " + e.getMessage(), e);
        }
    }

    // استرجاع جميع المنبهات النشطة
    public List<AlarmInfo> getActiveAlarms() {
        List<AlarmInfo> alarms = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = database.query(AlarmDatabase.TABLE_ALARMS,
                null,
                AlarmDatabase.COLUMN_ENABLED + " = 1",
                null,
                null,
                null,
                null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    AlarmInfo alarm = new AlarmInfo();
                    alarm.id = cursor.getLong(cursor.getColumnIndexOrThrow(AlarmDatabase.COLUMN_ID));
                    alarm.childId = cursor.getInt(cursor.getColumnIndexOrThrow(AlarmDatabase.COLUMN_CHILD_ID));
                    alarm.hour = cursor.getInt(cursor.getColumnIndexOrThrow(AlarmDatabase.COLUMN_HOUR));
                    alarm.minute = cursor.getInt(cursor.getColumnIndexOrThrow(AlarmDatabase.COLUMN_MINUTE));
                    alarms.add(alarm);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting active alarms: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return alarms;
    }

    // إعادة ضبط جميع المنبهات النشطة (يستخدم عند إعادة تشغيل الجهاز)
    public void restoreActiveAlarms(Context context) {
        List<AlarmInfo> alarms = getActiveAlarms();
        for (AlarmInfo alarm : alarms) {
            setAlarm(context, alarm.id, alarm.hour, alarm.minute);
        }
        Log.d(TAG, "Restored " + alarms.size() + " active alarms");
    }

    // الحصول على منبه معين
    public AlarmInfo getAlarm(long alarmId) {
        Cursor cursor = null;
        try {
            cursor = database.query(AlarmDatabase.TABLE_ALARMS,
                null,
                AlarmDatabase.COLUMN_ID + " = ?",
                new String[]{String.valueOf(alarmId)},
                null,
                null,
                null);

            if (cursor != null && cursor.moveToFirst()) {
                AlarmInfo alarm = new AlarmInfo();
                alarm.id = cursor.getLong(cursor.getColumnIndexOrThrow(AlarmDatabase.COLUMN_ID));
                alarm.childId = cursor.getInt(cursor.getColumnIndexOrThrow(AlarmDatabase.COLUMN_CHILD_ID));
                alarm.hour = cursor.getInt(cursor.getColumnIndexOrThrow(AlarmDatabase.COLUMN_HOUR));
                alarm.minute = cursor.getInt(cursor.getColumnIndexOrThrow(AlarmDatabase.COLUMN_MINUTE));
                return alarm;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting alarm: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    public static class AlarmInfo {
        public long id;
        public int childId;
        public int hour;
        public int minute;
    }
}
