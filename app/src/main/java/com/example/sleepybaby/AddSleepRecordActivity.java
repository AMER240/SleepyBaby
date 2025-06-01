package com.example.sleepybaby;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.button.MaterialButton;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddSleepRecordActivity extends AppCompatActivity {
    private int childId;
    private String childName;
    private DatabaseHelper databaseHelper;
    private MaterialButton buttonSelectDate;
    private MaterialButton buttonSleepTime;
    private MaterialButton buttonWakeTime;
    private Calendar selectedDate;
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
        initializeViews();
        databaseHelper = new DatabaseHelper(this);

        // Başlığı ayarla
        TextView titleTextView = toolbar.findViewById(R.id.textViewTitle);
        if (titleTextView != null) {
            titleTextView.setText(childName + " için Uyku Kaydı Ekle");
        }

        // Tarih seçimi
        selectedDate = Calendar.getInstance();
        buttonSelectDate.setOnClickListener(v -> showDatePicker());

        // Uyku saati seçimi
        buttonSleepTime.setOnClickListener(v -> showTimePicker(true));

        // Uyanma saati seçimi
        buttonWakeTime.setOnClickListener(v -> showTimePicker(false));

        // Kaydet butonu
        findViewById(R.id.buttonSave).setOnClickListener(v -> saveSleepRecord());
    }

    private void initializeViews() {
        buttonSelectDate = findViewById(R.id.buttonSelectDate);
        buttonSleepTime = findViewById(R.id.buttonSleepTime);
        buttonWakeTime = findViewById(R.id.buttonWakeTime);
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            (view, year, month, dayOfMonth) -> {
                selectedDate.set(year, month, dayOfMonth);
                buttonSelectDate.setText(String.format(Locale.getDefault(), 
                    "Seçilen Tarih: %02d/%02d/%d", dayOfMonth, month + 1, year));
            },
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH),
            selectedDate.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
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
        if (sleepHour == -1 || sleepMinute == -1) {
            Toast.makeText(this, "Lütfen uyku saatini seçin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (wakeHour == -1 || wakeMinute == -1) {
            Toast.makeText(this, "Lütfen uyanma saatini seçin", Toast.LENGTH_SHORT).show();
            return;
        }

        // Seçilen tarih ve saatleri birleştir
        Calendar sleepCalendar = (Calendar) selectedDate.clone();
        sleepCalendar.set(Calendar.HOUR_OF_DAY, sleepHour);
        sleepCalendar.set(Calendar.MINUTE, sleepMinute);
        sleepCalendar.set(Calendar.SECOND, 0);

        Calendar wakeCalendar = (Calendar) selectedDate.clone();
        wakeCalendar.set(Calendar.HOUR_OF_DAY, wakeHour);
        wakeCalendar.set(Calendar.MINUTE, wakeMinute);
        wakeCalendar.set(Calendar.SECOND, 0);

        // Eğer uyanma saati uyku saatinden önceyse, ertesi güne geç
        if (wakeCalendar.before(sleepCalendar)) {
            wakeCalendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        SleepRecord record = new SleepRecord();
        record.setChildId(childId);
        record.setSleepTime(sleepCalendar.getTime());
        record.setSleepHour(sleepHour);
        record.setSleepMinute(sleepMinute);
        record.setWakeHour(wakeHour);
        record.setWakeMinute(wakeMinute);

        long result = databaseHelper.addSleepRecord(record);
        if (result != -1) {
            Toast.makeText(this, "Uyku kaydı eklendi", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
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