package com.example.sleepybaby;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;

public class AddChildActivity extends AppCompatActivity {
    private static final String TAG = "AddChildActivity";
    
    private EditText editTextName;
    private NumberPicker numberPickerBirthYear;
    private RadioGroup radioGroupGender;
    private Button buttonSelectSleepTime;
    private Button buttonSelectWakeTime;
    private Button buttonSaveChild;
    
    private int sleepHour = 21; // Varsayılan uyku saati
    private int sleepMinute = 0;
    private int wakeHour = 7; // Varsayılan uyanma saati
    private int wakeMinute = 0;
    
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_child);

        try {
            Log.d(TAG, "Initializing views...");
            initializeViews();
            setupNumberPicker();
            setupTimeButtons();
            setupSaveButton();
            
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, "Uygulama başlatılırken hata oluştu: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }
    
    private void initializeViews() {
        editTextName = findViewById(R.id.editTextName);
        numberPickerBirthYear = findViewById(R.id.numberPickerBirthYear);
        radioGroupGender = findViewById(R.id.radioGroupGender);
        buttonSelectSleepTime = findViewById(R.id.buttonSelectSleepTime);
        buttonSelectWakeTime = findViewById(R.id.buttonSelectWakeTime);
        buttonSaveChild = findViewById(R.id.buttonSaveChild);
        
        databaseHelper = new DatabaseHelper(this);
        Log.d(TAG, "Views initialized successfully");
    }
    
    private void setupNumberPicker() {
        // Doğum yılı için NumberPicker ayarları
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        numberPickerBirthYear.setMinValue(currentYear - 18); // 18 yaş üstü
        numberPickerBirthYear.setMaxValue(currentYear);
        numberPickerBirthYear.setValue(currentYear - 2); // Varsayılan 2 yaşında
        numberPickerBirthYear.setWrapSelectorWheel(false);
    }
    
    private void setupTimeButtons() {
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
        
        // Varsayılan değerleri göster
        buttonSelectSleepTime.setText(String.format("Uyku: %02d:%02d", sleepHour, sleepMinute));
        buttonSelectWakeTime.setText(String.format("Uyanma: %02d:%02d", wakeHour, wakeMinute));
    }
    
    private void setupSaveButton() {
        buttonSaveChild.setOnClickListener(v -> {
            try {
                Log.d(TAG, "Save button clicked");
                
                // İsim kontrolü
                String name = editTextName.getText().toString().trim();
                if (name.isEmpty()) {
                    editTextName.setError("İsim gerekli");
                    editTextName.requestFocus();
                    return;
                }
                
                // Doğum yılı
                long birthYear = numberPickerBirthYear.getValue();
                
                // Cinsiyet kontrolü
                int selectedGenderId = radioGroupGender.getCheckedRadioButtonId();
                if (selectedGenderId == -1) {
                    Toast.makeText(this, "Lütfen cinsiyet seçiniz", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                String gender = (selectedGenderId == R.id.radioButtonMale) ? "Erkek" : "Kız";
                
                Log.d(TAG, "Input values - Name: " + name + ", BirthYear: " + birthYear + 
                      ", Gender: " + gender + ", Sleep: " + sleepHour + ":" + sleepMinute + 
                      ", Wake: " + wakeHour + ":" + wakeMinute);
                
                // Veritabanına kaydet
                Log.d(TAG, "Attempting to add child to database...");
                boolean inserted = databaseHelper.addChild(name, birthYear, gender, 
                                                          sleepHour, sleepMinute, wakeHour, wakeMinute);
                
                if (inserted) {
                    Log.d(TAG, "Child added successfully");
                    Toast.makeText(this, "Çocuk başarıyla eklendi!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Log.e(TAG, "Failed to add child");
                    Toast.makeText(this, "Ekleme sırasında hata oluştu.", Toast.LENGTH_SHORT).show();
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Error in save button click: " + e.getMessage());
                e.printStackTrace();
                Toast.makeText(this, "Bir hata oluştu: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
