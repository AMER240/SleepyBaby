package com.example.sleepybaby;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "sleepyBaby.db";
    private static final int DATABASE_VERSION = 3;

    public static final String TABLE_CHILDREN = "children";
    public static final String TABLE_SLEEP_RECORDS = "sleep_records";
    public static final String TABLE_SLEEP_STATISTICS = "sleep_statistics";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_BIRTH_DATE = "birth_date";
    public static final String COLUMN_GENDER = "gender";
    public static final String COLUMN_SLEEP_HOUR = "sleep_hour";
    public static final String COLUMN_SLEEP_MINUTE = "sleep_minute";
    public static final String COLUMN_WAKE_HOUR = "wake_hour";
    public static final String COLUMN_WAKE_MINUTE = "wake_minute";
    
    // SleepRecord tablosu için kolonlar
    public static final String COLUMN_CHILD_ID = "childId";
    public static final String COLUMN_START_TIME = "startTime";
    public static final String COLUMN_END_TIME = "endTime";
    public static final String COLUMN_QUALITY = "quality";
    public static final String COLUMN_NOTES = "notes";
    
    // SleepStatistics tablosu için kolonlar
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_TOTAL_SLEEP_MINUTES = "totalSleepMinutes";
    public static final String COLUMN_NUMBER_OF_SLEEPS = "numberOfSleeps";
    public static final String COLUMN_AVERAGE_SLEEP_QUALITY = "averageSleepQuality";
    public static final String COLUMN_LONGEST_SLEEP_MINUTES = "longestSleepMinutes";
    public static final String COLUMN_SHORTEST_SLEEP_MINUTES = "shortestSleepMinutes";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Çocuklar tablosu
        db.execSQL("CREATE TABLE children (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "birth_date INTEGER NOT NULL," +
                "gender TEXT NOT NULL," +
                "sleep_hour INTEGER NOT NULL," +
                "sleep_minute INTEGER NOT NULL," +
                "wake_hour INTEGER NOT NULL," +
                "wake_minute INTEGER NOT NULL)");

        // Uyku kayıtları tablosu
        db.execSQL("CREATE TABLE sleep_records (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "child_id INTEGER NOT NULL," +
                "sleep_time INTEGER NOT NULL," +
                "wake_time INTEGER NOT NULL," +
                "sleep_quality INTEGER NOT NULL," +
                "FOREIGN KEY (child_id) REFERENCES children(id) ON DELETE CASCADE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS sleep_records");
        db.execSQL("DROP TABLE IF EXISTS children");
        onCreate(db);
    }

    // Çocuk ekleme
    public boolean addChild(String name, long birthDate, String gender, int sleepHour, int sleepMinute, int wakeHour, int wakeMinute) {
        SQLiteDatabase db = null;
        try {
            Log.d(TAG, "Starting addChild method...");
            Log.d(TAG, "Parameters - Name: '" + name + "', BirthDate: " + birthDate + ", Gender: '" + gender + "'");
            Log.d(TAG, "Sleep time: " + sleepHour + ":" + sleepMinute + ", Wake time: " + wakeHour + ":" + wakeMinute);
            
            db = this.getWritableDatabase();
            Log.d(TAG, "Database opened successfully");
            
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME, name);
            values.put(COLUMN_BIRTH_DATE, birthDate);
            values.put(COLUMN_GENDER, gender);
            values.put(COLUMN_SLEEP_HOUR, sleepHour);
            values.put(COLUMN_SLEEP_MINUTE, sleepMinute);
            values.put(COLUMN_WAKE_HOUR, wakeHour);
            values.put(COLUMN_WAKE_MINUTE, wakeMinute);
            
            Log.d(TAG, "ContentValues created: " + values.toString());
            
            long result = db.insert(TABLE_CHILDREN, null, values);
            Log.d(TAG, "Insert result: " + result);
            
            if (result == -1) {
                Log.e(TAG, "Insert failed - result is -1");
                return false;
            } else {
                Log.d(TAG, "Insert successful - new row ID: " + result);
                return true;
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Exception in addChild: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            Log.e(TAG, "Stack trace: ", e);
            return false;
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
                Log.d(TAG, "Database closed");
            }
        }
    }

    // Tüm çocukları getir
    public List<Child> getAllChildren() {
        List<Child> childList = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        
        try {
            Log.d(TAG, "Starting getAllChildren method...");
            db = this.getReadableDatabase();
            Log.d(TAG, "Database opened for reading");
            
            cursor = db.rawQuery("SELECT * FROM " + TABLE_CHILDREN, null);
            Log.d(TAG, "Query executed, cursor count: " + cursor.getCount());

            if (cursor.moveToFirst()) {
                do {
                    try {
                        Child child = new Child();
                        child.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                        child.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)));
                        child.setBirthDate(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_BIRTH_DATE)));
                        child.setGender(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GENDER)));
                        child.setSleepHour(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SLEEP_HOUR)));
                        child.setSleepMinute(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SLEEP_MINUTE)));
                        child.setWakeHour(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_WAKE_HOUR)));
                        child.setWakeMinute(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_WAKE_MINUTE)));
                        childList.add(child);
                        Log.d(TAG, "Child added: " + child.getName() + " (ID: " + child.getId() + ")");
                    } catch (Exception e) {
                        Log.e(TAG, "Error processing child record: " + e.getMessage());
                        e.printStackTrace();
                    }
                } while (cursor.moveToNext());
            } else {
                Log.d(TAG, "No children found in database");
            }
            
            Log.d(TAG, "getAllChildren completed successfully, returning " + childList.size() + " children");
            return childList;
            
        } catch (Exception e) {
            Log.e(TAG, "Exception in getAllChildren: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            Log.e(TAG, "Stack trace: ", e);
            return new ArrayList<>(); // Boş liste döndür
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
                Log.d(TAG, "Cursor closed");
            }
            if (db != null && db.isOpen()) {
                db.close();
                Log.d(TAG, "Database closed");
            }
        }
    }

    // Çocuk silme
    public boolean deleteChild(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int deletedRows = db.delete(TABLE_CHILDREN, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return deletedRows > 0;
    }
    
    // Uyku kaydı ekleme
    public long addSleepRecord(SleepRecord record) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("child_id", record.getChildId());
        values.put("sleep_time", record.getSleepTime().getTime());
        values.put("wake_time", record.getWakeTime().getTime());
        values.put("sleep_quality", record.getSleepQuality());
        return db.insert("sleep_records", null, values);
    }
    
    // Çocuğun uyku kayıtlarını getirme
    public List<SleepRecord> getSleepRecords(int childId) {
        List<SleepRecord> records = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("sleep_records",
                new String[]{"id", "child_id", "sleep_time", "wake_time", "sleep_quality"},
                "child_id = ?",
                new String[]{String.valueOf(childId)},
                null, null, "sleep_time DESC");

        if (cursor.moveToFirst()) {
            do {
                SleepRecord record = new SleepRecord(
                    cursor.getInt(0),
                    cursor.getInt(1),
                    new Date(cursor.getLong(2)),
                    new Date(cursor.getLong(3)),
                    cursor.getInt(4)
                );
                records.add(record);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return records;
    }
    
    // Belirli bir tarihten sonraki uyku istatistiklerini getirme
    public SleepStatistics getSleepStatistics(int childId, Date startDate) {
        SQLiteDatabase db = this.getReadableDatabase();
        int totalSleepMinutes = 0;
        double totalQuality = 0;
        int recordCount = 0;

        Cursor cursor = db.query("sleep_records",
                new String[]{"sleep_time", "wake_time", "sleep_quality"},
                "child_id = ? AND sleep_time >= ?",
                new String[]{String.valueOf(childId), String.valueOf(startDate.getTime())},
                null, null, null);

        if (cursor.moveToFirst()) {
            do {
                long sleepTime = cursor.getLong(0);
                long wakeTime = cursor.getLong(1);
                int quality = cursor.getInt(2);

                // Uyku süresini hesapla (dakika cinsinden)
                long duration = (wakeTime - sleepTime) / (60 * 1000);
                totalSleepMinutes += (int)duration;
                totalQuality += quality;
                recordCount++;
            } while (cursor.moveToNext());
        }
        cursor.close();

        double averageQuality = recordCount > 0 ? totalQuality / recordCount : 0;
        return new SleepStatistics(totalSleepMinutes, averageQuality, recordCount);
    }
}
