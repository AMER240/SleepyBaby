package com.example.sleepybaby;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;
import android.app.KeyguardManager;
import android.view.View;
import android.os.Handler;
import android.os.Looper;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ActiveAlarmActivity extends Activity {
    private static final String TAG = "ActiveAlarmActivity";
    private PowerManager.WakeLock wakeLock;
    private Handler handler;
    private TextView textViewTime;
    private boolean isAlarmActive = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "ActiveAlarmActivity onCreate started");

        // Ekranı açık tut
        getWindow().addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
            WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
        );

        // Ekran kilidini aç
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        if (keyguardManager != null && keyguardManager.isKeyguardLocked()) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        }

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

        textViewTime = findViewById(R.id.textViewTime);
        handler = new Handler(Looper.getMainLooper());
        startTimeUpdate();

        Button buttonStop = findViewById(R.id.buttonStop);
        buttonStop.setOnClickListener(v -> {
            if (isAlarmActive) {
                stopAlarm();
                isAlarmActive = false;
                finish();
            }
        });

        // Tam ekran modu
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_FULLSCREEN |
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }

    private void startTimeUpdate() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (isAlarmActive) {
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                    textViewTime.setText(sdf.format(new Date()));
                    handler.postDelayed(this, 1000);
                }
            }
        });
    }

    private void stopAlarm() {
        AlarmReceiver.stopAlarm();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isAlarmActive = false;
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }
    }

    @Override
    public void onBackPressed() {
        // Geri tuşunu devre dışı bırak
    }
} 