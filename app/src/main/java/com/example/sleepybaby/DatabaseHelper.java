package com.example.sleepybaby;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "sleepyBaby.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_CHILDREN = "children";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_AGE = "age";
    public static final String COLUMN_SLEEP_HOUR = "sleepHour";
    public static final String COLUMN_SLEEP_MINUTE = "sleepMinute";
    public static final String COLUMN_WAKE_HOUR = "wakeHour";
    public static final String COLUMN_WAKE_MINUTE = "wakeMinute";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CHILDREN_TABLE = "CREATE TABLE " + TABLE_CHILDREN + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_AGE + " INTEGER,"
                + COLUMN_SLEEP_HOUR + " INTEGER,"
                + COLUMN_SLEEP_MINUTE + " INTEGER,"
                + COLUMN_WAKE_HOUR + " INTEGER,"
                + COLUMN_WAKE_MINUTE + " INTEGER"
                + ")";
        db.execSQL(CREATE_CHILDREN_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHILDREN);
        onCreate(db);
    }

    // إضافة طفل جديد
    public boolean addChild(String name, int age, int sleepHour, int sleepMinute, int wakeHour, int wakeMinute) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_AGE, age);
        values.put(COLUMN_SLEEP_HOUR, sleepHour);
        values.put(COLUMN_SLEEP_MINUTE, sleepMinute);
        values.put(COLUMN_WAKE_HOUR, wakeHour);
        values.put(COLUMN_WAKE_MINUTE, wakeMinute);

        long result = db.insert(TABLE_CHILDREN, null, values);
        db.close();
        return result != -1;
    }

    // جلب جميع الأطفال
    public List<Child> getAllChildren() {
        List<Child> childList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CHILDREN, null);

        if (cursor.moveToFirst()) {
            do {
                Child child = new Child();
                child.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                child.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)));
                child.setAge(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_AGE)));
                child.setSleepHour(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SLEEP_HOUR)));
                child.setSleepMinute(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SLEEP_MINUTE)));
                child.setWakeHour(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_WAKE_HOUR)));
                child.setWakeMinute(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_WAKE_MINUTE)));
                childList.add(child);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return childList;
    }

    // حذف طفل حسب ID
    public boolean deleteChild(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int deletedRows = db.delete(TABLE_CHILDREN, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return deletedRows > 0;
    }
}
