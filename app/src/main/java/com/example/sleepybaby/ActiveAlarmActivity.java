package com.example.sleepybaby;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;

public class ActiveAlarmActivity extends Activity {
    private static final String TAG = "ActiveAlarmActivity";
    private PowerManager.WakeLock wakeLock;
    private AlarmReceiver alarmReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "ActiveAlarmActivity onCreate started");

        // Ekranı açık tut
        getWindow().addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        );

        // Wake lock al
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(
            PowerManager.FULL_WAKE_LOCK |
            PowerManager.ACQUIRE_CAUSES_WAKEUP |
            PowerManager.ON_AFTER_RELEASE,
            "SleepyBaby:ActiveAlarmWakeLock"
        );
        wakeLock.acquire(30*60*1000L); // 30 minutes

        setContentView(R.layout.activity_active_alarm);

        // Alarm bilgilerini al
        String childName = getIntent().getStringExtra("CHILD_NAME");
        long alarmId = getIntent().getLongExtra("ALARM_ID", -1);

        // UI elemanlarını ayarla
        TextView textViewTitle = findViewById(R.id.textViewTitle);
        textViewTitle.setText(childName + " için Uyanma Zamanı!");

        Button buttonStop = findViewById(R.id.buttonStop);
        buttonStop.setOnClickListener(v -> {
            stopAlarm();
            finish();
        });

        // AlarmReceiver referansını al
        alarmReceiver = new AlarmReceiver();
    }

    private void stopAlarm() {
        if (alarmReceiver != null) {
            alarmReceiver.stopAlarm();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }
    }
} 