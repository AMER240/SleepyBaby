package com.example.sleepybaby;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddChildActivity extends AppCompatActivity {
    private static final String TAG = "AddChildActivity";
    
    private TextInputEditText editTextName;
    private TextInputEditText editTextBirthDate;
    private AutoCompleteTextView autoCompleteGender;
    private MaterialButton buttonSave;
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
    private Calendar selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_child);

        // Toolbar'ı ayarla
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Geri tuşu
        findViewById(R.id.buttonBack).setOnClickListener(v -> onBackPressed());

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
        editTextBirthDate = findViewById(R.id.editTextBirthDate);
        autoCompleteGender = findViewById(R.id.autoCompleteGender);
        buttonSave = findViewById(R.id.buttonSave);
        numberPickerBirthYear = findViewById(R.id.numberPickerBirthYear);
        radioGroupGender = findViewById(R.id.radioGroupGender);
        buttonSelectSleepTime = findViewById(R.id.buttonSelectSleepTime);
        buttonSelectWakeTime = findViewById(R.id.buttonSelectWakeTime);
        buttonSaveChild = findViewById(R.id.buttonSaveChild);
        
        databaseHelper = new DatabaseHelper(this);
        selectedDate = Calendar.getInstance();
        Log.d(TAG, "Views initialized successfully");

        // Cinsiyet seçeneklerini ayarla
        String[] genders = {"Erkek", "Kız"};
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, genders);
        autoCompleteGender.setAdapter(genderAdapter);

        // Doğum tarihi seçici
        editTextBirthDate.setOnClickListener(v -> showDatePicker());

        // Kaydet butonu
        buttonSave.setOnClickListener(v -> saveChild());
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
                
                // Doğum tarihi
                String birthDate = editTextBirthDate.getText().toString().trim();
                if (birthDate.isEmpty()) {
                    Toast.makeText(this, "Doğum tarihi gerekli", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                // Cinsiyet kontrolü
                String gender = autoCompleteGender.getText().toString().trim();
                if (gender.isEmpty()) {
                    Toast.makeText(this, "Lütfen cinsiyet seçiniz", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                Log.d(TAG, "Input values - Name: " + name + ", BirthDate: " + birthDate + 
                      ", Gender: " + gender + ", Sleep: " + sleepHour + ":" + sleepMinute + 
                      ", Wake: " + wakeHour + ":" + wakeMinute);
                
                // Veritabanına kaydet
                Log.d(TAG, "Attempting to add child to database...");
                boolean inserted = databaseHelper.addChild(name, birthDate, gender, 
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

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            (view, year, month, dayOfMonth) -> {
                selectedDate.set(year, month, dayOfMonth);
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                editTextBirthDate.setText(sdf.format(selectedDate.getTime()));
            },
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH),
            selectedDate.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void saveChild() {
        String name = editTextName.getText().toString().trim();
        String birthDate = editTextBirthDate.getText().toString().trim();
        String gender = autoCompleteGender.getText().toString().trim();

        if (name.isEmpty() || birthDate.isEmpty() || gender.isEmpty()) {
            Toast.makeText(this, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show();
            return;
        }

        Child child = new Child();
        child.setName(name);
        child.setBirthDate(selectedDate.getTimeInMillis());
        child.setGender(gender);

        long result = databaseHelper.addChild(child);
        if (result != -1) {
            Toast.makeText(this, "Çocuk başarıyla eklendi", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Çocuk eklenirken bir hata oluştu", Toast.LENGTH_SHORT).show();
        }
    }
}
