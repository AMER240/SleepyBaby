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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddSleepRecordActivity extends AppCompatActivity {
    private static final String TAG = "AddSleepRecordActivity";
    
    private TextView textViewSelectedChild;
    private TextView textViewSleepDate;
    private Button buttonSelectChild;
    private Button buttonSelectDate;
    private Button buttonSelectSleepTime;
    private Button buttonSelectWakeTime;
    private EditText editTextQuality;
    private Button buttonSaveRecord;
    
    private DatabaseHelper databaseHelper;
    private Child selectedChild;
    private Calendar selectedDate;
    private int sleepHour = 21, sleepMinute = 0;
    private int wakeHour = 7, wakeMinute = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sleep_record);

        try {
            // ActionBar ayarla
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Uyku Kaydı Ekle");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

            initializeViews();
            setupButtons();
            
            selectedDate = Calendar.getInstance();
            updateDateDisplay();
            
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, "Uygulama başlatılırken hata oluştu", Toast.LENGTH_LONG).show();
        }
    }
    
    private void initializeViews() {
        textViewSelectedChild = findViewById(R.id.textViewSelectedChild);
        textViewSleepDate = findViewById(R.id.textViewSleepDate);
        buttonSelectChild = findViewById(R.id.buttonSelectChild);
        buttonSelectDate = findViewById(R.id.buttonSelectDate);
        buttonSelectSleepTime = findViewById(R.id.buttonSelectSleepTime);
        buttonSelectWakeTime = findViewById(R.id.buttonSelectWakeTime);
        editTextQuality = findViewById(R.id.editTextQuality);
        buttonSaveRecord = findViewById(R.id.buttonSaveRecord);
        
        databaseHelper = new DatabaseHelper(this);
    }
    
    private void setupButtons() {
        // Çocuk seçimi
        buttonSelectChild.setOnClickListener(v -> showChildSelectionDialog());
        
        // Tarih seçimi
        buttonSelectDate.setOnClickListener(v -> showDatePicker());
        
        // Uyku saati seçimi
        buttonSelectSleepTime.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    sleepHour = hourOfDay;
                    sleepMinute = minute;
                    buttonSelectSleepTime.setText(String.format("Uyku: %02d:%02d", sleepHour, sleepMinute));
                },
                sleepHour,
                sleepMinute,
                true
            );
            timePickerDialog.show();
        });
        
        // Uyanma saati seçimi
        buttonSelectWakeTime.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    wakeHour = hourOfDay;
                    wakeMinute = minute;
                    buttonSelectWakeTime.setText(String.format("Uyanma: %02d:%02d", wakeHour, wakeMinute));
                },
                wakeHour,
                wakeMinute,
                true
            );
            timePickerDialog.show();
        });
        
        // Kaydet butonu
        buttonSaveRecord.setOnClickListener(v -> saveSleepRecord());
        
        // Varsayılan değerleri göster
        buttonSelectSleepTime.setText(String.format("Uyku: %02d:%02d", sleepHour, sleepMinute));
        buttonSelectWakeTime.setText(String.format("Uyanma: %02d:%02d", wakeHour, wakeMinute));
    }
    
    private void showChildSelectionDialog() {
        List<Child> children = databaseHelper.getAllChildren();
        if (children.isEmpty()) {
            Toast.makeText(this, "Önce bir çocuk eklemelisiniz", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String[] childNames = new String[children.size()];
        for (int i = 0; i < children.size(); i++) {
            childNames[i] = children.get(i).getName();
        }
        
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Çocuk Seçin");
        builder.setItems(childNames, (dialog, which) -> {
            selectedChild = children.get(which);
            textViewSelectedChild.setText("Seçilen Çocuk: " + selectedChild.getName());
            buttonSelectChild.setText(selectedChild.getName());
        });
        builder.show();
    }
    
    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            (view, year, month, dayOfMonth) -> {
                selectedDate.set(year, month, dayOfMonth);
                updateDateDisplay();
            },
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH),
            selectedDate.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }
    
    private void updateDateDisplay() {
        String dateStr = String.format("%02d/%02d/%d",
                selectedDate.get(Calendar.DAY_OF_MONTH),
                selectedDate.get(Calendar.MONTH) + 1,
                selectedDate.get(Calendar.YEAR));
        textViewSleepDate.setText("Tarih: " + dateStr);
        buttonSelectDate.setText(dateStr);
    }
    
    private void saveSleepRecord() {
        try {
            if (selectedChild == null) {
                Toast.makeText(this, "Lütfen bir çocuk seçin", Toast.LENGTH_SHORT).show();
                return;
            }
            
            String qualityStr = editTextQuality.getText().toString().trim();
            if (qualityStr.isEmpty()) {
                Toast.makeText(this, "Lütfen uyku kalitesi girin (1-5)", Toast.LENGTH_SHORT).show();
                return;
            }
            
            int quality = Integer.parseInt(qualityStr);
            if (quality < 1 || quality > 5) {
                Toast.makeText(this, "Uyku kalitesi 1-5 arasında olmalıdır", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Uyku ve uyanma zamanlarını hesapla
            Calendar sleepTime = (Calendar) selectedDate.clone();
            sleepTime.set(Calendar.HOUR_OF_DAY, sleepHour);
            sleepTime.set(Calendar.MINUTE, sleepMinute);
            
            Calendar wakeTime = (Calendar) selectedDate.clone();
            wakeTime.set(Calendar.HOUR_OF_DAY, wakeHour);
            wakeTime.set(Calendar.MINUTE, wakeMinute);
            
            // Eğer uyanma saati uyku saatinden küçükse, ertesi güne geç
            if (wakeTime.before(sleepTime)) {
                wakeTime.add(Calendar.DAY_OF_MONTH, 1);
            }
            
            SleepRecord record = new SleepRecord(
                0, // ID otomatik atanacak
                selectedChild.getId(),
                sleepTime.getTime(),
                wakeTime.getTime(),
                quality
            );
            
            long result = databaseHelper.addSleepRecord(record);
            if (result != -1) {
                Toast.makeText(this, "Uyku kaydı başarıyla eklendi!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Uyku kaydı eklenirken hata oluştu", Toast.LENGTH_SHORT).show();
            }
            
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Geçerli bir kalite değeri girin", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Error saving sleep record: " + e.getMessage());
            Toast.makeText(this, "Bir hata oluştu: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 