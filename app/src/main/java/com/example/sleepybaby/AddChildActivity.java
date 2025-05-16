package com.example.sleepybaby;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddChildActivity extends AppCompatActivity {

    EditText editTextName, editTextAge;
    TimePicker timePickerSleep, timePickerWake;
    Button buttonSaveChild;

    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_child);

        editTextName = findViewById(R.id.editTextName);
        editTextAge = findViewById(R.id.editTextAge);
        timePickerSleep = findViewById(R.id.timePickerSleep);
        timePickerWake = findViewById(R.id.timePickerWake);
        buttonSaveChild = findViewById(R.id.buttonSaveChild);

        databaseHelper = new DatabaseHelper(this);

        timePickerSleep.setIs24HourView(true);
        timePickerWake.setIs24HourView(true);

        buttonSaveChild.setOnClickListener(v -> {
            String name = editTextName.getText().toString().trim();
            String ageStr = editTextAge.getText().toString().trim();

            if (name.isEmpty()) {
                editTextName.setError("İsim gerekli");
                return;
            }
            if (ageStr.isEmpty()) {
                editTextAge.setError("Yaş gerekli");
                return;
            }

            int age = Integer.parseInt(ageStr);
            int sleepHour = timePickerSleep.getHour();
            int sleepMinute = timePickerSleep.getMinute();
            int wakeHour = timePickerWake.getHour();
            int wakeMinute = timePickerWake.getMinute();

            boolean inserted = databaseHelper.addChild(name, age, sleepHour, sleepMinute, wakeHour, wakeMinute);
            if (inserted) {
                Toast.makeText(this, "Çocuk başarıyla eklendi!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Ekleme sırasında hata oluştu.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
