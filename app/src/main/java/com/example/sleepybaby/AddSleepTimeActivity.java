package com.example.sleepybaby;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddSleepTimeActivity extends AppCompatActivity {

    private TimePicker timePicker;
    private Button saveTimeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sleep_time);

        timePicker = findViewById(R.id.timePicker);
        saveTimeButton = findViewById(R.id.saveTimeButton);

        saveTimeButton.setOnClickListener(view -> {
            int hour = timePicker.getHour();
            int minute = timePicker.getMinute();

            SharedPreferences sharedPreferences = getSharedPreferences("SleepData", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("sleepHour", hour);
            editor.putInt("sleepMinute", minute);
            editor.apply();

            String time = "Saat: " + hour + ":" + (minute < 10 ? "0" + minute : minute);
            Toast.makeText(AddSleepTimeActivity.this, "Zaman Kaydedildi: " + time, Toast.LENGTH_SHORT).show();

        });
    }
}
