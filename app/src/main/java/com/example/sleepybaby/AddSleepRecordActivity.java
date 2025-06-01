package com.example.sleepybaby;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.slider.Slider;
import com.google.android.material.textfield.TextInputEditText;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddSleepRecordActivity extends AppCompatActivity {
    private static final String TAG = "AddSleepRecordActivity";
    
    private TextView textViewTitle;
    private MaterialButton buttonSleepStartTime;
    private MaterialButton buttonSleepEndTime;
    private Slider sliderSleepQuality;
    private TextView textViewSleepQuality;
    private TextInputEditText editTextNotes;
    private MaterialButton buttonSave;
    private DatabaseHelper dbHelper;
    private long childId;
    private String childName;
    private Calendar sleepStartTime;
    private Calendar sleepEndTime;

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
        childId = getIntent().getLongExtra("CHILD_ID", -1);
        childName = getIntent().getStringExtra("CHILD_NAME");

        if (childId == -1 || childName == null) {
            Toast.makeText(this, "Çocuk bilgileri alınamadı", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // View'ları initialize et
        initializeViews();
        dbHelper = new DatabaseHelper(this);

        // Başlığı ayarla
        textViewTitle.setText(childName + " - Uyku Kaydı Ekle");

        // Uyku başlangıç zamanı seçici
        buttonSleepStartTime.setOnClickListener(v -> showTimePicker(true));

        // Uyku bitiş zamanı seçici
        buttonSleepEndTime.setOnClickListener(v -> showTimePicker(false));

        // Uyku kalitesi slider'ı
        sliderSleepQuality.addOnChangeListener((slider, value, fromUser) -> {
            int quality = (int) value;
            String qualityText;
            switch (quality) {
                case 1:
                    qualityText = "Çok Kötü";
                    break;
                case 2:
                    qualityText = "Kötü";
                    break;
                case 3:
                    qualityText = "Orta";
                    break;
                case 4:
                    qualityText = "İyi";
                    break;
                case 5:
                    qualityText = "Çok İyi";
                    break;
                default:
                    qualityText = "Orta";
            }
            textViewSleepQuality.setText(qualityText);
        });

        // Kaydet butonu
        buttonSave.setOnClickListener(v -> saveSleepRecord());
    }
    
    private void initializeViews() {
        textViewTitle = findViewById(R.id.textViewTitle);
        buttonSleepStartTime = findViewById(R.id.buttonSleepStartTime);
        buttonSleepEndTime = findViewById(R.id.buttonSleepEndTime);
        sliderSleepQuality = findViewById(R.id.sliderSleepQuality);
        textViewSleepQuality = findViewById(R.id.textViewSleepQuality);
        editTextNotes = findViewById(R.id.editTextNotes);
        buttonSave = findViewById(R.id.buttonSave);
    }
    
    private void showTimePicker(boolean isStartTime) {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(
            this,
            (view, hourOfDay, minute) -> {
                if (isStartTime) {
                    sleepStartTime = Calendar.getInstance();
                    sleepStartTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    sleepStartTime.set(Calendar.MINUTE, minute);
                    buttonSleepStartTime.setText(String.format(Locale.getDefault(), 
                        "Başlangıç: %02d:%02d", hourOfDay, minute));
                } else {
                    sleepEndTime = Calendar.getInstance();
                    sleepEndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    sleepEndTime.set(Calendar.MINUTE, minute);
                    buttonSleepEndTime.setText(String.format(Locale.getDefault(), 
                        "Bitiş: %02d:%02d", hourOfDay, minute));
                }
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        );
        timePickerDialog.show();
    }
    
    private void saveSleepRecord() {
        if (sleepStartTime == null || sleepEndTime == null) {
            Toast.makeText(this, "Lütfen uyku başlangıç ve bitiş zamanlarını seçin", 
                Toast.LENGTH_SHORT).show();
            return;
        }

        if (sleepEndTime.before(sleepStartTime)) {
            Toast.makeText(this, "Bitiş zamanı başlangıç zamanından önce olamaz", 
                Toast.LENGTH_SHORT).show();
            return;
        }

        // Uyku süresini hesapla (dakika cinsinden)
        long sleepDurationMinutes = (sleepEndTime.getTimeInMillis() - 
            sleepStartTime.getTimeInMillis()) / (60 * 1000);

        // Uyku kaydını oluştur
        SleepRecord record = new SleepRecord();
        record.setChildId(childId);
        record.setStartTime(sleepStartTime.getTimeInMillis());
        record.setEndTime(sleepEndTime.getTimeInMillis());
        record.setDurationMinutes((int) sleepDurationMinutes);
        record.setQuality((int) sliderSleepQuality.getValue());
        record.setNotes(editTextNotes.getText().toString().trim());

        // Veritabanına kaydet
        long result = dbHelper.addSleepRecord(record);
        if (result != -1) {
            Toast.makeText(this, "Uyku kaydı başarıyla eklendi", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Uyku kaydı eklenirken bir hata oluştu", 
                Toast.LENGTH_SHORT).show();
        }
    }
} 