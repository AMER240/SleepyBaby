package com.example.sleepybaby;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.view.WindowManager;
import android.os.Build;

public class ActiveAlarmActivity extends AppCompatActivity {
    private TextView textViewAlarmTime;
    private TextView textViewChildName;
    private Button buttonStopAlarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Set window flags
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
            getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
            );
        } else {
            getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
            );
        }
        
        setContentView(R.layout.activity_active_alarm);

        // تهيئة العناصر
        textViewAlarmTime = findViewById(R.id.textViewAlarmTime);
        textViewChildName = findViewById(R.id.textViewChildName);
        buttonStopAlarm = findViewById(R.id.buttonStopAlarm);

        // عرض الوقت الحالي
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        textViewAlarmTime.setText(sdf.format(new Date()));

        // عرض اسم الطفل
        String childName = getIntent().getStringExtra("CHILD_NAME");
        textViewChildName.setText("حان وقت استيقاظ " + childName);

        // إعداد زر إيقاف المنبه
        buttonStopAlarm.setOnClickListener(v -> {
            // إيقاف المنبه
            AlarmReceiver receiver = new AlarmReceiver();
            receiver.stopAlarm();
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        // تعطيل زر الرجوع
    }
} 