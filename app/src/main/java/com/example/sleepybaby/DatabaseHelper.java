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
    private static final int DATABASE_VERSION = 5;

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
    public static final String COLUMN_PHOTO_URI = "photo_uri";
    
    // SleepRecord tablosu için kolonlar
    public static final String COLUMN_CHILD_ID = "childId";
    public static final String COLUMN_SLEEP_TIME = "sleep_time";
    public static final String COLUMN_WAKE_TIME = "wake_time";
    public static final String COLUMN_SLEEP_QUALITY = "sleep_quality";
    public static final String COLUMN_NOTES = "notes";
    
    // SleepStatistics tablosu için kolonlar
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_TOTAL_SLEEP_MINUTES = "totalSleepMinutes";
    public static final String COLUMN_NUMBER_OF_SLEEPS = "numberOfSleeps";
    public static final String COLUMN_AVERAGE_SLEEP_QUALITY = "averageSleepQuality";
    public static final String COLUMN_LONGEST_SLEEP_MINUTES = "longestSleepMinutes";
    public static final String COLUMN_SHORTEST_SLEEP_MINUTES = "shortestSleepMinutes";

    private static final String CREATE_SLEEP_RECORDS_TABLE = "CREATE TABLE " + TABLE_SLEEP_RECORDS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_CHILD_ID + " INTEGER,"
            + COLUMN_SLEEP_TIME + " INTEGER,"
            + COLUMN_WAKE_TIME + " INTEGER,"
            + COLUMN_SLEEP_QUALITY + " INTEGER,"
            + COLUMN_NOTES + " TEXT,"
            + "FOREIGN KEY(" + COLUMN_CHILD_ID + ") REFERENCES " + TABLE_CHILDREN + "(" + COLUMN_ID + ")"
            + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Çocuklar tablosu
        db.execSQL("CREATE TABLE " + TABLE_CHILDREN + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_NAME + " TEXT NOT NULL," +
                COLUMN_BIRTH_DATE + " INTEGER NOT NULL," +
                COLUMN_GENDER + " TEXT NOT NULL," +
                COLUMN_SLEEP_HOUR + " INTEGER NOT NULL," +
                COLUMN_SLEEP_MINUTE + " INTEGER NOT NULL," +
                COLUMN_WAKE_HOUR + " INTEGER NOT NULL," +
                COLUMN_WAKE_MINUTE + " INTEGER NOT NULL," +
                COLUMN_PHOTO_URI + " TEXT" +
                ")");

        // Uyku kayıtları tablosu
        db.execSQL(CREATE_SLEEP_RECORDS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 4) {
            // Eski tabloları sil
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_SLEEP_RECORDS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHILDREN);

            // Tabloları yeniden oluştur
            onCreate(db);
        }
    }

    // Çocuk ekleme
    public boolean addChild(String name, long birthDate, String gender, int sleepHour, int sleepMinute, int wakeHour, int wakeMinute, String photoUri) {
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
            values.put(COLUMN_PHOTO_URI, photoUri);
            
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
                        int photoUriIndex = cursor.getColumnIndex(COLUMN_PHOTO_URI);
                        if (photoUriIndex != -1) {
                            child.setPhotoUri(cursor.getString(photoUriIndex));
                        }
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

        values.put(COLUMN_CHILD_ID, record.getChildId());
        values.put(COLUMN_SLEEP_TIME, record.getSleepTime().getTime());
        
        // Uyanma zamanını hesapla
        Calendar wakeCalendar = Calendar.getInstance();
        wakeCalendar.setTime(record.getSleepTime());
        wakeCalendar.set(Calendar.HOUR_OF_DAY, record.getWakeHour());
        wakeCalendar.set(Calendar.MINUTE, record.getWakeMinute());
        wakeCalendar.set(Calendar.SECOND, 0);
        
        // Eğer uyanma saati uyku saatinden önceyse, ertesi güne geç
        Calendar sleepCalendar = Calendar.getInstance();
        sleepCalendar.setTime(record.getSleepTime());
        sleepCalendar.set(Calendar.HOUR_OF_DAY, record.getSleepHour());
        sleepCalendar.set(Calendar.MINUTE, record.getSleepMinute());
        sleepCalendar.set(Calendar.SECOND, 0);
        
        if (wakeCalendar.before(sleepCalendar)) {
            wakeCalendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        
        values.put(COLUMN_WAKE_TIME, wakeCalendar.getTimeInMillis());
        values.put(COLUMN_SLEEP_QUALITY, record.getQuality());
        values.put(COLUMN_NOTES, record.getNotes());

        long result = db.insert(TABLE_SLEEP_RECORDS, null, values);
        db.close();
        return result;
    }
    
    // Çocuğun uyku kayıtlarını getirme
    public List<SleepRecord> getSleepRecords(int childId) {
        List<SleepRecord> records = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        try {
            String[] columns = {
                COLUMN_ID,
                COLUMN_CHILD_ID,
                COLUMN_SLEEP_TIME,
                COLUMN_WAKE_TIME,
                COLUMN_SLEEP_QUALITY,
                COLUMN_NOTES
            };

            String selection = COLUMN_CHILD_ID + " = ?";
            String[] selectionArgs = {String.valueOf(childId)};
            String orderBy = COLUMN_SLEEP_TIME + " DESC";

            Cursor cursor = db.query(
                TABLE_SLEEP_RECORDS,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                orderBy
            );

            if (cursor.moveToFirst()) {
                do {
                    SleepRecord record = new SleepRecord();
                    record.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                    record.setChildId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CHILD_ID)));
                    
                    // Tarihleri ayarla
                    Date sleepTime = new Date(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_SLEEP_TIME)));
                    Date wakeTime = new Date(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_WAKE_TIME)));
                    
                    Calendar sleepCalendar = Calendar.getInstance();
                    sleepCalendar.setTime(sleepTime);
                    Calendar wakeCalendar = Calendar.getInstance();
                    wakeCalendar.setTime(wakeTime);
                    
                    record.setSleepTime(sleepTime);
                    record.setWakeTime(wakeTime);
                    record.setSleepHour(sleepCalendar.get(Calendar.HOUR_OF_DAY));
                    record.setSleepMinute(sleepCalendar.get(Calendar.MINUTE));
                    record.setWakeHour(wakeCalendar.get(Calendar.HOUR_OF_DAY));
                    record.setWakeMinute(wakeCalendar.get(Calendar.MINUTE));
                    
                    record.setQuality(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SLEEP_QUALITY)));
                    record.setNotes(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTES)));
                    records.add(record);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "Error in getSleepRecords: " + e.getMessage());
            e.printStackTrace();
        }

        return records;
    }
    
    // Belirli bir tarihten sonraki uyku istatistiklerini getirme
    public SleepStatistics getSleepStatistics(int childId, Date startDate) {
        SQLiteDatabase db = this.getReadableDatabase();
        int totalSleepMinutes = 0;
        double totalQuality = 0;
        int recordCount = 0;

        Cursor cursor = db.query(TABLE_SLEEP_RECORDS,
                new String[]{COLUMN_SLEEP_TIME, COLUMN_WAKE_TIME, COLUMN_SLEEP_QUALITY},
                COLUMN_CHILD_ID + " = ? AND " + COLUMN_SLEEP_TIME + " >= ?",
                new String[]{String.valueOf(childId), String.valueOf(startDate.getTime())},
                null, null, null);

        if (cursor.moveToFirst()) {
            do {
                long sleepTime = cursor.getLong(0);
                long wakeTime = cursor.getLong(1);
                double quality = cursor.getDouble(2);

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

    // Çocuk getirme
    public Child getChild(int childId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CHILDREN,
                null,
                COLUMN_ID + "=?",
                new String[]{String.valueOf(childId)},
                null, null, null);

        Child child = null;
        if (cursor.moveToFirst()) {
            child = new Child();
            child.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
            child.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)));
            child.setBirthDate(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_BIRTH_DATE)));
            child.setGender(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GENDER)));
            child.setSleepHour(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SLEEP_HOUR)));
            child.setSleepMinute(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SLEEP_MINUTE)));
            child.setWakeHour(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_WAKE_HOUR)));
            child.setWakeMinute(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_WAKE_MINUTE)));
            int photoUriIndex = cursor.getColumnIndex(COLUMN_PHOTO_URI);
            if (photoUriIndex != -1) {
                child.setPhotoUri(cursor.getString(photoUriIndex));
            }
        }
        cursor.close();
        return child;
    }

    // Çocuk güncelleme
    public boolean updateChild(Child child) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, child.getName());
        values.put(COLUMN_BIRTH_DATE, child.getBirthDate());
        values.put(COLUMN_GENDER, child.getGender());
        values.put(COLUMN_SLEEP_HOUR, child.getSleepHour());
        values.put(COLUMN_SLEEP_MINUTE, child.getSleepMinute());
        values.put(COLUMN_WAKE_HOUR, child.getWakeHour());
        values.put(COLUMN_WAKE_MINUTE, child.getWakeMinute());
        values.put(COLUMN_PHOTO_URI, child.getPhotoUri());
        int result = db.update(TABLE_CHILDREN, values, COLUMN_ID + "=?",
                new String[]{String.valueOf(child.getId())});
        db.close();
        return result > 0;
    }

    // Ortalama uyku süresini hesaplama
    public double getAverageSleepHours(int childId, int days) {
        SQLiteDatabase db = this.getReadableDatabase();
        double totalHours = 0;
        int count = 0;

        try {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, -days);
            long startDate = calendar.getTimeInMillis();

            String query = "SELECT sleep_time, wake_time FROM " + TABLE_SLEEP_RECORDS +
                          " WHERE " + COLUMN_CHILD_ID + " = ? AND sleep_time >= ? " +
                          "ORDER BY sleep_time DESC";

            Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(childId), String.valueOf(startDate)});

            if (cursor.moveToFirst()) {
                do {
                    long sleepTime = cursor.getLong(0);
                    long wakeTime = cursor.getLong(1);
                    
                    // Uyku süresini hesapla (saat cinsinden)
                    double duration = (wakeTime - sleepTime) / (60.0 * 60.0 * 1000.0);
                    totalHours += duration;
                    count++;
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "Error in getAverageSleepHours: " + e.getMessage());
            e.printStackTrace();
        }

        return count > 0 ? totalHours / count : 0;
    }

    // Ortalama uyku kalitesini hesaplama
    public double getSleepQuality(int childId, int days) {
        SQLiteDatabase db = this.getReadableDatabase();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -days);
        long startDate = calendar.getTimeInMillis();

        Cursor cursor = db.query(TABLE_SLEEP_RECORDS,
                new String[]{COLUMN_SLEEP_QUALITY},
                COLUMN_CHILD_ID + " = ? AND " + COLUMN_SLEEP_TIME + " >= ?",
                new String[]{String.valueOf(childId), String.valueOf(startDate)},
                null, null, null);

        double totalQuality = 0;
        int count = 0;

        if (cursor.moveToFirst()) {
            do {
                totalQuality += cursor.getInt(0);
                count++;
            } while (cursor.moveToNext());
        }
        cursor.close();

        return count > 0 ? totalQuality / count : 0;
    }

    // Belirli bir tarih aralığındaki uyku kayıtlarını getirme
    public List<SleepRecord> getSleepRecords(int childId, long startDate, long endDate) {
        List<SleepRecord> records = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_SLEEP_RECORDS,
                new String[]{COLUMN_ID, COLUMN_CHILD_ID, COLUMN_SLEEP_TIME, COLUMN_WAKE_TIME, COLUMN_SLEEP_QUALITY, COLUMN_NOTES},
                COLUMN_CHILD_ID + " = ? AND " + COLUMN_SLEEP_TIME + " >= ? AND " + COLUMN_SLEEP_TIME + " <= ?",
                new String[]{String.valueOf(childId), String.valueOf(startDate), String.valueOf(endDate)},
                null, null, COLUMN_SLEEP_TIME + " DESC");

        if (cursor.moveToFirst()) {
            do {
                SleepRecord record = new SleepRecord();
                record.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                record.setChildId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CHILD_ID)));
                
                // Tarihleri ayarla
                Date sleepTime = new Date(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_SLEEP_TIME)));
                Date wakeTime = new Date(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_WAKE_TIME)));
                
                Calendar sleepCalendar = Calendar.getInstance();
                sleepCalendar.setTime(sleepTime);
                Calendar wakeCalendar = Calendar.getInstance();
                wakeCalendar.setTime(wakeTime);
                
                record.setSleepTime(sleepTime);
                record.setSleepHour(sleepCalendar.get(Calendar.HOUR_OF_DAY));
                record.setSleepMinute(sleepCalendar.get(Calendar.MINUTE));
                record.setWakeHour(wakeCalendar.get(Calendar.HOUR_OF_DAY));
                record.setWakeMinute(wakeCalendar.get(Calendar.MINUTE));
                
                record.setQuality(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SLEEP_QUALITY)));
                record.setNotes(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTES)));
                records.add(record);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return records;
    }

    // Uyku kaydı silme
    public int deleteSleepRecord(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int deletedRows = db.delete(TABLE_SLEEP_RECORDS, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return deletedRows;
    }
}
