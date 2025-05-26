package com.example.sleepybaby;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class AddChildActivity extends AppCompatActivity {
    private static final String TAG = "AddChildActivity";
    EditText editTextName;
    EditText editTextBirthYear;
    EditText editTextGender;
    TimePicker timePickerSleep;
    TimePicker timePickerWake;
    Button buttonSaveChild;

    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_child);

        try {
            Log.d(TAG, "Initializing views...");
            editTextName = findViewById(R.id.editTextName);
            editTextBirthYear = findViewById(R.id.editTextBirthYear);
            editTextGender = findViewById(R.id.editTextGender);
            timePickerSleep = findViewById(R.id.timePickerSleep);
            timePickerWake = findViewById(R.id.timePickerWake);
            buttonSaveChild = findViewById(R.id.buttonSaveChild);

            databaseHelper = new DatabaseHelper(this);
            Log.d(TAG, "DatabaseHelper initialized");

            timePickerSleep.setIs24HourView(true);
            timePickerWake.setIs24HourView(true);

            buttonSaveChild.setOnClickListener(v -> {
                try {
                    Log.d(TAG, "Save button clicked");
                    String name = editTextName.getText().toString().trim();
                    String birthYearStr = editTextBirthYear.getText().toString().trim();
                    String gender = editTextGender.getText().toString().trim();

                    Log.d(TAG, "Input values - Name: " + name + ", BirthYear: " + birthYearStr + ", Gender: " + gender);

                    // Veri doğrulama
                    if (name.isEmpty()) {
                        editTextName.setError("İsim gerekli");
                        return;
                    }
                    if (birthYearStr.isEmpty()) {
                        editTextBirthYear.setError("Doğum yılı gerekli");
                        return;
                    }
                    if (gender.isEmpty()) {
                        editTextGender.setError("Cinsiyet gerekli");
                        return;
                    }

                    // Doğum yılı kontrolü
                    long birthYear;
                    try {
                        birthYear = Long.parseLong(birthYearStr);
                        int currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
                        if (birthYear < 1900 || birthYear > currentYear) {
                            editTextBirthYear.setError("Geçerli bir doğum yılı giriniz");
                            return;
                        }
                    } catch (NumberFormatException e) {
                        Log.e(TAG, "Invalid birth year format: " + birthYearStr);
                        editTextBirthYear.setError("Geçerli bir yıl giriniz");
                        return;
                    }

                    int sleepHour = timePickerSleep.getHour();
                    int sleepMinute = timePickerSleep.getMinute();
                    int wakeHour = timePickerWake.getHour();
                    int wakeMinute = timePickerWake.getMinute();

                    Log.d(TAG, "Attempting to add child to database...");
                    boolean inserted = databaseHelper.addChild(name, birthYear, gender, sleepHour, sleepMinute, wakeHour, wakeMinute);
                    
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
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, "Uygulama başlatılırken hata oluştu: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }
}
