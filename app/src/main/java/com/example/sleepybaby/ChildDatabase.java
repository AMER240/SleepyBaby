package com.example.sleepybaby;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ChildDatabase extends SQLiteOpenHelper {
    private static final String TAG = "ChildDatabase";
    private static final String DATABASE_NAME = "children.db";
    private static final int DATABASE_VERSION = 1;

    // Table names
    public static final String TABLE_CHILDREN = "children";

    // Children table columns
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_BIRTH_DATE = "birth_date";
    public static final String COLUMN_GENDER = "gender";

    public ChildDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            String CREATE_CHILDREN_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_CHILDREN + " ("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_NAME + " TEXT NOT NULL CHECK (length(" + COLUMN_NAME + ") > 0), "
                    + COLUMN_BIRTH_DATE + " INTEGER NOT NULL, "
                    + COLUMN_GENDER + " TEXT NOT NULL CHECK (" + COLUMN_GENDER + " IN ('M', 'F'))" + ")";

            db.execSQL(CREATE_CHILDREN_TABLE);
            Log.d(TAG, "Children table created successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error creating children table: " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            Log.d(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHILDREN);
            onCreate(db);
        } catch (Exception e) {
            Log.e(TAG, "Error upgrading database: " + e.getMessage(), e);
            throw e;
        }
    }

    public long addChild(Child child) {
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME, child.getName());
            values.put(COLUMN_BIRTH_DATE, child.getBirthDate());
            values.put(COLUMN_GENDER, child.getGender());
            
            long id = db.insert(TABLE_CHILDREN, null, values);
            Log.d(TAG, "Added child with ID: " + id);
            return id;
        } catch (Exception e) {
            Log.e(TAG, "Error adding child: " + e.getMessage(), e);
            return -1;
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    public boolean updateChild(Child child) {
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME, child.getName());
            values.put(COLUMN_BIRTH_DATE, child.getBirthDate());
            values.put(COLUMN_GENDER, child.getGender());
            
            int rows = db.update(TABLE_CHILDREN, values, COLUMN_ID + " = ?",
                    new String[]{String.valueOf(child.getId())});
            Log.d(TAG, "Updated child with ID " + child.getId() + ", rows affected: " + rows);
            return rows > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error updating child: " + e.getMessage(), e);
            return false;
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    public boolean deleteChild(int id) {
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            int rows = db.delete(TABLE_CHILDREN, COLUMN_ID + " = ?",
                    new String[]{String.valueOf(id)});
            Log.d(TAG, "Deleted child with ID " + id + ", rows affected: " + rows);
            return rows > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error deleting child: " + e.getMessage(), e);
            return false;
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    public Child getChild(int id) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = getReadableDatabase();
            cursor = db.query(TABLE_CHILDREN,
                    null,
                    COLUMN_ID + " = ?",
                    new String[]{String.valueOf(id)},
                    null,
                    null,
                    null);

            if (cursor != null && cursor.moveToFirst()) {
                Child child = new Child();
                child.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                child.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)));
                child.setBirthDate(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_BIRTH_DATE)));
                child.setGender(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GENDER)));
                return child;
            }
            return null;
        } catch (Exception e) {
            Log.e(TAG, "Error getting child: " + e.getMessage(), e);
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    public Cursor getAllChildren() {
        try {
            SQLiteDatabase db = getReadableDatabase();
            return db.query(TABLE_CHILDREN,
                    null,
                    null,
                    null,
                    null,
                    null,
                    COLUMN_NAME + " ASC");
        } catch (Exception e) {
            Log.e(TAG, "Error getting all children: " + e.getMessage(), e);
            return null;
        }
    }
}
