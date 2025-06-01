package com.example.sleepybaby;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddSleepRecordActivity extends AppCompatActivity {
    private int childId;
    private String childName;
    private DatabaseHelper databaseHelper;
    private Button buttonSleepTime;
    private Button buttonWakeTime;
    private int sleepHour = -1;
    private int sleepMinute = -1;
    private int wakeHour = -1;
    private int wakeMinute = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sleep_record);

        // Toolbar'ı ayarla
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Geri tuşu
        findViewById(R.id.buttonBack).setOnClickListener(v -> onBackPressed());

        // Intent'ten verileri al
        childId = getIntent().getIntExtra("child_id", -1);
        childName = getIntent().getStringExtra("child_name");

        if (childId == -1 || childName == null) {
            Toast.makeText(this, "Geçersiz çocuk bilgisi", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // View'ları initialize et
        buttonSleepTime = findViewById(R.id.buttonSleepTime);
        buttonWakeTime = findViewById(R.id.buttonWakeTime);
        Button buttonSave = findViewById(R.id.buttonSave);

        databaseHelper = new DatabaseHelper(this);

        // Uyku saati seçici
        buttonSleepTime.setOnClickListener(v -> showTimePicker(true));

        // Uyanma saati seçici
        buttonWakeTime.setOnClickListener(v -> showTimePicker(false));

        // Kaydet butonu
        buttonSave.setOnClickListener(v -> saveSleepRecord());
    }

    private void showTimePicker(boolean isSleepTime) {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(
            this,
            (view, hourOfDay, minute) -> {
                if (isSleepTime) {
                    sleepHour = hourOfDay;
                    sleepMinute = minute;
                    buttonSleepTime.setText(String.format(Locale.getDefault(), 
                        "Uyku Saati: %02d:%02d", hourOfDay, minute));
                } else {
                    wakeHour = hourOfDay;
                    wakeMinute = minute;
                    buttonWakeTime.setText(String.format(Locale.getDefault(), 
                        "Uyanma Saati: %02d:%02d", hourOfDay, minute));
                }
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        );
        timePickerDialog.show();
    }

    private void saveSleepRecord() {
        if (sleepHour == -1 || sleepMinute == -1 || wakeHour == -1 || wakeMinute == -1) {
            Toast.makeText(this, "Lütfen uyku ve uyanma saatlerini seçin", Toast.LENGTH_SHORT).show();
            return;
        }

        SleepRecord record = new SleepRecord();
        record.setChildId(childId);
        record.setSleepTime(new Date());
        record.setSleepHour(sleepHour);
        record.setSleepMinute(sleepMinute);
        record.setWakeHour(wakeHour);
        record.setWakeMinute(wakeMinute);

        long result = databaseHelper.addSleepRecord(record);
        if (result != -1) {
            Toast.makeText(this, "Uyku kaydı eklendi", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Uyku kaydı eklenirken hata oluştu", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 