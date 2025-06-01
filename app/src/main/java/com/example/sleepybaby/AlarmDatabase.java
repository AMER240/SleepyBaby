package com.example.sleepybaby;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class AlarmDatabase extends SQLiteOpenHelper {
    private static final String TAG = "AlarmDatabase";
    private static final String DATABASE_NAME = "alarms.db";
    private static final int DATABASE_VERSION = 1;

    // Table names
    public static final String TABLE_ALARMS = "alarms";
    public static final String TABLE_ALARM_SETTINGS = "alarm_settings";

    // Alarms table columns
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_CHILD_ID = "child_id";
    public static final String COLUMN_HOUR = "hour";
    public static final String COLUMN_MINUTE = "minute";
    public static final String COLUMN_ENABLED = "enabled";

    // Alarm settings columns
    public static final String COLUMN_VOLUME = "volume";
    public static final String COLUMN_SOUND_ID = "sound_id";

    public AlarmDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            // Enable foreign key support
            db.execSQL("PRAGMA foreign_keys=ON;");

            // Create alarms table
            String CREATE_ALARMS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_ALARMS + " ("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_CHILD_ID + " INTEGER NOT NULL, "
                    + COLUMN_HOUR + " INTEGER NOT NULL CHECK (" + COLUMN_HOUR + " >= 0 AND " + COLUMN_HOUR + " < 24), "
                    + COLUMN_MINUTE + " INTEGER NOT NULL CHECK (" + COLUMN_MINUTE + " >= 0 AND " + COLUMN_MINUTE + " < 60), "
                    + COLUMN_ENABLED + " INTEGER NOT NULL DEFAULT 0, "
                    + "CONSTRAINT fk_child "
                    + "FOREIGN KEY (" + COLUMN_CHILD_ID + ") "
                    + "REFERENCES " + ChildDatabase.TABLE_CHILDREN + " (" + ChildDatabase.COLUMN_ID + ") "
                    + "ON DELETE CASCADE)";

            // Create alarm settings table
            String CREATE_SETTINGS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_ALARM_SETTINGS + " ("
                    + COLUMN_ID + " INTEGER PRIMARY KEY, "
                    + COLUMN_VOLUME + " INTEGER NOT NULL CHECK (" + COLUMN_VOLUME + " >= 0 AND " + COLUMN_VOLUME + " <= 100), "
                    + COLUMN_SOUND_ID + " INTEGER NOT NULL, "
                    + "CONSTRAINT fk_alarm "
                    + "FOREIGN KEY (" + COLUMN_ID + ") REFERENCES "
                    + TABLE_ALARMS + "(" + COLUMN_ID + ") ON DELETE CASCADE)";

            db.execSQL(CREATE_ALARMS_TABLE);
            db.execSQL(CREATE_SETTINGS_TABLE);

            Log.d(TAG, "Database tables created successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error creating database tables: " + e.getMessage(), e);
            throw e; // إعادة رمي الاستثناء ليتم معالجته في المستوى الأعلى
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            Log.d(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);
            
            // Drop older tables if existed
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALARM_SETTINGS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALARMS);
            
            // Create tables again
            onCreate(db);
        } catch (Exception e) {
            Log.e(TAG, "Error upgrading database: " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }
}
