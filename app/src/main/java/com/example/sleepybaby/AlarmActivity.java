package com.example.sleepybaby;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import java.util.Calendar;
import java.util.Locale;
import android.widget.ArrayAdapter;
import android.app.TimePickerDialog.OnTimeSetListener;
import com.example.sleepybaby.AppAlarmManager;
import com.example.sleepybaby.ChildDatabase;
import android.content.ContentValues;
import android.util.Log;
import android.widget.Toast;

public class AlarmActivity extends AppCompatActivity {
    private TextView alarmTime;
    private TextView alarmStatus;
    private MaterialButton btnSetTime;
    private MaterialButton btnTurnOn;
    private MaterialButton btnTurnOff;
    private SeekBar volumeSeekBar;
    private Spinner soundSpinner;
    private MediaPlayer mediaPlayer;
    private AppAlarmManager appAlarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        initializeViews();
        setupAlarmControls();
        setupSoundControls();
    }

    private void initializeViews() {
        alarmTime = findViewById(R.id.alarmTime);
        alarmStatus = findViewById(R.id.alarmStatus);
        btnSetTime = findViewById(R.id.btnSetTime);
        btnTurnOn = findViewById(R.id.btnTurnOn);
        btnTurnOff = findViewById(R.id.btnTurnOff);
        volumeSeekBar = findViewById(R.id.volumeSeekBar);
        soundSpinner = findViewById(R.id.soundSpinner);

        appAlarmManager = AppAlarmManager.getInstance(this);

        // إنشاء قاعدة البيانات
        ChildDatabase dbHelper = new ChildDatabase(this);
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        // جلب أول طفل موجود في قاعدة البيانات
        Cursor childCursor = database.query(ChildDatabase.TABLE_CHILDREN,
            new String[]{ChildDatabase.COLUMN_ID},
            null, null, null, null, null);
        
        int childId = 1; // القيمة الافتراضية
        if (childCursor != null && childCursor.moveToFirst()) {
            int columnIndex = childCursor.getColumnIndex(ChildDatabase.COLUMN_ID);
            if (columnIndex >= 0) {
                childId = childCursor.getInt(columnIndex);
            }
            childCursor.close();
        }
        
        // إغلاق قاعدة البيانات
        database.close();
        dbHelper.close();
    }

    private void setupAlarmControls() {
        btnSetTime.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> 
                alarmTime.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)),
                0, 0, true);
            timePickerDialog.show();
        });

        btnTurnOn.setOnClickListener(v -> {
            if (alarmTime.getText().toString().isEmpty()) {
                alarmStatus.setText(getString(R.string.please_set_alarm_time));
                return;
            }

            // استخراج الساعات والدقائق
            String[] timeParts = alarmTime.getText().toString().split("[:؛]");
            int hour = Integer.parseInt(timeParts[0]);
            int minute = Integer.parseInt(timeParts[1]);

            // إنشاء منبه جديد باستخدام AppAlarmManager
            long alarmId = appAlarmManager.addAlarm(this, 1, hour, minute); // 1 هو ID الطفل
            
            if (alarmId != -1) {
                alarmStatus.setText(getString(R.string.alarm_is_on));
                Log.d("AlarmActivity", "Alarm set successfully with ID: " + alarmId);
                // إظهار رسالة تأكيد
                Toast.makeText(AlarmActivity.this, "تم إعداد المنبه بنجاح", Toast.LENGTH_SHORT).show();
            } else {
                alarmStatus.setText(getString(R.string.failed_to_set_alarm));
                Log.e("AlarmActivity", "Failed to set alarm");
                // إظهار رسالة خطأ
                Toast.makeText(AlarmActivity.this, "فشل في إعداد المنبه", Toast.LENGTH_SHORT).show();
            }
        });

        btnTurnOff.setOnClickListener(v -> {
            // إلغاء المنبه باستخدام AppAlarmManager
            appAlarmManager.toggleAlarm(this, 1, false); // 1 هو ID الطفل
            alarmStatus.setText(getString(R.string.alarm_is_off));
        });
    }

    private void setupSoundControls() {
        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null) {
                    mediaPlayer.setVolume(progress / 100f, progress / 100f);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Initialize sound spinner with alarm sounds
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
            this, 
            R.array.alarm_sounds, 
            android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        soundSpinner.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }
}
