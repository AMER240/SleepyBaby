package com.example.sleepybaby;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;


public class AlarmChildConnector {
    private static AlarmChildConnector instance;
    private AlarmDatabase alarmDb;
    private SQLiteDatabase database;

    private AlarmChildConnector(Context context) {
        alarmDb = new AlarmDatabase(context);
        database = alarmDb.getWritableDatabase();
    }

    public static AlarmChildConnector getInstance(Context context) {
        if (instance == null) {
            instance = new AlarmChildConnector(context);
        }
        return instance;
    }

    // ربط منبه جديد بطفلك
    public long connectAlarmToChild(int childId, int hour, int minute) {
        // أولاً، نتأكد من وجود الطفل في قاعدة البيانات
        Cursor childCursor = database.query("children",
                null,
                "id = ?",
                new String[]{String.valueOf(childId)},
                null,
                null,
                null);
        
        if (childCursor == null || !childCursor.moveToFirst()) {
            return -1; // الطفل غير موجود
        }

            // إضافة المنبه
        ContentValues values = new ContentValues();
        values.put(AlarmDatabase.COLUMN_CHILD_ID, childId);
        values.put(AlarmDatabase.COLUMN_HOUR, hour);
        values.put(AlarmDatabase.COLUMN_MINUTE, minute);
        return database.insert(AlarmDatabase.TABLE_ALARMS, null, values);
    }

    // تحديث وقت المنبه لطفلك
    public void updateAlarmTime(int alarmId, int hour, int minute) {
        String sql = "UPDATE " + AlarmDatabase.TABLE_ALARMS + " SET " +
                    AlarmDatabase.COLUMN_HOUR + " = ?, " +
                    AlarmDatabase.COLUMN_MINUTE + " = ? WHERE " +
                    AlarmDatabase.COLUMN_ID + " = ?";
        
        database.execSQL(sql, new Object[]{hour, minute, alarmId});
    }

    // جلب جميع المنبهات لطفلك
    public Cursor getChildAlarms(int childId) {
        return database.query(AlarmDatabase.TABLE_ALARMS,
                null,
                AlarmDatabase.COLUMN_CHILD_ID + " = ?",
                new String[]{String.valueOf(childId)},
                null,
                null,
                null);
    }

    // جلب تفاصيل الطفل المرتبط بالمنبه
    public Cursor getChildDetails(int alarmId) {
        return database.query("children",
                null,
                "id IN (SELECT " + AlarmDatabase.COLUMN_CHILD_ID + " FROM " +
                AlarmDatabase.TABLE_ALARMS + " WHERE " + AlarmDatabase.COLUMN_ID + " = ?)",
                new String[]{String.valueOf(alarmId)},
                null,
                null,
                null);
    }

    // حذف المنبه وفصله من الطفل
    public void disconnectAlarm(int alarmId) {
        database.delete(AlarmDatabase.TABLE_ALARMS, AlarmDatabase.COLUMN_ID + " = ?", new String[]{String.valueOf(alarmId)});
    }
}
