package com.example.sleepybaby;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
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
    private int selectedQuality = 3;

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

        // Kalite seçimi
        setupQualitySelection();
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
        record.setQuality(selectedQuality);
        
        // Notları al
        String notes = ((TextInputEditText) findViewById(R.id.editTextNotes)).getText().toString();
        record.setNotes(notes);

        long result = databaseHelper.addSleepRecord(record);
        if (result != -1) {
            Toast.makeText(this, "Uyku kaydı eklendi", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "Uyku kaydı eklenirken hata oluştu", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupQualitySelection() {
        // Kalite seçim butonlarını oluştur
        LinearLayout qualityLayout = findViewById(R.id.qualityLayout);
        qualityLayout.removeAllViews(); // Mevcut butonları temizle
        
        // Ekran genişliğini al
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int buttonWidth = (screenWidth - 48) / 5; // 5 buton için genişlik hesapla (kenar boşlukları dahil)
        
        // Butonlar için parametreler
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            buttonWidth,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(4, 0, 4, 0); // Butonlar arası boşluk
        
        // Her kalite seviyesi için buton oluştur
        for (int i = 1; i <= 5; i++) {
            MaterialButton button = new MaterialButton(this);
            button.setText(String.valueOf(i));
            button.setLayoutParams(params);
            button.setHeight(48); // Yükseklik
            button.setPadding(0, 0, 0, 0); // İç boşluğu kaldır
            button.setTextSize(16); // Yazı boyutu
            button.setCornerRadius(24); // Yuvarlak köşeler
            button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.purple_200)));
            button.setTextColor(ContextCompat.getColor(this, android.R.color.black));
            
            final int quality = i;
            button.setOnClickListener(v -> {
                selectedQuality = quality;
                // Tüm butonların görünümünü sıfırla
                for (int j = 0; j < qualityLayout.getChildCount(); j++) {
                    View child = qualityLayout.getChildAt(j);
                    if (child instanceof MaterialButton) {
                        MaterialButton btn = (MaterialButton) child;
                        btn.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.purple_200)));
                        btn.setTextColor(ContextCompat.getColor(this, android.R.color.black));
                    }
                }
                // Seçilen butonu vurgula
                button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.purple_500)));
                button.setTextColor(ContextCompat.getColor(this, android.R.color.white));
            });
            
            qualityLayout.addView(button);
        }
        
        // Varsayılan olarak orta kaliteyi seç (3)
        if (qualityLayout.getChildCount() >= 3) {
            MaterialButton defaultButton = (MaterialButton) qualityLayout.getChildAt(2);
            defaultButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.purple_500)));
            defaultButton.setTextColor(ContextCompat.getColor(this, android.R.color.white));
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