package com.example.sleepybaby;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddWakeTimeActivity extends AppCompatActivity {

    private TimePicker timePicker;
    private Button saveWakeTimeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wake_time);

        timePicker = findViewById(R.id.timePickerWake);
        saveWakeTimeButton = findViewById(R.id.saveWakeTimeButton);

        saveWakeTimeButton.setOnClickListener(v -> {
            int hour = timePicker.getHour();
            int minute = timePicker.getMinute();

            SharedPreferences sharedPreferences = getSharedPreferences("SleepData", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("wakeHour", hour);
            editor.putInt("wakeMinute", minute);
            editor.apply();

            String time = "Saat: " + hour + ":" + (minute < 10 ? "0" + minute : minute);
            Toast.makeText(this, "Zamanı Kaydedildi: " + time, Toast.LENGTH_SHORT).show();
        });
    }
}
