package com.example.sleepybaby;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "sleepyBaby.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_CHILDREN = "children";
    public static final String TABLE_SLEEP_RECORDS = "sleep_records";
    public static final String TABLE_SLEEP_STATISTICS = "sleep_statistics";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_AGE = "age";
    public static final String COLUMN_SLEEP_HOUR = "sleepHour";
    public static final String COLUMN_SLEEP_MINUTE = "sleepMinute";
    public static final String COLUMN_WAKE_HOUR = "wakeHour";
    public static final String COLUMN_WAKE_MINUTE = "wakeMinute";

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
        // Children tablosu
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

        // SleepRecord tablosu
        String CREATE_SLEEP_RECORDS_TABLE = "CREATE TABLE " + TABLE_SLEEP_RECORDS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_CHILD_ID + " INTEGER,"
                + COLUMN_START_TIME + " INTEGER," // Unix timestamp olarak saklanacak
                + COLUMN_END_TIME + " INTEGER," // Unix timestamp olarak saklanacak
                + COLUMN_QUALITY + " INTEGER,"
                + COLUMN_NOTES + " TEXT,"
                + "FOREIGN KEY(" + COLUMN_CHILD_ID + ") REFERENCES " + TABLE_CHILDREN + "(" + COLUMN_ID + ")"
                + ")";
        db.execSQL(CREATE_SLEEP_RECORDS_TABLE);

        // SleepStatistics tablosu
        String CREATE_SLEEP_STATISTICS_TABLE = "CREATE TABLE " + TABLE_SLEEP_STATISTICS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_CHILD_ID + " INTEGER,"
                + COLUMN_DATE + " INTEGER," // Unix timestamp olarak saklanacak
                + COLUMN_TOTAL_SLEEP_MINUTES + " INTEGER,"
                + COLUMN_NUMBER_OF_SLEEPS + " INTEGER,"
                + COLUMN_AVERAGE_SLEEP_QUALITY + " REAL,"
                + COLUMN_LONGEST_SLEEP_MINUTES + " INTEGER,"
                + COLUMN_SHORTEST_SLEEP_MINUTES + " INTEGER,"
                + "FOREIGN KEY(" + COLUMN_CHILD_ID + ") REFERENCES " + TABLE_CHILDREN + "(" + COLUMN_ID + ")"
                + ")";
        db.execSQL(CREATE_SLEEP_STATISTICS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SLEEP_STATISTICS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SLEEP_RECORDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHILDREN);
        onCreate(db);
    }

    // Çocuk ekleme
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

    // Tüm çocukları getir
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

    // Çocuk silme
    public boolean deleteChild(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int deletedRows = db.delete(TABLE_CHILDREN, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return deletedRows > 0;
    }

    // Uyku kaydı ekleme
    public boolean addSleepRecord(SleepRecord record) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CHILD_ID, record.getChildId());
        values.put(COLUMN_START_TIME, record.getStartTime().getTime());
        values.put(COLUMN_END_TIME, record.getEndTime().getTime());
        values.put(COLUMN_QUALITY, record.getQuality());
        values.put(COLUMN_NOTES, record.getNotes());

        long result = db.insert(TABLE_SLEEP_RECORDS, null, values);
        db.close();
        return result != -1;
    }

    // Uyku kayıtlarını getir
    public List<SleepRecord> getSleepRecords(long childId) {
        List<SleepRecord> records = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_SLEEP_RECORDS, null,
                COLUMN_CHILD_ID + "=?", new String[]{String.valueOf(childId)},
                null, null, COLUMN_START_TIME + " DESC");

        if (cursor.moveToFirst()) {
            do {
                SleepRecord record = new SleepRecord(
                        cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CHILD_ID)),
                        new Date(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_START_TIME))),
                        new Date(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_END_TIME))),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUALITY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTES))
                );
                record.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                records.add(record);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return records;
    }

    // İstatistik kaydetme
    public boolean saveSleepStatistics(SleepStatistics stats) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CHILD_ID, stats.getChildId());
        values.put(COLUMN_DATE, stats.getDate().getTime());
        values.put(COLUMN_TOTAL_SLEEP_MINUTES, stats.getTotalSleepMinutes());
        values.put(COLUMN_NUMBER_OF_SLEEPS, stats.getNumberOfSleeps());
        values.put(COLUMN_AVERAGE_SLEEP_QUALITY, stats.getAverageSleepQuality());
        values.put(COLUMN_LONGEST_SLEEP_MINUTES, stats.getLongestSleepMinutes());
        values.put(COLUMN_SHORTEST_SLEEP_MINUTES, stats.getShortestSleepMinutes());

        long result = db.insert(TABLE_SLEEP_STATISTICS, null, values);
        db.close();
        return result != -1;
    }

    // İstatistikleri getir
    public SleepStatistics getSleepStatistics(long childId, Date date) {
        SQLiteDatabase db = this.getReadableDatabase();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        long startOfDay = cal.getTimeInMillis();

        cal.add(Calendar.DAY_OF_MONTH, 1);
        long endOfDay = cal.getTimeInMillis();

        Cursor cursor = db.query(TABLE_SLEEP_STATISTICS, null,
                COLUMN_CHILD_ID + "=? AND " + COLUMN_DATE + ">=? AND " + COLUMN_DATE + "<?",
                new String[]{String.valueOf(childId), String.valueOf(startOfDay), String.valueOf(endOfDay)},
                null, null, null);

        SleepStatistics stats = null;
        if (cursor.moveToFirst()) {
            stats = new SleepStatistics(childId, date);
            stats.setTotalSleepMinutes(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_TOTAL_SLEEP_MINUTES)));
            stats.setNumberOfSleeps(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NUMBER_OF_SLEEPS)));
            stats.setAverageSleepQuality(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AVERAGE_SLEEP_QUALITY)));
            stats.setLongestSleepMinutes(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_LONGEST_SLEEP_MINUTES)));
            stats.setShortestSleepMinutes(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_SHORTEST_SLEEP_MINUTES)));
        }
        cursor.close();
        db.close();
        return stats;
    }
}
