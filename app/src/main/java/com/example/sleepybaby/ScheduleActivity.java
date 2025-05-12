package com.example.sleepybaby;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ScheduleActivity extends AppCompatActivity {

    TextView sleepText, wakeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        sleepText = findViewById(R.id.sleepTimeTextView);
        wakeText = findViewById(R.id.wakeTimeTextView);

        SharedPreferences sharedPreferences = getSharedPreferences("SleepData", MODE_PRIVATE);

        int sleepHour = sharedPreferences.getInt("sleepHour", -1);
        int sleepMinute = sharedPreferences.getInt("sleepMinute", -1);
        int wakeHour = sharedPreferences.getInt("wakeHour", -1);
        int wakeMinute = sharedPreferences.getInt("wakeMinute", -1);

        if (sleepHour != -1 && sleepMinute != -1) {
            sleepText.setText("Uyku zamanı: " + sleepHour + ":" + String.format("%02d", sleepMinute));
        } else {
            sleepText.setText("Uyku zamanı: Kaydedilmedi");
        }

        if (wakeHour != -1 && wakeMinute != -1) {
            wakeText.setText("Uyanış zamanı: " + wakeHour + ":" + String.format("%02d", wakeMinute));
        } else {
            wakeText.setText("Uyanış zamanı: Kaydedilmedi");
        }
    }
}
