package com.example.sleepybaby;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddChildActivity extends AppCompatActivity {
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
            editTextName = findViewById(R.id.editTextName);
            editTextBirthYear = findViewById(R.id.editTextBirthYear);
            editTextGender = findViewById(R.id.editTextGender);
            timePickerSleep = findViewById(R.id.timePickerSleep);
            timePickerWake = findViewById(R.id.timePickerWake);
            buttonSaveChild = findViewById(R.id.buttonSaveChild);

            databaseHelper = new DatabaseHelper(this);

            timePickerSleep.setIs24HourView(true);
            timePickerWake.setIs24HourView(true);

            buttonSaveChild.setOnClickListener(v -> {
                try {
                    String name = editTextName.getText().toString().trim();
                    String birthYearStr = editTextBirthYear.getText().toString().trim();
                    String gender = editTextGender.getText().toString().trim();

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
                        editTextBirthYear.setError("Geçerli bir yıl giriniz");
                        return;
                    }

                    int sleepHour = timePickerSleep.getHour();
                    int sleepMinute = timePickerSleep.getMinute();
                    int wakeHour = timePickerWake.getHour();
                    int wakeMinute = timePickerWake.getMinute();

                    boolean inserted = databaseHelper.addChild(name, birthYear, gender, sleepHour, sleepMinute, wakeHour, wakeMinute);
                    if (inserted) {
                        Toast.makeText(this, "Çocuk başarıyla eklendi!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Ekleme sırasında hata oluştu.", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(this, "Bir hata oluştu: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "Uygulama başlatılırken hata oluştu: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }
}
