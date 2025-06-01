package com.example.sleepybaby;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.slider.Slider;
import com.google.android.material.textfield.TextInputEditText;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddSleepRecordActivity extends AppCompatActivity {
    private static final String TAG = "AddSleepRecordActivity";

    private Button buttonSelectDate;
    private Button buttonSelectSleepTime;
    private Button buttonSelectWakeTime;
    private Button buttonSave;
    private ImageButton buttonBack;
    private Slider sliderSleepQuality;
    private EditText editTextNotes;
    private DatabaseHelper databaseHelper;
    private Calendar selectedDate;
    private Calendar sleepStartTime;
    private Calendar sleepEndTime;
    private SimpleDateFormat dateFormat;
    private SimpleDateFormat timeFormat;
    private int childId;
    private String childName;

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
        buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(v -> finish());

        // Intent'ten verileri al
        childId = getIntent().getIntExtra("CHILD_ID", -1);
        childName = getIntent().getStringExtra("CHILD_NAME");

        if (childId == -1 || childName == null) {
            Toast.makeText(this, "Çocuk bilgileri alınamadı", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        databaseHelper = new DatabaseHelper(this);
        selectedDate = Calendar.getInstance();
        sleepStartTime = Calendar.getInstance();
        sleepEndTime = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        initializeViews();
        setupListeners();

        // Başlığı ayarla
        buttonSelectDate.setText(dateFormat.format(selectedDate.getTime()));
        buttonSelectSleepTime.setText(timeFormat.format(sleepStartTime.getTime()));
        buttonSelectWakeTime.setText(timeFormat.format(sleepEndTime.getTime()));

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
            editTextNotes.setText(qualityText);
        });
    }
    
    private void initializeViews() {
        buttonSelectDate = findViewById(R.id.buttonSelectDate);
        buttonSelectSleepTime = findViewById(R.id.buttonSelectSleepTime);
        buttonSelectWakeTime = findViewById(R.id.buttonSelectWakeTime);
        buttonSave = findViewById(R.id.buttonSave);
        sliderSleepQuality = findViewById(R.id.sliderSleepQuality);
        editTextNotes = findViewById(R.id.editTextNotes);
    }
    
    private void setupListeners() {
        buttonSave.setOnClickListener(v -> saveSleepRecord());

        buttonSelectDate.setOnClickListener(v -> showDatePicker());
        buttonSelectSleepTime.setOnClickListener(v -> showTimePicker(true));
        buttonSelectWakeTime.setOnClickListener(v -> showTimePicker(false));
    }
    
    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedDate.set(year, month, dayOfMonth);
                    buttonSelectDate.setText(dateFormat.format(selectedDate.getTime()));
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void showTimePicker(boolean isStartTime) {
        Calendar calendar = isStartTime ? sleepStartTime : sleepEndTime;
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);
                    Button button = isStartTime ? buttonSelectSleepTime : buttonSelectWakeTime;
                    button.setText(timeFormat.format(calendar.getTime()));
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
        );
        timePickerDialog.show();
    }
    
    private void saveSleepRecord() {
        if (sleepEndTime.before(sleepStartTime)) {
            Toast.makeText(this, "Uyanma zamanı uyku zamanından önce olamaz", Toast.LENGTH_SHORT).show();
            return;
        }

        SleepRecord record = new SleepRecord();
        record.setChildId(childId);
        record.setStartTime(sleepStartTime.getTime());
        record.setEndTime(sleepEndTime.getTime());
        record.setQuality((int) sliderSleepQuality.getValue());
        record.setNotes(editTextNotes.getText().toString().trim());

        long result = databaseHelper.addSleepRecord(record);
        if (result != -1) {
            Toast.makeText(this, "Uyku kaydı başarıyla eklendi", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Uyku kaydı eklenirken bir hata oluştu", Toast.LENGTH_SHORT).show();
        }
    }
} 